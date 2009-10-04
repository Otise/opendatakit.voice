package org.odk.voice.vxml;

import java.io.IOException;
import java.io.Writer;

public class VxmlField {
  
  String name;
  
  VxmlPrompt prompt;
  
  String noinput = "<reprompt/>";
  
  String nomatch = "<reprompt/>";
  
  String filled = "<reprompt/>";
  
  String grammar;
  
  public VxmlField (String name, VxmlPrompt prompt, String grammar){
    this.name = name;
    this.prompt = prompt;
    this.grammar = grammar;
  }
  
  public void write(Writer out) throws IOException{
    out.write("  <field name=\"" + name + "\">\n");
    prompt.write(out);
    out.write("    " + grammar + "\n");
    out.write("    <noinput>\n");
    out.write("      " + noinput + "\n");
    out.write("    </noinput>\n");
    out.write("    <nomatch>\n");
    out.write("      " + nomatch + "\n");
    out.write("    </nomatch>\n");
    out.write("    <filled>\n");
    out.write("      " + filled + "\n");
    out.write("    </filled>\n");
    out.write("    </grammar>\n");
    out.write("    </field>\n");
    
  }
}
