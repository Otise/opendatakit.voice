package org.odk.voice.digits2string;

import junit.framework.TestCase;


public class LevenshteinDistanceCalculatorTest extends TestCase {
  LevenshteinDistanceCalculator calc;
  
  final int MAX_SIZE = 30;
  final int ITER = 100;
  
  public void setUp(){
    calc = new LevenshteinDistanceCalculator(MAX_SIZE);
  }
  
  public void testSimple(){
    assertEquals(0, calc.getDistance("",""));
    assertEquals(0, calc.getDistance("12345","12345"));
    for (int i = 0; i < ITER; i++) {
      assertEquals(3, calc.getDistance("Saturday", "Sunday"));
      assertEquals(3, calc.getDistance("kitten", "sitting"));
    }
    
    assertEquals(26, calc.getDistance("abcdefghijklmnopqrstuvwxyz",
                                      "zyxwvutsrqponmlkjihgfedcba"));
    
    assertEquals(7, calc.getDistance("michael", "b"));
    assertEquals(6, calc.getDistance("666555", "2"));
  }
  
}
