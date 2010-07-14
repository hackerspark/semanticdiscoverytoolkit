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
package org.sd.token;


import org.sd.xml.DataProperties;
import org.sd.xml.DomElement;

/**
 * Options for a StandardTokenizer instance.
 * <p>
 * @author Spence Koehler
 */
public class StandardTokenizerOptions {
  
  private TokenRevisionStrategy revisionStrategy;
  public TokenRevisionStrategy getRevisionStrategy() {
    return revisionStrategy;
  }
  public void setRevisionStrategy(TokenRevisionStrategy revisionStrategy) {
    this.revisionStrategy = revisionStrategy;
  }

  /**
   * Specifies the break between a lowercase letter immediately followed
   * by an uppercase letter.
   * 
   * Examples
   * - Break.ZERO_WIDTH_SOFT_BREAK would softsplit camelcased words.
   * - Break.NO_BREAK would always maintain full camelcased words.
   * 
   * Default value is Break.ZERO_WIDTH_SOFT_BREAK
   */
  private Break lowerUpperBreak;
  public Break getLowerUpperBreak() {
    return lowerUpperBreak;
  }
  public void setLowerUpperBreak(Break lowerUpperBreak) {
    this.lowerUpperBreak = lowerUpperBreak;
  }

  /**
   * Specifies the break between an uppercase letter immediately followed
   * by a lowercase letter.
   * 
   * Examples
   * - Break.NO_BREAK would always maintain full capitalized words
   * - Break.ZERO_WIDTH_SOFT_BREAK would softsplit capitalized initials
   * 
   * Default value is Break.NO_BREAK
   */
  private Break upperLowerBreak;
  public Break getUpperLowerBreak() {
    return upperLowerBreak;
  }
  public void setUpperLowerBreak(Break upperLowerBreak) {
    this.upperLowerBreak = upperLowerBreak;
  }

  /**
   * Specifies the break between an uppercase letter and a digit.
   * 
   * Examples:
   * - Break.NO_BREAK would maintain e.g. product numbers (like Z90)
   * - Break.ZERO_WIDTH_SOFT_BREAK would softsplit some dates (like MAR04)
   * 
   * Default value is Break.NO_BREAK
   */
  private Break upperDigitBreak;
  public Break getUpperDigitBreak() {
    return upperDigitBreak;
  }
  public void setUpperDigitBreak(Break upperDigitBreak) {
    this.upperDigitBreak = upperDigitBreak;
  }

  /**
   * Specifies the break between a lowercase letter and a digit.
   * 
   * Examples:
   * - Break.ZERO_WIDTH_SOFT_BREAK would softsplit some dates (like Mar04)
   * - Break.NO_BREAK would maintain e.g. product numbers (like i386)
   * 
   * Default value is Break.ZERO_WIDTH_SOFT_BREAK
   */
  private Break lowerDigitBreak;
  public Break getLowerDigitBreak() {
    return lowerDigitBreak;
  }
  public void setLowerDigitBreak(Break lowerDigitBreak) {
    this.lowerDigitBreak = lowerDigitBreak;
  }

  /**
   * Specifies the break between a digit and an uppercase letter.
   * 
   * Examples:
   * - Break.NO_BREAK would maintain ordinals (like 12TH) and product numbers (like 3G)
   * - Break.ZERO_WIDTH_SOFT_BREAK would softsplit ordinals and product numbers
   * 
   * Default value is Break.NO_BREAK
   */
  private Break digitUpperBreak;
  public Break getDigitUpperBreak() {
    return digitUpperBreak;
  }
  public void setDigitUpperBreak(Break digitUpperBreak) {
    this.digitUpperBreak = digitUpperBreak;
  }

  /**
   * Specifies the break between a digit and a lowercase letter.
   * 
   * Examples:
   * - Break.NO_BREAK would maintain ordinals (like 12th) and product numbers (like 9i)
   * - Break.ZERO_WIDTH_SOFT_BREAK would softsplit ordinals and product numbers
   * 
   * Default value is Break.NO_BREAK
   */
  private Break digitLowerBreak;
  public Break getDigitLowerBreak() {
    return digitLowerBreak;
  }
  public void setDigitLowerBreak(Break digitLowerBreak) {
    this.digitLowerBreak = digitLowerBreak;
  }

