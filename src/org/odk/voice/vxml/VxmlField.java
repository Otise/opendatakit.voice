package org.odk.voice.vxml;

import java.io.IOException;
import java.io.Writer;

public class VxmlField {
  
  String name;
  
  VxmlPrompt prompt;
  
  String noinput = "<reprompt/>";
  
  String nomatch = "<reprompt/>";
  
  String filled = "<reprompt/>";
  
  public VxmlField (String name, VxmlPrompt prompt){
    this.name = name;
    this.prompt = prompt;
  }
  
  public void write(Writer out) throws IOException{
    out.write("  <field name=\"" + name + "\">\n");
    prompt.write(out);
    out.write("    <noinput>\n");
    out.write("      " + noinput + "\n");
    out.write("    </noinput>\n");
    out.write("    <nomatch>\n");
    out.write("      " + nomatch + "\n");
    out.write("    </nomatch>\n");
    out.write("    <filled>\n");
    out.write("      " + filled + "\n");
    out.write("    </filled>\n");
    out.write("    </field>\n");
    
  }
}
