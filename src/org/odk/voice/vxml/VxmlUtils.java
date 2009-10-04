package org.odk.voice.vxml;

import java.util.Map;

public class VxmlUtils {
  
  
  public static void indent(String s, int level) {
    String indent = "";
    for (int i=0; i < level; indent=indent+"  ", i++);
    s = indent + s;
    indent = "\n" + indent;
    s.replaceAll("\n", indent);
  }
  
  public static String createGrammar(Map<String, String> grammarMap) {
    StringBuilder grammar = new StringBuilder(
    "<grammar mode=\"dtmf\"\n" +
    "    type=\"application/srgs+xml\"\n" +
    "    root=\"TOPLEVEL\"\n" +
    "    tag-format=\"semantics/1.0\"\n" +
    "    version=\"1.0\">\n" +
    "  <rule id=\"TOPLEVEL\" scope=\"public\">\n" +
    "  <one-of>\n");
    
    for (Map.Entry<String,String> e : grammarMap.entrySet()) {
      grammar = grammar.append("<item> " + e.getKey() + " <tag> " + e.getValue() + "</tag> </item>\n");
    }
    grammar.append("</one-of></rule></grammar>");
    return grammar.toString();
  }
}
