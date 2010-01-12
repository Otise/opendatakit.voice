package org.odk.voice.vxml;

import org.apache.commons.lang.StringEscapeUtils;

public class VxmlUtils {
  
  
  public static String indent(String s, int level) {
    String indent = "";
    for (int i=0; i < level; indent=indent+"  ", i++);
    s = indent + s;
    indent = "\n" + indent;
    return s.substring(0, s.length()-1).replaceAll("\n", indent) + s.substring(s.length()-1);
  }
  
  public static String createGrammar(String[] keys, String[] tags) {
    StringBuilder grammar = new StringBuilder(
    "<grammar mode=\"dtmf\"\n" +
    "    type=\"application/srgs+xml\"\n" +
    "    root=\"TOPLEVEL\"\n" +
    "    tag-format=\"semantics/1.0\"\n" +
    "    version=\"1.0\">\n" +
    "  <rule id=\"TOPLEVEL\" scope=\"public\">\n" +
    "  <one-of>\n");
    
    for (int i = 0; i < keys.length; i++) {
      grammar = grammar.append("    <item> " + keys[i] + " <tag>" + tags[i] + "</tag> </item>\n");
    }
    grammar.append("  </one-of></rule>\n");
    grammar.append("</grammar>\n");
    return grammar.toString();
  }
  
  public static String getWav(String audio){
    return (int) Math.abs(audio.hashCode()) + ".wav";
  }
  
  public static String getAudio(String text, String audio){
    if (audio == null || audio == "") return text;
    else
      return "<audio src=\"audio/" + VxmlUtils.getWav(audio) + "\">\n" + 
           "  " + (text==null?"":text) + "\n" +
           "</audio>\n";
  }
  
  public static String getAudio(String textAndAudio){
    return getAudio(textAndAudio, textAndAudio);
  }
  
  public static String createLocalGoto(String nextUrl){
    return "<goto next=\"#" + StringEscapeUtils.escapeHtml(nextUrl) + "\" />";
  }
  
  public static String createRemoteGoto(String nextUrl){
    return "<submit next=\"" + StringEscapeUtils.escapeHtml(nextUrl) + "\" namelist=\"sessionid\" />";
  }
  
  public static String createSubmit(String nextUrl, String... namelist){
    String nl = "sessionid";
    for (String name : namelist) {
      nl = nl + " " + name;
    }
    return "<submit next=\"" + StringEscapeUtils.escapeHtml(nextUrl) + "\" namelist=\"" + nl + "\"/>";
  }
  
  public static String createMultipartSubmit(String nextUrl, String... namelist) {
    String nl = "sessionid";
    for (String name : namelist) {
      nl = nl + " " + name;
    }
    return "<submit next=\"" + StringEscapeUtils.escapeHtml(nextUrl) + "\" method=\"POST\" enctype=\"multipart/form-data\" namelist=\"" + nl + "\"/>";
  }
  
  /**
   * 
   * @param name Variable name.
   * @param expr Expression for variable value.
   * @param stringExpr If true, expr is quoted (i.e. it should be a string). 
   * Otherwise, the VXML interpreter will treat it as ECMAScript.
   * @return
   */
  public static String createVar(String name, String expr, boolean stringExpr) {
    if (stringExpr)
      return"<var name=\"" + name + "\" expr=\"'" + expr + "'\"/>";
    else
      return"<var name=\"" + name + "\" expr=\"" + expr + "\"/>";
  }
}
