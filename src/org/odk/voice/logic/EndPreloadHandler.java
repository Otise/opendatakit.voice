package org.odk.voice.logic;

import org.javarosa.core.model.data.BooleanData;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.model.utils.IPreloadHandler;

public class EndPreloadHandler implements IPreloadHandler {

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
      node.setAnswer(new BooleanData(true));
      return true;
  }
  
}
