package ee.taltech.bigdata;

import lombok.Getter;

@Getter
public enum Comparisons {
  DATE_TIME("date-time"),
  DATE("date"),
  TIME("time"),
  CURRENCY_AMOUNT("currency-amount"),
  CURRENCY_PRECISE("currency-precise"),
  FIXED_POINT("fixed-point"),
  FLOATING_POINT("floating-point"),
  INTEGER("integer"),
  NUMERIC("numeric"),
  BLOB("blob"),
  BYTE_ARRAY("byte-array"),
  OBJECT("object"),
  NULL("null");


  private final String value;

  Comparisons(String value) {
    this.value = value;
  }
}
