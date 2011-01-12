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
package org.sd.atn;


import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sd.token.CategorizedToken;
import org.sd.token.Feature;
import org.sd.util.tree.NodePath;
import org.sd.util.tree.Tree;
import org.sd.xml.DomElement;
import org.sd.xml.DomNode;
import org.sd.xml.XmlLite;
import org.w3c.dom.NodeList;

/**
 * A generic parse interpreter.
 *
 * @author Spence Koehler
 */
public class RecordParseInterpreter extends TemplateParseInterpreter {
  
  private String[] classifications;
  private InnerResources resources;
  private List<RecordTemplate> topTemplates;
  private boolean trace;

  public RecordParseInterpreter(DomNode domNode, ResourceManager resourceManager) {
    super(domNode, resourceManager);
  }

  /**
   * @return true to execute recordTemplate.interpret(parse); otherwise, false.
   */
  protected boolean foundMatchingTemplateHook(RecordTemplate recordTemplate, Parse parse) {
    return true;
  }

  /**
   * Hook on a final interpretation.
   */
  protected ParseInterpretation interpretationHook(ParseInterpretation interp, Parse parse) {
    return interp;
  }

  /**
   * Hook on each record interpNode just after creation and before insertion
   * as a child into its tree.
   * <p>
   * NOTE: This hook can be called twice for each recordTemplate -- once before
   *       its fields are processed (start==true) and again after the fields
   *       have been *successfully* processed (start=false).  If the hook is
   *       called twice in a row with start=true, then field processing of the
   *       prior invocation yielded no true results.
   */
  protected Tree<XmlLite.Data> interpRecordNodeHook(Tree<XmlLite.Data> recordNode, Parse parse,
                                                    Tree<String> parseNode, Tree<XmlLite.Data> parentNode,
                                                    String fieldName, RecordTemplate recordTemplate,
                                                    boolean start) {
    if (trace) trace("record", recordNode, fieldName, start);

    return recordNode;
  }

  /**
   * Hook on each field interpNode just after creation and before insertion
   * as a child into its tree.
   * <p>
   * Note that each non-root record node will come back through as a field
   * (after all fields have been visited) but not all fields come through as a
   * record.
   */
  protected Tree<XmlLite.Data> interpFieldNodeHook(Tree<XmlLite.Data> fieldNode, Parse parse,
                                                   Tree<String> selectedNode, Tree<XmlLite.Data> parentNode,
                                                   FieldTemplate fieldTemplate) {
    if (trace) trace("field", fieldNode, fieldTemplate.getName(), null);

    return fieldNode;
  }
}