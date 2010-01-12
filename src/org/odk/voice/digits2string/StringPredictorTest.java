package org.odk.voice.digits2string;
import junit.framework.TestCase;


public class StringPredictorTest extends TestCase {
  public void testCharToInt(){
    for (int c : new StringPredictor(null).charToInt)
      System.out.println(c);
  }
}
