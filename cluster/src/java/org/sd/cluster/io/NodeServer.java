/*
    Copyright 2009 Semantic Discovery, Inc. (www.semanticdiscovery.com)

    This file is part of the Semantic Discovery Toolkit.

    The Semantic Discovery Toolkit is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The Semantic Discovery Toolkit is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with The Semantic Discovery Toolkit.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.sd.cluster.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Node server.
 * <p>
 * Multithreaded management of sockets and incoming messages from a NodeClient over a port.
 * <p>
 * Starts the server thread to fill the queue and message handler threads by
 * polling the message queue.
 *
 * @author Spence Koehler
 */
public class NodeServer extends Thread {

  /**
   * Identifying prefix string for instances.
   */
  public static final String PREFIX_STRING = "NodeServer";

  /**
   * Maximum shutdown latency, or time between shutting down the server and its thread dying. In millis.
   */
  public static final int SHUTDOWN_LATENCY = 1000;


  private InetSocketAddress mySocketAddress;

  private final Context context;
  private int serverId;
  private String nodeName;
  private LinkedBlockingQueue<Message> messageQueue;
  private ExecutorService serverThread;         // waits for socket connections
  private ExecutorService socketPool;           // performs socket I/O
  private ExecutorService messageQueueThread;   // polls message queue for messages
  private ExecutorService messageHandlerPool;   // handles messages

  private static final AtomicInteger nextServerId = new AtomicInteger(0);
  private final AtomicBoolean stayAlive = new AtomicBoolean(true);
  private final AtomicInteger socketThreadId = new AtomicInteger(0);
  private final AtomicInteger messageHandlerThreadId = new AtomicInteger(0);

  /**
   * Construct a node server.
   *
   * @param context                   The context for this server.
   * @param mySocketAddress           The server's socket address for receiving connections.
   * @param numSocketThreads          Number of threads to pool for managing sockets.
   * @param numMessageHandlerThreads  Number of threads to handle messages.
   */
  public NodeServer(Context context, InetSocketAddress mySocketAddress, int numSocketThreads, int numMessageHandlerThreads) {
    super(NodeUtil.buildNodeName(PREFIX_STRING, context.getName(), mySocketAddress.toString(), nextServerId.get()));
    this.context = context;
    this.serverId = nextServerId.getAndIncrement();
    this.nodeName = NodeUtil.buildNodeName(PREFIX_STRING, context.getName(), mySocketAddress.toString(), serverId);
    this.mySocketAddress = mySocketAddress;

    this.messageQueue = new LinkedBlockingQueue<Message>();
    this.serverThread
      = Executors.newSingleThreadExecutor(
        new ThreadFactory() {
          public Thread newThread(Runnable r) {
            return new Thread(r, nodeName + "-ServerThread");
          }
        });
    this.socketPool
      = Executors.newFixedThreadPool(
        numSocketThreads,
        new ThreadFactory() {
          public Thread newThread(Runnable r) {
            return new Thread(r, nodeName + "-Socket-" + socketThreadId.getAndIncrement());
          }
        });
//    ((ThreadPoolExecutor)this.socketPool).setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    this.messageQueueThread
      = Executors.newSingleThreadExecutor(
        new ThreadFactory() {
          public Thread newThread(Runnable r) {
            return new Thread(r, nodeName + "-MessageQueueThread");
          }
        });
    this.messageHandlerPool
      = Executors.newFixedThreadPool(
        numMessageHandlerThreads,
        new ThreadFactory() {
          public Thread newThread(Runnable r) {
            return new Thread(r, nodeName + "-MessageHandler-" + messageHandlerThreadId.getAndIncrement());
          }
        });
  }

  public boolean isUp() {
    return stayAlive.get();
  }

  public void run() {
//    System.out.println("Starting server: " + nodeName);

    ServerSocket serverSocket = null;

    try {
      serverSocket = new ServerSocket(mySocketAddress.getPort());
      serverSocket.setSoTimeout(SHUTDOWN_LATENCY);  // stop waiting to accept periodically
    }
    catch (IOException e) {
      //todo: determine what to do here... can't listen on the port!
      System.err.println("mySocketAddress=" + mySocketAddress);
      e.printStackTrace(System.err);
    }

    // start the socket listener
    if (stayAlive.get()) serverThread.execute(new SocketListener(serverSocket));

    // start the message queue listener
    if (stayAlive.get()) {
      messageQueueThread.execute(new Runnable() {
          public void run() {
            while (stayAlive.get()) {
              final Message message = getNextMessage(500);
              if (message != null) handleMessage(message);
            }
          }
        });
    }

    while (stayAlive.get()) {
      synchronized (this) {
        final long starttime = System.currentTimeMillis();

        try {
          wait(SHUTDOWN_LATENCY);
        }
        catch (InterruptedException ie) {
          final long endtime = System.currentTimeMillis();
          System.out.println(new Date(endtime) + ": WARNING: NodeServer.run.wait(" + SHUTDOWN_LATENCY +
                             ") interrupted! (after " + (endtime - starttime) + " millis)");
          ie.printStackTrace(System.out);
//          shutdown(true);
        }
      }
    }
  }

