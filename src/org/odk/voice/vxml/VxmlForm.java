package org.odk.voice.vxml;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class VxmlForm {
  
  private static final String formHeader = 
    "  <form id =\"%s\">\n";
  
  private static final String formFooter = 
    "  </form>\n";
  
  private String contents;
  
  String id;
  
  VxmlField[] fields = new VxmlField[]{};
  
  public VxmlForm(String id){
    this.id = id;
  }
  
  public VxmlForm(String id, VxmlField field){
    this(id);
    this.fields = new VxmlField[]{field};
  }
  
  public VxmlForm(String id, VxmlPrompt prompt, String grammar, String filled) {
    this(id);
    this.fields = new VxmlField[]{new VxmlField("main", prompt, grammar, filled)};
  }
  
  
  public void write(Writer out) throws IOException {
    out.write(String.format(formHeader, id));
    out.write(contents);
    for (VxmlField f: fields)
      f.write(out);
    out.write(formFooter);
  }

  public void setContents(String contents) {
    this.contents = contents;
  }

  public String getContents() {
    return contents;
  }
}
