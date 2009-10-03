package org.odk.voice.widgets;

import java.io.InputStream;

import org.javarosa.core.model.data.IAnswerData;
import org.odk.voice.xform.PromptElement;

public class DateWidget implements IQuestionWidget {
  
  private final PromptElement p;
  
  public DateWidget(PromptElement p) {
    this.p = p;
  }
  
  @Override
  public String[] getPromptStrings() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getPromptVxml() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IAnswerData getAnswer(String stringData, InputStream binaryData)
      throws IllegalArgumentException {
    // TODO Auto-generated method stub
    return null;
  }

}
