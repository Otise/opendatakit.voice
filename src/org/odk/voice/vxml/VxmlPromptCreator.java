package org.odk.voice.vxml;

/**
 * TODO(alerer): Need to explain this. It's really weird.
 * 
 * @author alerer
 *
 */
public interface VxmlPromptCreator {
  public VxmlPrompt createBasicPrompt(String vxml, String[] vxmlAudioStrings);
  public VxmlPrompt createPrompt(String... textAndAudio);
  public VxmlPrompt createPrompt(String[] text, String[] audio);
}
