public class StringUtilsCustom {
  public static final String SPACE = " ";
  public static final String HASH_TAG = "#";
  public static final String EMPTY = "";
  public static final int INDEX_NOT_FOUND = -1;
  public static boolean isEmpty(final CharSequence cs) {
    return cs == null || cs.length() == 0;
  }
  public static boolean isNotEmpty(final CharSequence cs) {
    return !isEmpty(cs);
  }


}
