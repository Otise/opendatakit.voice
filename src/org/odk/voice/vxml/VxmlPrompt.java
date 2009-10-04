package org.odk.voice.vxml;

import java.io.IOException;
import java.io.Writer;

import org.odk.voice.constants.FileConstants;

public class VxmlPrompt {
  private String[] text;
  private String[] audio;
  int length;
  
  public VxmlPrompt(String[] text, String[] audio) {
    assert(text != null || audio != null);
    assert(text == null || audio == null || text.length == audio.length);
    this.text = text;
    this.audio = audio;
    this.length = text.length;
  }
  public VxmlPrompt(String text, String audio) {
    this(text==null?null:new String[]{text}, audio==null?null:new String[]{audio});
  }
  public VxmlPrompt(String[] textAndAudio) {
    this(textAndAudio, textAndAudio);
  }
  public VxmlPrompt(String textAndAudio) {
    this(textAndAudio, textAndAudio);
  }
  
  public void write(Writer out) throws IOException {
    out.write("      <prompt>\n");
    for (int i = 0; i < length; i++){
      out.write("        <audio " + (audio==null?"":"src=\"" + getWmv(audio[i]) + "\"") + ">\n");
      out.write("          " + (text==null?"":text[i]) + "\n");
      out.write("        </audio>\n");
    }
      out.write("      </prompt>\n");
  }
    
  private String getWmv(String audio){
    return FileConstants.PROMPT_AUDIO_PATH + audio.hashCode() + ".wmv";
  }
}
