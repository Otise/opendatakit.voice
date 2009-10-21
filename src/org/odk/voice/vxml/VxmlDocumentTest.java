package org.odk.voice.vxml;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import junit.framework.TestCase;

public class VxmlDocumentTest extends TestCase {

  public void testSimpleDocument(){
    //VxmlPrompt p = new VxmlArrayPrompt("Testing 1 2 3");
    VxmlForm f = new VxmlForm("test", null, VxmlUtils.createGrammar(new String[]{"1","2"}, 
                              new String[]{"out.pressed=1","out.pressed=2"}),
                              VxmlUtils.createLocalGoto("next"));
    try {
    new VxmlDocument(f).write(new OutputStreamWriter(new FileOutputStream("log.txt")));
    } catch (Exception e) { fail(); }
  }
}
