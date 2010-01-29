package org.odk.voice.local;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * JavaRosa/XForms defines languages in terms of names such as 'English' and 
 * 'French', while Java uses Locales to represent different languages/regions. 
 * This class provides a mapping between XForms language names and Java Locales. 
 * 
 * If your form includes a language not included in this list, you must add it here.
 * 
 * @author alerer
 *
 */
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