  /**
   * Specifies how to treat each character in a double-dash sequence
   * where at least one side borders on whitespace.
   * 
   * When this value is Break.SINGLE_WIDTH_SOFT_BREAK, the first dash
   * receives this break value while the second dash is marked as
   * Break.NO_BREAK. Otherwise, the value is repeated for both dashes.
   * 
   * Default is Break.SINGLE_WIDTH_HARD_BREAK for both.
   */
  private Break nonEmbeddedDoubleDashBreak;
  public Break getNonEmbeddedDoubleDashBreak() {
    return nonEmbeddedDoubleDashBreak;
  }
  public void setNonEmbeddedDoubleDashBreak(Break nonEmbeddedDoubleDashBreak) {
    this.nonEmbeddedDoubleDashBreak = nonEmbeddedDoubleDashBreak;
  }

  /**
   * Specifies how to treat each character in a double-dash sequence
   * where neither side borders on whitespace or punctuation.
   * 
   * When this value is Break.SINGLE_WIDTH_SOFT_BREAK, the first dash
   * receives this break value while the second dash is marked as
   * Break.NO_BREAK. Otherwise, the value is repeated for both dashes.
   * 
   * Default is Break.SINGLE_WIDTH_HARD_BREAK for both.
   */
  private Break embeddedDoubleDashBreak;
  public Break getEmbeddedDoubleDashBreak() {
    return embeddedDoubleDashBreak;
  }
  public void setEmbeddedDoubleDashBreak(Break embeddedDoubleDashBreak) {
    this.embeddedDoubleDashBreak = embeddedDoubleDashBreak;
  }

  /**
   * Specifies how to treat a single dash not bordering on characters
   * or digits on both sides.
   * 
   * Default is Break.SINGLE_WIDTH_SOFT_BREAK.
   * 
   * Note: A free-standing dash (" - ") is treated as a hard break.
   */
  private Break embeddedDashBreak;
  public Break getEmbeddedDashBreak() {
    return embeddedDashBreak;
  }
  public void setEmbeddedDashBreak(Break embeddedDashBreak) {
    this.embeddedDashBreak = embeddedDashBreak;
  }

  /**
   * A left-bordered dash has non-white on the left side and would be
   * a non-break if considered as a hyphenation to be detected and
   * handled by feature classifiers applied to a token. Otherwise, it
   * could be marked as a hard (typical) break or soft break.
   * 
   * Default is Break.NO_BREAK;
   */
  private Break leftBorderedDashBreak;
  public Break getLeftBorderedDashBreak() {
    return leftBorderedDashBreak;
  }
  public void setLeftBorderedDashBreak(Break leftBorderedDashBreak) {
    this.leftBorderedDashBreak = leftBorderedDashBreak;
  }

  /**
   * A right-bordered dash has non-white on the right side and would
   * be a non-break if considered as the "negative" for a negative number
   * or as a reverse hyphenation denoting a suffix (like for "-ing").
   * 
   * Default is Break.NO_BREAK;
   */
  private Break rightBorderedDashBreak;
  public Break getRightBorderedDashBreak() {
    return rightBorderedDashBreak;
  }
  public void setRightBorderedDashBreak(Break rightBorderedDashBreak) {
    this.rightBorderedDashBreak = rightBorderedDashBreak;
  }

  /**
   * A free-standing dash (" - ") can be treated as a soft break if the
   * token feature classifiers handle that form; otherwise, it should
   * be treated as a hard break.
   * 
   * Default is Break.SINGLE_WIDTH_HARD_BREAK;
   */
  private Break freeStandingDashBreak;
  public Break getFreeStandingDashBreak() {
    return freeStandingDashBreak;
  }
  public void setFreeStandingDashBreak(Break freeStandingDashBreak) {
    this.freeStandingDashBreak = freeStandingDashBreak;
  }

  /**
   * How to treat whitespace. Treat as a soft break if multitoken words are to
   * be classified; otherwise, treat as a hard break.
   //
   * Default is Break.SINGLE_WIDTH_SOFT_BREAK;
   */
  private Break whitespaceBreak;
  public Break getWhitespaceBreak() {
    return whitespaceBreak;
  }
  public void setWhitespaceBreak(Break whitespaceBreak) {
    this.whitespaceBreak = whitespaceBreak;
  }

  /**
   * How to treat quotes and parentheses, including square and angle brackets.
   */
  private Break quoteAndParenBreak;
  public Break getQuoteAndParenBreak() {
    return quoteAndParenBreak;
  }
  public void setQuoteAndParenBreak(Break quoteAndParenBreak) {
    this.quoteAndParenBreak = quoteAndParenBreak;
  }


