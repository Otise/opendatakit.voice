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
  
  String contents = "";
  String id;
  List<VxmlField> fields = new ArrayList<VxmlField>();
  
  public VxmlForm(String id){
    this.id = id;
  }
  
  public VxmlForm(String id, VxmlField field){
    this(id);
    fields.add(field);
  }
  
  public void setContents (String contents) {
    this.contents = contents;
  }
  
  public void write(Writer out) throws IOException {
    out.write(String.format(formHeader, id));
    for (VxmlField f: fields)
      f.write(out);
    out.write(formFooter);
  }
}
