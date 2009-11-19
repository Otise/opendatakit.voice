package org.odk.voice.vxml;

import java.io.IOException;
import java.io.Writer;

public class VxmlSection {
  String contents = null;
  
  public VxmlSection(){}
  public VxmlSection(String contents){
    this.contents = contents;
  }
  
  public void write(Writer out) throws IOException{
    out.write(contents);
  }
}
