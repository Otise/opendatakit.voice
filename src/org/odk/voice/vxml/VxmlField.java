package org.odk.voice.vxml;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class VxmlField extends VxmlSection{
  
  String name;
  
  VxmlPrompt prompt;
  
  Map<Integer,String> noinput = new HashMap<Integer,String>();//String noinput = "<reprompt/>";
  Map<Integer,String> nomatch = new HashMap<Integer,String>();
  
  String contents, filled, grammar = null;
  
  public VxmlField (String name, VxmlPrompt prompt, String grammar, String filled) {
    this.name = name;
    this.prompt = prompt;
    this.grammar = grammar;
    this.filled = filled;
    noinput.put(null, "<reprompt/>"); nomatch.put(null, "<reprompt/>");
  }
  
  /**
   * Add a &lt;nomatch&gt; element for this count. If count is null, the nomatch does not have a count
   * @param count
   * @param nomatch
   */
  public void setNomatch(Integer count, String nomatch) {
    this.nomatch.put(count, nomatch);
  }
//  public void setNoinput3(String noinput3) {
//    this.noinput3 = noinput3;
//  }
  /**
   * Add a &lt;noinput&gt; element for this count. If count is null, the noinput does not have a count
   * @param count
   * @param nomatch
   */
  public void setNoinput(Integer count, String noinput) {
    this.noinput.put(count, noinput);
  }
  
  public void setContents(String contents) {
    this.contents = contents;
  }

  @Override
  public void write(Writer out) throws IOException{
    out.write("<field name=\"" + name + "\">");
    if (prompt != null)
      out.write(prompt.toString());
    if (grammar != null)
      out.write(grammar);
    if (contents != null)
      out.write(contents);
    for (Integer i : noinput.keySet()) {
      out.write("<noinput" + (i==null?"":" count=\"" + i + "\"") + ">" + 
          noinput.get(i) + "</noinput>");
    }
    for (Integer i : nomatch.keySet()) {
      out.write("<nomatch" + (i==null?"":" count=\"" + i + "\"") + ">" + 
          nomatch.get(i) + "</nomatch>");
    }
//    out.write("    <noinput>\n");
//    out.write("      " + noinput + "\n");
//    out.write("    </noinput>\n");
//    out.write("    <nomatch>\n");
//    out.write("      " + nomatch + "\n");
//    out.write("    </nomatch>\n");
//    out.write("    <noinput count=\"3\">");
//    out.write("      " + noinput3 + "\n");
//    out.write("    </noinput>");
    out.write("<filled>" + filled + "</filled>");
    //out.write("      <if cond=\"action=='MAIN_MENU'\">\n");
    //out.write(VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action"));
    //out.write("      </if>\n");
//    out.write("      " + filled + "\n");
//    out.write("    </filled>\n");
    out.write("    </field>\n");
    
  }

}
