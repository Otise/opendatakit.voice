package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;

import org.javarosa.core.model.data.IAnswerData;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.xform.PromptElement;

/**
 * Not implemented.
 * 
 * @author alerer
 *
 */
public class DecimalWidget extends QuestionWidget {
  
  public DecimalWidget(PromptElement p) {
    super(p);
  }
  

  @Override
  public void getPromptVxml(Writer out) throws IOException {
    // TODO Auto-generated method stub
    
  }


  @Override
  public IAnswerData getAnswer(String stringData, MultiPartFormData binaryData)
      throws IllegalArgumentException {
    // TODO Auto-generated method stub
    return null;
  }

}