  /**
   * Construct with default options.
   */
  public StandardTokenizerOptions() {
    this.revisionStrategy = TokenRevisionStrategy.LSL;

    this.lowerUpperBreak = Break.ZERO_WIDTH_SOFT_BREAK;
    this.upperLowerBreak = Break.NO_BREAK;
    this.upperDigitBreak = Break.NO_BREAK;
    this.lowerDigitBreak = Break.ZERO_WIDTH_SOFT_BREAK;
    this.digitUpperBreak = Break.NO_BREAK;
    this.digitLowerBreak = Break.NO_BREAK;

    this.nonEmbeddedDoubleDashBreak = Break.SINGLE_WIDTH_HARD_BREAK;
    this.embeddedDoubleDashBreak = Break.SINGLE_WIDTH_HARD_BREAK;
    this.embeddedDashBreak = Break.SINGLE_WIDTH_SOFT_BREAK;
    this.leftBorderedDashBreak = Break.NO_BREAK;
    this.rightBorderedDashBreak = Break.NO_BREAK;
    this.freeStandingDashBreak = Break.SINGLE_WIDTH_HARD_BREAK;
    this.whitespaceBreak = Break.SINGLE_WIDTH_SOFT_BREAK;
    this.quoteAndParenBreak = Break.SINGLE_WIDTH_HARD_BREAK;
  }

  /**
   * Construct with the given options.
   */
  public StandardTokenizerOptions(DomElement optionsElement) {
    final DataProperties options = new DataProperties(optionsElement);
    init(options);
  }

  /**
   * Construct with the given options.
   */
  public StandardTokenizerOptions(DataProperties options) {
    init(options);
  }

  private void init(DataProperties options) {
    // enum LSL, SO, LO, SL, LS
    String revisionStrategy = options.getString("revisionStrategy", "LSL");

    // break NO_BREAK, SINGLE_WIDTH_HARD_BREAK, SINGLE_WIDTH_SOFT_BREAK, ZERO_WIDTH_SOFT_BREAK, ZERO_WIDTH_HARD_BREAK
    String lowerUpperBreak = options.getString("lowerUpperBreak", "ZERO_WIDTH_SOFT_BREAK");
    String upperLowerBreak = options.getString("upperLowerBreak", "NO_BREAK");
    String upperDigitBreak = options.getString("upperDigitBreak", "NO_BREAK");
    String lowerDigitBreak = options.getString("lowerDigitBreak", "ZERO_WIDTH_SOFT_BREAK");
    String digitUpperBreak = options.getString("digitUpperBreak", "NO_BREAK");
    String digitLowerBreak = options.getString("digitLowerBreak", "NO_BREAK");

    String nonEmbeddedDoubleDashBreak = options.getString("nonEmbeddedDoubleDashBreak", "SINGLE_WIDTH_HARD_BREAK");
    String embeddedDoubleDashBreak = options.getString("embeddedDoubleDashBreak", "SINGLE_WIDTH_HARD_BREAK");
    String embeddedDashBreak = options.getString("embeddedDashBreak", "SINGLE_WIDTH_SOFT_BREAK");
    String leftBorderedDashBreak = options.getString("leftBorderedDashBreak", "NO_BREAK");
    String rightBorderedDashBreak = options.getString("rightBorderedDashBreak", "NO_BREAK");
    String freeStandingDashBreak = options.getString("freeStandingDashBreak", "SINGLE_WIDTH_HARD_BREAK");
    String whitespaceBreak = options.getString("whitespaceBreak", "SINGLE_WIDTH_SOFT_BREAK");
    String quoteAndParenBreak = options.getString("quoteAndParenBreak", "SINGLE_WIDTH_HARD_BREAK");

    // set RevisionStrategy
    this.revisionStrategy = translateRevisionStrategy(revisionStrategy);

    // set Breaks
    this.lowerUpperBreak = translateBreak(lowerUpperBreak);
    this.upperLowerBreak = translateBreak(upperLowerBreak);
    this.upperDigitBreak = translateBreak(upperDigitBreak);
    this.lowerDigitBreak = translateBreak(lowerDigitBreak);
    this.digitUpperBreak = translateBreak(digitUpperBreak);
    this.digitLowerBreak = translateBreak(digitLowerBreak);

    this.nonEmbeddedDoubleDashBreak = translateBreak(nonEmbeddedDoubleDashBreak);
    this.embeddedDoubleDashBreak = translateBreak(embeddedDoubleDashBreak);
    this.embeddedDashBreak = translateBreak(embeddedDashBreak);
    this.leftBorderedDashBreak = translateBreak(leftBorderedDashBreak);
    this.rightBorderedDashBreak = translateBreak(rightBorderedDashBreak);
    this.freeStandingDashBreak = translateBreak(freeStandingDashBreak);
    this.whitespaceBreak = translateBreak(whitespaceBreak);
    this.quoteAndParenBreak = translateBreak(quoteAndParenBreak);
  }

