/*
    Copyright 2010 Semantic Discovery, Inc. (www.semanticdiscovery.com)

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


import java.io.IOException;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Utility class for building an xml string.
 * <p>
 * @author Spence Koehler
 */
public class XmlStringBuilder {
  
  private StringBuilder xml;
  private DomElement _xmlElement;
  private String rootTag;
  private boolean ended;

  /**
   * Construct without any root tag.
   */
  public XmlStringBuilder() {
    this(null);
  }

  /**
   * Construct with the given root tag and attrs (without &lp; and &rp;).
   */
  public XmlStringBuilder(String rootTagAndAttrs) {
    reset(rootTagAndAttrs);
  }

  /**
   * Clear all data, resetting to initial state.
   */
  public final XmlStringBuilder reset(String rootTagAndAttrs) {
    this.rootTag = rootTagAndAttrs;
    return reset();
  }

  /**
   * Clear all data, resetting to initial state.
   */
  public final XmlStringBuilder reset() {
    this.xml = null;
    this._xmlElement = null;
    this.ended = false;
    return this;
  }

  /**
   * Set the xml for this instance, overriding the root and any added data.
   * <p>
   * All later adds will fail and return null until this instances is reset.
   */
  public XmlStringBuilder setXmlString(String xmlString) {
    initXml(null);
    xml.append(xmlString);
    this.ended = true;
    return this;
  }

  /**
   * Set the xml for this instance, overriding the root and any added data.
   * <p>
   * All later adds will fail and return null until this instances is reset.
   */
  public XmlStringBuilder setXmlElement(DomElement xmlElement) {
    initXml(null);
    addElement(xmlElement);
    this._xmlElement = xmlElement;
    this.ended = true;
    return this;
  }

  /**
   * Set this instance's data to be a copy of the other.
   */
  public void copy(XmlStringBuilder other) {
    this.xml = other.xml;
    this._xmlElement = other._xmlElement;
    this.rootTag = other.rootTag;
    this.ended = other.ended;
  }

  /**
   * Adds the full element to this instance if possible.
   */
  public XmlStringBuilder addElement(DomElement element) {
    if (ended || element == null) return null;
    if (xml == null) initXml(rootTag);
    element.asFlatString(xml);
    _xmlElement = null;
    return this;
  }

  /**
   * Add the well-formed (including escaped) xml string to this instance.
   * <p>
   * Note that the root tag will automatically be built. All other xml is to be
   * added through this or other methods.
   */
  public XmlStringBuilder addXml(String xmlString) {
    if (ended || xmlString == null) return null;
    if (xml == null) initXml(rootTag);
    xml.append(xmlString);
    _xmlElement = null;
    return this;
  }

  /**
   * Add a (start) tag given the text between the &lp; and &rp;
   */
  public XmlStringBuilder addTag(String tagWithAttrs) {
    if (ended || tagWithAttrs == null) return null;
    final StringBuilder tag = new StringBuilder();
    tag.append('<').append(tagWithAttrs).append('>');
    return addXml(tag.toString());
  }

  /**
   * Add an end tag.
   */
  public XmlStringBuilder addEndTag(String tagWithAttrs) {
    if (ended || tagWithAttrs == null) return null;
    final StringBuilder tag = new StringBuilder();
    tag.append("</").append(getTagName(tagWithAttrs)).append(">");
    return addXml(tag.toString());
  }

  /**
   * Adds the tag (as-is, including attributes) between &lt; and &gt; symbols
   * and the (unescaped) text followed by the close tag.
   * <p>
   * If the text is null, then the tag is self-terminated; otherwise, the text
   * is added along with a closing tag.
   */
  public XmlStringBuilder addTagAndText(String tag, String text) {
    return addTagAndText(tag, text, true);
  }

  /**
   * Adds the tag (as-is, including attributes) between &lt; and &gt; symbols
   * and the (unescaped) text followed (optionally) by the close tag.
   * <p>
   * If the text is null, then the tag is self-terminated; otherwise, the text
   * is added along with a closing tag (if close==true).
   */
  public XmlStringBuilder addTagAndText(String tag, String text, boolean close) {
    if (ended || tag == null) return null;

    final StringBuilder xmlString = new StringBuilder();
    xmlString.append('<').append(tag);

    if (text == null) {
      xmlString.append("/>");
    }
    else {
      xmlString.append('>').append(StringEscapeUtils.escapeXml(text));

      if (close) {
        xmlString.append("</").append(getTagName(tag)).append('>');
      }
    }

    return addXml(xmlString.toString());
  }

  /**
   * Get the (current) xml element (if xmlString is well-formed) or null.
   */
  public DomElement getXmlElement() {
    if (_xmlElement == null) {
      final String xmlString = getXmlString();

      if (xmlString != null) {
        try {
          _xmlElement = (DomElement)XmlFactory.buildDomNode(xmlString, false);
        }
        catch (IOException e) {
        }
      }
    }

    return _xmlElement;
  }

  public String getXmlString() {
    String result = null;

    if ((xml == null || xml.length() == 0)) {
      if (_xmlElement != null) {
        if (xml == null) xml = new StringBuilder();
        _xmlElement.asFlatString(xml);
        ended = true;
        result = xml.toString();
      }
      else if (rootTag != null && !"".equals(rootTag)) {
        initXml(rootTag);
        ended = true;
        result = xml.toString();
      }
      // else result is null
    }
    else {
      if (rootTag == null || ended) {
        result = xml.toString();
      }
      else {
        final StringBuilder all = new StringBuilder();
        all.append(xml).append("</").append(getTagName(rootTag)).append('>');
        result = all.toString();
      }
    }

    return result;
  }

  public String toString() {
    return getXmlString();
  }

  private final String getTagName(String tagString) {
    final int spPos = tagString.indexOf(' ');
    return (spPos >= 0) ? tagString.substring(0, spPos) : tagString;
  }

  private final void initXml(String rootTag) {
    if (xml == null) {
      xml = new StringBuilder();
    }
    else {
      xml.setLength(0);
    }
    this.ended = false;
    this._xmlElement = null;
    if (rootTag != null) {
      xml.append('<').append(rootTag).append('>');
    }
  }
}
