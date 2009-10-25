package org.odk.voice.logic;

import java.util.HashMap;
import java.util.Map;

import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.StringData;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.model.utils.IPreloadHandler;

/**
 * In the JavaRosa core, preloads of type 'property' are handled by calling the 
 * <code>JavaRosaServiceProvider.instance().getPropertyManager().getProperty(propName, value)</code>. 
 * This retrieves string values from a static/singleton property map. In the case of ODK Voice, where 
 * a single instance of JavaRosa is handling multiple sessions with different properties simultaneously, 
 * properties need to be tied directly to the FormDef. Therefore, we are overriding the property preload 
 * handler in QuestionPreloader with this one.
 * 
 * @author alerer
 *
 */
public class PropertyPreloadHandler implements IPreloadHandler {

  public static final String PHONE_NUMBER_PROPERTY = "phonenumber";
  public static final String SESSION_ID_PROPERTY = "sessionid";
  private Map<String,String> properties = new HashMap<String,String>();
  
  @Override
  public String preloadHandled() {
    return "property";
  }

  @Override
  public IAnswerData handlePreload(String preloadParams) {
    String propname = preloadParams;
    String propval = properties.get(propname); // no longer calling static JR service
    StringData data = null;
    if (propval != null && propval.length() > 0) {
      data = new StringData(propval);
    }
    return data;
  }

  @Override
  public boolean handlePostProcess(TreeElement node, String propName) {
    IAnswerData answer = node.getValue();
    String value = (answer == null ? null : answer.getDisplayText());
    if (propName != null && propName.length() > 0 && value != null && value.length() > 0)
      properties.put(propName, value);
    return false;
  }
  
  public void setProperty(String propName, String propValue) {
    properties.put(propName, propValue);
  }

}
