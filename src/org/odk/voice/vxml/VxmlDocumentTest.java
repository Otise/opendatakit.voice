package org.odk.voice.vxml;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import junit.framework.TestCase;

public class VxmlDocumentTest extends TestCase {

  public void testSimpleDocument(){
    VxmlPrompt p = new VxmlPrompt("Testing 1 2 3");
    VxmlField f = new VxmlField("test", p);
    try {
    new VxmlDocument(f).write(new OutputStreamWriter(new FileOutputStream("log.txt")));
    } catch (Exception e) { fail(); }
  }
}
