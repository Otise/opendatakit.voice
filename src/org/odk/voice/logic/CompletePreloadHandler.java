package org.odk.voice.logic;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.model.utils.IPreloadHandler;

public class CompletePreloadHandler implements IPreloadHandler {

  @Override
  public String preloadHandled() {
    return "complete";
  }

  @Override
  public IAnswerData handlePreload(String preloadParams) {
    return null;
  }

  @Override
  public boolean handlePostProcess(TreeElement node, String propName) {
      node.setAnswer(new StringData("true"));
      return true;
  }
  
}
