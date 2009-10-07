package org.odk.voice.vxml;

import java.io.IOException;
import java.io.Writer;

import org.odk.voice.constants.FileConstants;

public class VxmlPrompt {
  private String vxml;
  
  public VxmlPrompt(String vxml) {
    this.vxml = vxml;
  }
  
  public void write(Writer out) throws IOException {
    out.write(VxmlUtils.indent(vxml, 3));
  }
    

}
