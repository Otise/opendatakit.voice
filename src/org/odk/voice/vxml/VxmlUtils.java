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
  
  /**
   * <p>Create a VXML &lt;grammar&gt; node with the given keys and tags.</p>
   * Example:
   * <code>createGrammar(new String[]{"1","4"},new String[]{"goforward","goback"})</code>
   * would return a grammar with two options, (1 and 4 on the keypad). 1 would result in 
   * the field being equal to "goforward", and 4 to "goback".
   * @param keys Grammar keys.
   * @param tags Grammar tags associated with the corresponding key.
   * @return The grammar VXML string.
   */
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
  
  /**
   * Use {@link getAudio} instead of this except for special cases.
   * @param audio
   * @return
   */
  public static String getWav(String audio){
    return (int) audio.hashCode() + ".wav";
  }
  
  /**
   * Creates an &lt;audio&gt; VXML node.
   * @param text The audio prompt text as it should be played in TTS.
   * @param audio The audio prompt text as it will be given to the voice actor to record.
   * @return The VXML node.
   */
  public static String getAudio(String text, String audio){
    if (audio == null || audio == "") return text;
    else
      return "<audio src=\"audio/" + VxmlUtils.getWav(audio) + "\">\n" + 
           "  " + (text==null?"":text) + "\n" +
           "</audio>\n";
  }
  
  /**
   * @see #getAudio(String, String)
   */
  public static String getAudio(String textAndAudio){
    return getAudio(textAndAudio, textAndAudio);
  }
  
  public static String createLocalGoto(String nextUrl){
    return "<goto next=\"#" + StringEscapeUtils.escapeHtml(nextUrl) + "\" />";
  }
  
  public static String createRemoteGoto(String nextUrl){
    return "<submit next=\"" + StringEscapeUtils.escapeHtml(nextUrl) + "\" namelist=\"sessionid\" />";
  }
  
  /**
   * Creates a VXML &lt;submit&gt; node, which submits a set of values to a URL
   * and requests a new VXML document.
   * 
   * @param nextUrl The URL to submit to.
   * @param namelist A list of parameter names to pass in the http request. Note that 
   * 'sessionid' is automatically passed; you may also want to pass other parameters specific 
   * to your dialogue, e.g. the 'action' parameter.
   * @return The submit node.
   */
  public static String createSubmit(String nextUrl, String... namelist){
    String nl = "sessionid";
    for (String name : namelist) {
      nl = nl + " " + name;
    }
    return "<submit next=\"" + StringEscapeUtils.escapeHtml(nextUrl) + "\" namelist=\"" + nl + "\"/>";
  }
  
  /**
   * Creates a submit with a multipart encoding.
   * @see #createSubmit.
   */
  public static String createMultipartSubmit(String nextUrl, String... namelist) {
    String nl = "sessionid";
    for (String name : namelist) {
      nl = nl + " " + name;
    }
    return "<submit next=\"" + StringEscapeUtils.escapeHtml(nextUrl) + "\" method=\"POST\" enctype=\"multipart/form-data\" namelist=\"" + nl + "\"/>";
  }
  
  /**
   * Create a VXML &lt;var&gt; node.
   * For example, createVar("action","NEXT_PROMPT",true) would return
   * <code><var name="action" expr="'NEXT_PROMPT'"/></code>.
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
