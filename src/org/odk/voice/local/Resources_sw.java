package org.odk.voice.local;

import java.util.ListResourceBundle;


/**
 * The default ResourceBundle for local content.
 * ODK Voice can be translated to other languages by creating locale-specific Resource bundles,
 * e.g. Resources_de for German (the ISO code must be the same as the language code in the XForm).
 * 
 * @author alerer
 *
 */
public class Resources_sw extends ListResourceBundle {
  public Object[][] getContents() {
      return contents;
  }
  
  static final Object[][] contents = {
      {ResourceKeys.THANK_YOU,
        "Thank you (in Swahili)."
      },
      {ResourceKeys.INT_INSTRUCTIONS,
        "Please enter the number on the keypad (in Swahili)."
      },
  // END OF MATERIAL TO LOCALIZE
  };
}
