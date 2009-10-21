package org.odk.voice.vxml;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.odk.voice.servlet.FormVxmlServlet;

/**
 * VxmlDocument, VxmlForm, VxmlField, VxmlPrompt, etc., are a set of utility classes for constructing 
 * VXML dialogues. They are used extensively by the {@link org.odk.voice.widgets} classes, but a Widget 
 * class could also output VXML independently of these helper classes.
 * 
 * @author alerer
 *
 */
public class VxmlDocument {
  
  String contents;
  VxmlForm[] forms = null;
  
  public VxmlDocument(VxmlForm... forms){
    for (VxmlForm f: forms)
      assert(f != null);
    this.forms = forms;
  }
  
  private static final String maintainer = "adam.lerer@gmail.com";
  
  private static final String vxmlHeader = 
  		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
  		"<vxml version = \"2.1\" >\n" +
  		"<meta name=\"maintainer\" content=\"" + maintainer + "\"/>\n";

  private static final String vxmlFooter =
    "<catch event=\"connection.disconnect.hangup\">\n" +
    "  " + VxmlUtils.createRemoteGoto(FormVxmlServlet.ADDR + "?action=HANGUP") + "\n" +
    "</catch>\n" +
    "</vxml>\n";

  public void setContents(String contents){
    this.contents = contents;
  }
  public void write(Writer out) throws IOException {
    out.write(vxmlHeader);
    if (contents != null)
      out.write(contents);
    for (VxmlForm f: forms)
      f.write(out);
    out.write(vxmlFooter);
    out.flush();
  }
  
}