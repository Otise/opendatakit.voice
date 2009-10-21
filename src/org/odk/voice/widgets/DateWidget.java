package org.odk.voice.widgets;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.javarosa.core.model.data.IAnswerData;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.xform.PromptElement;

public class DateWidget extends QuestionWidget {

  public DateWidget(PromptElement p) {
    super(p);
  }
  
  @Override
  public String[] getPromptStrings() {
    // TODO Auto-generated method stub
    return null;
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
