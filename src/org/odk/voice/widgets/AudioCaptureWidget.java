package org.odk.voice.widgets;

import java.io.InputStream;

import org.javarosa.core.model.data.IAnswerData;
import org.odk.voice.xform.PromptElement;

public class AudioCaptureWidget implements IQuestionWidget {
  
  private final PromptElement p;
  
  public AudioCaptureWidget(PromptElement p) {
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
  public IAnswerData getAnswer(String answer, InputStream data)
      throws IllegalArgumentException {
    // TODO Auto-generated method stub
    return null;
  }

}
