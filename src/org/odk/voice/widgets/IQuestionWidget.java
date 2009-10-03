package org.odk.voice.widgets;

import java.io.InputStream;

import org.javarosa.core.model.data.IAnswerData;

public interface IQuestionWidget {
  
  
  /**
   * 
   * @return A string containing the VoiceXML rendering this question.
   */
  public String getPromptVxml();
  
  /**
   * 
   * @return A list of strings used in this question's prompts.
   */
  public String[] getPromptStrings();
  
  /**
   * Process the data returned from the vxml client to produce an IAnswerData object.
   * 
   * @param stringData The contents of the stringData parameter from the vxml client, or null if the parameter is not sent.
   * @param binaryData The contents of the request body from the vxml client, or null if there is no request body.
   * @return An IAnswerData object.
   * @throws IllegalArgumentException If the returned data is invalid.
   */
  public IAnswerData getAnswer(String stringData, InputStream binaryData) throws IllegalArgumentException;
  
}
