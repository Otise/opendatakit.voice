package org.odk.voice.local;

import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.TestCase;

public class ResourceTest extends TestCase {

  public void setUp(){
    
  }
  
  public void testResources(){
    ResourceBundle resources =
      ResourceBundle.getBundle(org.odk.voice.local.Resources.class.getCanonicalName(), 
          new Locale("en"));
    assertEquals("Thank you.", resources.getString(ResourceKeys.THANK_YOU));
    resources =
      ResourceBundle.getBundle(org.odk.voice.local.Resources.class.getCanonicalName(), 
          new Locale("sw"));
    assertEquals("Thank you in Swahili.", resources.getString(ResourceKeys.THANK_YOU));
  }
}
