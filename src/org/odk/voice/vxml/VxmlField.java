package org.odk.voice.vxml;

import java.io.IOException;
import java.io.Writer;

import org.odk.voice.servlet.FormVxmlServlet;

public class VxmlField extends VxmlSection{
  
  String name;
  
  VxmlPrompt prompt;
  
  String noinput = "<reprompt/>";
  
  String nomatch = "<reprompt/>";
  
  String filled = "<reprompt/>";
  
  String contents = null;
  
  String grammar;
  
  public VxmlField (String name, VxmlPrompt prompt, String grammar, String filled) {
    this.name = name;
    this.prompt = prompt;
    this.grammar = grammar;
    this.filled = filled;
  }
  
  public void setNomatch(String nomatch) {
    this.nomatch = nomatch;
  }
  public void setNoinput(String noinput) {
    this.noinput = noinput;
  }
  
  public void setContents(String contents) {
    this.contents = contents;
  }

  @Override
  public void write(Writer out) throws IOException{
    out.write("  <field name=\"" + name + "\">\n");
    if (prompt != null)
      out.write(prompt.toString());
    //out.write(VxmlUtils.createGrammar(new String[]{"*"}, new String[]{"out.action='MAIN_MENU'"}));
    if (grammar != null)
      out.write(grammar);
    if (contents != null)
      out.write(contents);
    out.write("    <noinput>\n");
    out.write("      " + noinput + "\n");
    out.write("    </noinput>\n");
    out.write("    <nomatch>\n");
    out.write("      " + nomatch + "\n");
    out.write("    </nomatch>\n");
    out.write("    <filled>\n");
    //out.write("      <if cond=\"action=='MAIN_MENU'\">\n");
    //out.write(VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action"));
    //out.write("      </if>\n");
    out.write("      " + filled + "\n");
    out.write("    </filled>\n");
    out.write("    </field>\n");
    
  }
}
