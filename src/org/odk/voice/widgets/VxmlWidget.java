package org.odk.voice.widgets;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.javarosa.core.model.data.IAnswerData;

public interface VxmlWidget {
  
  
//  /**
//   * 
//   * @return A string containing the VoiceXML rendering this question.
//   */
//  public String getPromptVxml();
  
  /**
   * 
   * @param out A writer to write the VoiceXML to.
   */
  public void getPromptVxml(Writer out) throws IOException;
  
  /**
   * 
   * @return A list of strings used in this question's prompts.
   */
  public String[] getPromptStrings();
  
}
