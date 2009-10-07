package org.odk.voice.vxml;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class VxmlDocument {
  
  String contents = "";
  VxmlForm[] forms = null;;
  
  public VxmlDocument(VxmlForm... forms){
    this.forms = forms;
  }
  
  private static final String maintainer = "adam.lerer@gmail.com";
  
  private static final String vxmlHeader = 
  		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
  		"<vxml version = \"2.1\" >\n" +
  		"<meta name=\"maintainer\" content=\"" + maintainer + "\"/>\n";

  private static final String vxmlFooter = 
    "</vxml>\n";

  public void setContents(String contents){
    this.contents = contents;
  }
  public void write(Writer out) throws IOException {
    out.write(vxmlHeader);
    out.write(contents);
    for (VxmlForm f: forms)
      f.write(out);
    out.write(vxmlFooter);
    out.flush();
  }
  
}