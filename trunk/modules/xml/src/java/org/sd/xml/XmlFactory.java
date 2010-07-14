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
package org.sd.xml;


import org.sd.io.FileUtil;
import org.sd.util.thread.Killable;
import org.sd.util.thread.TimeLimitedThread;
import org.sd.util.tree.Tree;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Factory class for building an xml tree.
 * <p>
 * @author Spence Koehler
 */
public class XmlFactory {
  
  public static final XmlTagParser XML_TAG_PARSER_IGNORE_COMMENTS = new XmlTagParser(null, null, false, true);
  public static final XmlTagParser XML_TAG_PARSER_KEEP_COMMENTS = new XmlTagParser(null, null, false, false);
  public static final XmlTagParser HTML_TAG_PARSER_IGNORE_COMMENTS = new XmlTagParser(true, true);
  public static final XmlTagParser HTML_TAG_PARSER_KEEP_COMMENTS = new XmlTagParser(true, false);

  public static final XmlLite XML_LITE_IGNORE_COMMENTS = new XmlLite(XML_TAG_PARSER_IGNORE_COMMENTS, null, false);   // ignore comments
  public static final XmlLite XML_LITE_KEEP_COMMENTS = new XmlLite(XML_TAG_PARSER_KEEP_COMMENTS, null, false);       // keep comments
  public static final XmlLite HTML_LITE_IGNORE_COMMENTS = new XmlLite(HTML_TAG_PARSER_IGNORE_COMMENTS, HtmlHelper.DEFAULT_IGNORE_TAGS, true); // ignore comments
  public static final XmlLite HTML_LITE_KEEP_COMMENTS = new XmlLite(HTML_TAG_PARSER_KEEP_COMMENTS, HtmlHelper.DEFAULT_IGNORE_TAGS, true);     // keep comments

  public static final String XML_FILENAME_ATTRIBUTE = "_xmlFilename";


  /**
   * Load the file as a DOM document.
   */
  public static final DomDocument loadDocument(File file, boolean htmlFlag) throws IOException {
    return loadDocument(file, htmlFlag, null);
  }

  /**
   * Load the file as a DOM document.
   */
  public static final DomDocument loadDocument(File file, boolean htmlFlag, DataProperties options) throws IOException {
    final Tree<XmlLite.Data> domTree = readXmlTree(file, true, htmlFlag, false);
    domTree.getAttributes().put(XML_FILENAME_ATTRIBUTE, file);

    final DomNode domNode = domTree.getData().asDomNode();
    final DomDocument domDocument = domNode.getOwnerDomDocument();
    if (options != null) domDocument.setDataProperties(options);

    return domDocument;
  }

  public static final DomDocument loadDocument(InputStream inputStream, boolean htmlFlag, DataProperties options) throws IOException {
    final Tree<XmlLite.Data> domTree = readXmlTree(inputStream, null, true, htmlFlag, null, false);

    final DomNode domNode = domTree.getData().asDomNode();
    final DomDocument domDocument = domNode.getOwnerDomDocument();
    if (options != null) domDocument.setDataProperties(options);

    return domDocument;
  }

  /**
   * Load the file as a DOM document.
   */
  public static final DomDocument loadDocument(String xml, boolean htmlFlag) throws IOException {
    final Tree<XmlLite.Data> domTree = buildXmlTree(xml, true, htmlFlag);

    final DomNode domNode = domTree.getData().asDomNode();
    final DomDocument domDocument = domNode.getOwnerDomDocument();
    return domDocument;
  }


  /**
   * If the tree holds XmlLite.Data instances, get its corresponding DomDocument;
   * otherwise, null.
   */
  public static final <T> DomDocument getDomDocument(Tree<T> tree) {
    DomDocument result = null;

    final T data = tree.getData();
    if (data instanceof XmlLite.Data) {
      final XmlLite.Data xmlData = (XmlLite.Data)data;
      result = xmlData.asDomNode().getOwnerDomDocument();
    }

    return result;
  }


  /**
   * Read the xml tree from the given file, unless the time limit is reached.
   *
   * @return the xmlTree or null if the timeLimit was hit.
   */
  public static final Tree<XmlLite.Data> readXmlTree(File file, boolean ignoreComments, boolean htmlFlag, long timeLimit, boolean requireXmlTag) {
    final XmlTreeBuilder builder = new XmlTreeBuilder(file, ignoreComments, htmlFlag, requireXmlTag);
    final TimeLimitedThread runner = new TimeLimitedThread(builder);
    runner.run(timeLimit);
    return builder.getXmlTree();
  }

  public static final Tree<XmlLite.Data> readXmlTree(File file, boolean ignoreComments, boolean htmlFlag, boolean requireXmlTag) throws IOException {
    return readXmlTree(file, ignoreComments, htmlFlag, null, requireXmlTag);
  }

  public static final Tree<XmlLite.Data> readXmlTree(File file, boolean ignoreComments, boolean htmlFlag, AtomicBoolean die, boolean requireXmlTag) throws IOException {
    return readXmlTree(file, ignoreComments, htmlFlag, null, die, requireXmlTag);
  }

