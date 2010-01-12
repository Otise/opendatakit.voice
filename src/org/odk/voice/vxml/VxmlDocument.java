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
  String sessionid;
  String contents;
  VxmlForm[] forms = null;
  
  public VxmlDocument(String sessionid, VxmlForm... forms){
    this.sessionid = sessionid;
    for (VxmlForm f: forms)
      assert(f != null);
    this.forms = forms;
  }
  
  private static final String maintainer = "adam.lerer@gmail.com";
  
  private static final String vxmlHeader = 
  		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
  		"<vxml version=\"2.0\" xmlns=\"http://www.w3.org/2001/vxml\">\n" +
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
    out.write("<property name=\"inputmodes\" value=\"dtmf\"/>");
    if (sessionid != null)
      out.write(VxmlUtils.createVar("sessionid", sessionid, true));
    if (contents != null)
      out.write(contents);
    for (VxmlForm f: forms)
      f.write(out);
    out.write(vxmlFooter);
    out.flush();
  }
  
}