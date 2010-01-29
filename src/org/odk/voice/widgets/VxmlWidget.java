package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Interface for classes that render a VoiceXML document.
 * 
 * @author alerer
 *
 */
public interface VxmlWidget {
  
  /**
   * 
   * @param out A writer to write the VoiceXML to.
   */
  public void getPromptVxml(Writer out) throws IOException;
  
  /**
   * 
   * @return A list of strings used in this question's prompts.
   */
  public List<String> getPromptStrings();
  
}
