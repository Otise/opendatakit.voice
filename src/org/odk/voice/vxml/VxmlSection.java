package org.odk.voice.vxml;

import java.io.IOException;
import java.io.Writer;

/**
 * Container for arbitrary code inside a VXML &lt;form&gt;. Usually used for 
 * &lt;block&gt; elements.
 * 
 * @author alerer
 *
 */
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
