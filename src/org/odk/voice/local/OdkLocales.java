package org.odk.voice.local;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OdkLocales {

  private static Map<String,Locale> localeMap = new HashMap<String,Locale>();
  
  static {
    localeMap.put("English", Locale.ENGLISH);
    localeMap.put("Swahili", new Locale("sw"));
  }
  
  public static Locale getLocale(String language) {
    return localeMap.get(language);
  }
}
