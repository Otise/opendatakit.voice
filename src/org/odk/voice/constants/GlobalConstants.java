package org.odk.voice.constants;

import java.util.Locale;

public class GlobalConstants {
  
  /**
   * URL where completed form data is sent for aggregation.
   * Should be the /submission URL of an ODK Aggregate instance, or another server 
   * implementing the same API.
   */
  public static final String UPLOAD_URL = "http://odk-voice.appspot.com/submission";
  
  /**
   *  default locale (language) for rendering forms
   */
  public static final Locale DEFAULT_LOCALE = new Locale("en");
  
  public static final String INTERDIGIT_TIMEOUT = "3000ms";
  
  /**
   * Keys into the 'misc' database table.
   * Use with DbAdapter.getMiscValue();
   */
  public static final String CURRENT_RECORD_PROMPT_KEY = "currentrecordprompt";
  public static final String OUTBOUND_URL_KEY = "outboundurl";
  public static final String OUTBOUND_TOKEN_KEY = "outboundtoken";
  public static final String OUTBOUND_CALLERID_KEY = "outboundcallerid";
  
}
