package org.odk.voice.widgets;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.odk.voice.vxml.VxmlPrompt;
import org.odk.voice.vxml.VxmlPromptCreator;
import org.odk.voice.vxml.VxmlUtils;

public abstract class WidgetBase implements VxmlWidget, VxmlPromptCreator{

  private List<String> promptStrings = null;
  
  @Override
  public String[] getPromptStrings(){
    promptStrings = new ArrayList<String>();
    try {
      getPromptVxml(new OutputStreamWriter(new OutputStream(){
        public void write ( int b ) { }
      }));
    } catch (IOException e) {}
    return promptStrings.toArray(new String[]{});
  }
  
  public String getWmvPath(String audio) {
    if (promptStrings != null)
      promptStrings.add(audio);
    return audio.hashCode() + ".wmv";
  }
  
  public VxmlPrompt createBasicPrompt(String vxml, String[] promptAudioStrings){
    if (promptStrings != null && promptAudioStrings != null) {
      for (String s: promptAudioStrings)
        promptStrings.add(s);
    }
    final String vxml2 = vxml;
    return new VxmlPrompt(){
      public String getPromptString(){
        return vxml2;
      }
    };
  }
  
  public VxmlPrompt createPrompt(String[] text, String[] audio) {
    int length = text==null? audio.length : text.length;
    String vxml = "      <prompt>\n";
    for (int i = 0; i < length; i++){
      vxml = vxml + VxmlUtils.indent(VxmlUtils.getAudio(text==null?null:text[i], audio==null?null:audio[i]), 4);
    }
    vxml = vxml + "      </prompt>\n";
    return createBasicPrompt(vxml, audio);
  }
  
  public VxmlPrompt createPrompt(String... textAndAudio) {
    return createPrompt(textAndAudio, textAndAudio);
  }
  

}
