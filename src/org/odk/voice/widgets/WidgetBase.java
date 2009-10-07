package org.odk.voice.widgets;

import java.io.IOException;
import java.io.StringWriter;

public abstract class WidgetBase implements IQuestionWidget{
  
  @Override
  public String getPromptVxml() {
    StringWriter sw = new StringWriter(); //we need to change getPromptVxml interface to use a writer natively
    try{
      getPromptVxml(sw);
    } catch (IOException e) {
      return "IOException";
    }
    return sw.toString();
  }
  
  // getAnswer here too?
}
