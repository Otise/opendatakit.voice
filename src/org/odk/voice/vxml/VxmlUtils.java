package org.odk.voice.vxml;

public class VxmlUtils {
  
  
  public static void indent(String s, int level) {
    String indent = "";
    for (int i=0; i < level; indent=indent+"  ", i++);
    s = indent + s;
    indent = "\n" + indent;
    s.replaceAll("\n", indent);
  }
}