  /**
   * Utility method to translate a revision strategy String to a TokenRevisionStrategy.
   */
  public static TokenRevisionStrategy translateRevisionStrategy(String revisionStrategyString) {
    TokenRevisionStrategy result = TokenRevisionStrategy.LSL;

    if (revisionStrategyString != null) revisionStrategyString = revisionStrategyString.toUpperCase();

    if ("LSL".equals(revisionStrategyString)) {
      result = TokenRevisionStrategy.LSL;
    }
    else if ("SO".equals(revisionStrategyString)) {
      result = TokenRevisionStrategy.SO;
    }
    else if ("LO".equals(revisionStrategyString)) {
      result = TokenRevisionStrategy.LO;
    }
    else if ("SL".equals(revisionStrategyString)) {
      result = TokenRevisionStrategy.SL;
    }
    else if ("LS".equals(revisionStrategyString)) {
      result = TokenRevisionStrategy.LS;
    }
    else {
      result = TokenRevisionStrategy.LSL;
    }

    return result;
  }

  /**
   * Utility method to translate a break String to a Break instance.
   */
  public static Break translateBreak(String breakString) {
    Break result = null;

    if (breakString != null) breakString = breakString.toUpperCase();

    if ("NO_BREAK".equals(breakString)) {
      result = Break.NO_BREAK;
    }
    else if ("SINGLE_WIDTH_HARD_BREAK".equals(breakString)) {
      result = Break.SINGLE_WIDTH_HARD_BREAK;
    }
    else if ("SINGLE_WIDTH_SOFT_BREAK".equals(breakString)) {
      result = Break.SINGLE_WIDTH_SOFT_BREAK;
    }
    else if ("ZERO_WIDTH_SOFT_BREAK".equals(breakString)) {
      result = Break.ZERO_WIDTH_SOFT_BREAK;
    }
    else if ("ZERO_WIDTH_HARD_BREAK".equals(breakString)) {
      result = Break.ZERO_WIDTH_HARD_BREAK;
    }
    else {
      result = Break.NO_BREAK;
    }

    return result;
  }

  public boolean equals(Object o) {
    boolean result = this == o;

    if (!result && o instanceof StandardTokenizerOptions) {
      final StandardTokenizerOptions other = (StandardTokenizerOptions)o;

      result =
        this.revisionStrategy == other.revisionStrategy &&

        this.lowerUpperBreak == other.lowerUpperBreak &&
        this.upperDigitBreak == other.upperDigitBreak &&
        this.lowerDigitBreak == other.lowerDigitBreak &&
        this.digitUpperBreak == other.digitUpperBreak &&
        this.digitLowerBreak == other.digitLowerBreak &&

        this.nonEmbeddedDoubleDashBreak == other.nonEmbeddedDoubleDashBreak &&
        this.embeddedDoubleDashBreak == other.embeddedDoubleDashBreak &&
        this.embeddedDashBreak == other.embeddedDashBreak &&
        this.leftBorderedDashBreak == other.leftBorderedDashBreak &&
        this.rightBorderedDashBreak == other.rightBorderedDashBreak &&
        this.freeStandingDashBreak == other.freeStandingDashBreak &&
        this.whitespaceBreak == other.whitespaceBreak &&
        this.quoteAndParenBreak == other.quoteAndParenBreak;
    }

    return result;
  }

  public int hashCode() {
    int result = 1;

    result = result * 17 + this.revisionStrategy.hashCode();

    result = result * 17 + this.lowerUpperBreak.hashCode();
    result = result * 17 + this.upperDigitBreak.hashCode();
    result = result * 17 + this.lowerDigitBreak.hashCode();
    result = result * 17 + this.digitUpperBreak.hashCode();
    result = result * 17 + this.digitLowerBreak.hashCode();

    result = result * 17 + this.nonEmbeddedDoubleDashBreak.hashCode();
    result = result * 17 + this.embeddedDoubleDashBreak.hashCode();
    result = result * 17 + this.embeddedDashBreak.hashCode();
    result = result * 17 + this.leftBorderedDashBreak.hashCode();
    result = result * 17 + this.rightBorderedDashBreak.hashCode();
    result = result * 17 + this.freeStandingDashBreak.hashCode();
    result = result * 17 + this.whitespaceBreak.hashCode();
    result = result * 17 + this.quoteAndParenBreak.hashCode();

    return result;
  }
}