  public void shutdown(boolean now) {
    if (stayAlive.compareAndSet(true, false)) {
      if (now) {
        serverThread.shutdownNow();
        socketPool.shutdownNow();
        messageQueueThread.shutdownNow();
        messageHandlerPool.shutdownNow();
      }
      else {
        // shutdown server thread so no new connections are made
        serverThread.shutdown();

        // wait for message

        socketPool.shutdown();
        messageQueueThread.shutdown();
        messageHandlerPool.shutdown();
      }
    }
  }

  private Message getNextMessage(int timeout) {
    Message result = null;

    long starttime = System.currentTimeMillis();
    int numRetries = 100;

    while (stayAlive.get() && result == null && numRetries > 0) {
      try {
        result = messageQueue.poll(timeout, TimeUnit.MILLISECONDS);
      }
      catch (InterruptedException ie) {
        final long endtime = System.currentTimeMillis();
        System.out.println(new Date(endtime) + ": WARNING: NodeServer.getNextMessage(" + timeout +
                           ") interrupted! (after " + (endtime - starttime) + " millis) [numRetries=" + numRetries + "]");
        ie.printStackTrace(System.out);
        starttime = System.currentTimeMillis();  // reset the clock
        --numRetries;
//        stayAlive.set(false);
      }
    }

    return result;
  }

  private void handleMessage(final Message message) {
    if (stayAlive.get()) {
      boolean handled = false;
      int tryCount = 0;
      while (stayAlive.get() && tryCount < 100) {
        try {
          messageHandlerPool.execute(new Runnable() {
              public void run() {
                message.handle(context);
              }
            });
          handled = true;
          break;
        }
        catch (RejectedExecutionException e) {
          Thread.yield();
//           try {
//             Thread.sleep(100);
//           }
//           catch (InterruptedException stop) {break;}
          ++tryCount;
        }
      }
      if (!handled) {
        throw new IllegalStateException("Couldn't handle message '" + message + "'! " + tryCount + " failures.");
      }
    }
  }

  private class SocketListener implements Runnable {
    private ServerSocket serverSocket;

    public SocketListener(ServerSocket serverSocket) {
      this.serverSocket = serverSocket;
    }

    public void run() {
      Socket socket = null;

      if (serverSocket == null) {
        shutdown(true);
        return;
      }

      while (stayAlive.get()) {
//        System.out.println(nodeName + "-SocketListener -- accepting...");
        try {
          socket = serverSocket.accept();
//          System.out.println(nodeName + " ACCEPTED socket!");
        }
        catch (SocketTimeoutException e) {
          if (!stayAlive.get()) {
            shutdown(true);
          }
        }
        catch (IOException e) {
          //todo: determine what to do here...
          e.printStackTrace(System.err);
          shutdown(true);
        }

        if (socket != null) {
          // start a socket thread
          socketPool.execute(new SocketHandler(socket));
          socket = null;
        }
        else {
//          System.out.println(nodeName + " NO socket!");
        }
      }
    }
  }
  
  private class SocketHandler implements Runnable {
    private Socket socket;

    public SocketHandler(Socket socket) {
      this.socket = socket;
    }

    public void run() {
      SocketIO socketIO = null;
      try {
        // receive message over socket
        socketIO = new SocketIO(socket);
        final DataOutputStream dataOut = socketIO.getDataOutput();
        final DataInputStream dataIn = socketIO.getDataInput();
        if (dataOut != null && dataIn != null) {
          // get message, send response, put message into message queue
          final Messenger messenger = new Messenger(dataOut, dataIn);
          final Message message = messenger.receiveMessage(context);  // does both receive and response

          //todo: split up receiving message and sending response so that if the message
          //      queue is full we can report that in the response to the client.
          messageQueue.add(message);
        }
      }
      catch (IOException e) {
        //todo: determine what to do here... failed receiving message/sending response
        e.printStackTrace(System.err);
      }
      finally {
        try {
          if (socketIO != null) socketIO.close();
          socket.close();

          socketIO = null;
          socket = null;
        }
        catch (IOException e) {
          //todo: determine what to do here... failed closing socketIO
          e.printStackTrace(System.err);
        }
      }
    }
  }
}