  private static final XmlLite getXmlLite(boolean ignoreComments, boolean htmlFlag) {
    XmlLite result = null;

    if (htmlFlag) {
      result = ignoreComments ? HTML_LITE_IGNORE_COMMENTS : HTML_LITE_KEEP_COMMENTS;
    }
    else {
      result = ignoreComments ? XML_LITE_IGNORE_COMMENTS : XML_LITE_KEEP_COMMENTS;
    }

    return result;
  }

  public static final Tree<XmlLite.Data> readXmlTree(File file, boolean ignoreComments, boolean htmlFlag, Encoding[] encoding, AtomicBoolean die, boolean requireXmlTag) throws IOException {
    final XmlLite xmlLite = getXmlLite(ignoreComments, htmlFlag);

    Tree<XmlLite.Data> result = null;
    InputStream inputStream = null;
    XmlInputStream xmlInputStream = null;

    try {
      inputStream = FileUtil.getInputStream(file);
      xmlInputStream = new XmlInputStream(inputStream);
      if (!requireXmlTag || (requireXmlTag && xmlInputStream.foundXmlTag())) {
        result = xmlLite.parse(xmlInputStream, die);
        if (encoding != null && encoding.length == 1) encoding[0] = xmlInputStream.getEncoding();
      }
    }
    catch (EncodingException e) {
      result = readXmlTree(file, Encoding.ASCII, ignoreComments, htmlFlag, die, requireXmlTag);
      if (encoding != null && encoding.length == 1) encoding[0] = Encoding.ASCII;
    }
    catch (EOFException e) {
      System.err.println("***WARNING: Unexpected End of File '" + file.getAbsolutePath() + "'!");
      e.printStackTrace(System.err);
      result = null;
    }
    finally {
      if (inputStream != null) inputStream.close();
      if (xmlInputStream != null) xmlInputStream.close();
    }

    // add a cross-reference attribute to the file on the root node if there isn't one already.
    if (result != null) {
      final XmlLite.Data data = result.getData();
      final XmlLite.Tag tag = data.asTag();
      if (tag != null) {
        String xref = tag.getAttribute("xref");
        if (xref == null) tag.setAttribute("xref", file.getAbsolutePath());
      }
    }

    return result;
  }

  public static final Tree<XmlLite.Data> readXmlTree(File file, Encoding encoding, boolean ignoreComments, boolean htmlFlag, AtomicBoolean die, boolean requireXmlTag) throws IOException {
    Tree<XmlLite.Data> result = null;
    InputStream inputStream = null;

    try {
      inputStream = FileUtil.getInputStream(file);
      result = readXmlTree(inputStream, encoding, ignoreComments, htmlFlag, die, requireXmlTag);
    }
    finally {
      if (inputStream != null) inputStream.close();
    }

    return result;
  }

  public static final Tree<XmlLite.Data> readXmlTree(InputStream inputStream, Encoding encoding, boolean ignoreComments, boolean htmlFlag, AtomicBoolean die, boolean requireXmlTag) throws IOException {

    if (encoding == null) encoding = Encoding.UTF8;
    final XmlLite xmlLite = getXmlLite(ignoreComments, htmlFlag);

    Tree<XmlLite.Data> result = null;
    XmlInputStream xmlInputStream = null;

    try {
      xmlInputStream = new XmlInputStream(inputStream, encoding);
      if (!requireXmlTag || (requireXmlTag && xmlInputStream.foundXmlTag())) {
        result = xmlLite.parse(xmlInputStream, die);
      }
    }
    finally {
      if (xmlInputStream != null) xmlInputStream.close();
    }
    return result;
  }

  public static final Tree<XmlLite.Data> buildXmlTree(String xmlString, boolean ignoreComments, boolean htmlFlag) throws IOException {
    final XmlLite xmlLite = getXmlLite(ignoreComments, htmlFlag);
    return xmlLite.parse(xmlString);
  }

  public static final String stripHtmlFormatting(String htmlString) {
    String result = null;

    try {
      final Tree<XmlLite.Data> xmlNode = buildXmlTree(htmlString, true, true);
      result = XmlTreeHelper.getAllText(xmlNode);
    }
    catch (IOException e) {
      // ignore. problem should be revealed by null result.
    }

    return result;
  }

  private static final class XmlTreeBuilder implements Killable {

    private File file;
    private boolean ignoreComments;
    private boolean htmlFlag;
    private boolean requireXmlTag;
    private Tree<XmlLite.Data> xmlTree;
    private AtomicBoolean die = new AtomicBoolean(false);

    public XmlTreeBuilder(File file, boolean ignoreComments, boolean htmlFlag, boolean requireXmlTag) {
      this.file = file;
      this.ignoreComments = ignoreComments;
      this.htmlFlag = htmlFlag;
      this.requireXmlTag = requireXmlTag;
      this.xmlTree = null;
    }

    public void die() {
      die.set(true);
    }

    public void run() {
      try {
        this.xmlTree = XmlFactory.readXmlTree(file, ignoreComments, htmlFlag, die, requireXmlTag);
      }
      catch (IOException e) {
        //log exception and ignore. null will be result.
        e.printStackTrace(System.err);
      }
    }

    public Tree<XmlLite.Data> getXmlTree() {
      return xmlTree;
    }
  }
}
