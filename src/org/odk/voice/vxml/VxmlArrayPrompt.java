package org.odk.voice.vxml;

import java.io.IOException;
import java.io.Writer;

import org.odk.voice.constants.FileConstants;

public class VxmlArrayPrompt extends VxmlPrompt {
  private String[] text;
  private String[] audio;
  int length;
  
  public VxmlArrayPrompt(String[] text, String[] audio) {
    super("");
    assert(text != null || audio != null);
    assert(text == null || audio == null || text.length == audio.length);
    this.text = text;
    this.audio = audio;
    this.length = text==null? audio.length : text.length;
  }
  public VxmlArrayPrompt(String text, String audio) {
    this(text==null?null:new String[]{text}, audio==null?null:new String[]{audio});
  }
  public VxmlArrayPrompt(String[] textAndAudio) {
    this(textAndAudio, textAndAudio);
  }
  public VxmlArrayPrompt(String textAndAudio) {
    this(textAndAudio, textAndAudio);
  }
  
  public void write(Writer out) throws IOException {
    out.write("      <prompt>\n");
    for (int i = 0; i < length; i++){
      out.write(VxmlUtils.indent(VxmlUtils.getAudio(text==null?null:text[i], audio==null?null:audio[i]), 4));
    }
    out.write("      </prompt>\n");
  }
}
