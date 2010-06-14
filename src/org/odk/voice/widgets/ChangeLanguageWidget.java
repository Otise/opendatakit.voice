package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.javarosa.core.util.OrderedHashtable;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlPrompt;
import org.odk.voice.vxml.VxmlSection;
import org.odk.voice.vxml.VxmlUtils;
import org.odk.voice.xform.FormHandler;

/**
 * Widged rendering a menu where user can change language.
 * @author alerer
 *
 */
public class ChangeLanguageWidget extends WidgetBase {
 
  private static org.apache.log4j.Logger log = Logger
  .getLogger(ChangeLanguageWidget.class);
  
  private String[] languages;
  
  public ChangeLanguageWidget(String[] languages) {
    this.languages = languages;
  }
  
  public void getPromptVxml(Writer out) throws IOException{
    
    if (languages == null) {
      log.warn("No languages.");
      return;
    }
    
    if (languages.length > 9)
      log.warn("More than 9 languages. Cannot handle more than 9 languages.");
    List<String> promptSegments = new ArrayList<String>();
    List<String> grammarKeys = new ArrayList<String>();
    List<String> grammarTags = new ArrayList<String>();
    
    promptSegments.add(getString(ResourceKeys.CHANGE_LANGUAGE_INSTRUCTIONS));
    
    
    int i = 1;
    for (String language : languages) {
          
          promptSegments.add(String.format(getString(ResourceKeys.SELECT_1_PRESS),i));
          promptSegments.add(language);
          grammarKeys.add(Integer.toString(i));
          grammarTags.add(language);
          
//          confPrompt.append("<" + (i==1?"if":"elseif") + " cond=\"answer=='" + itemValue + "'\"" + (i==1?"":"/") + ">\n");
//          confPrompt.append(VxmlUtils.getAudio(itemLabel));
//          addPromptString(itemLabel);
          
          i++;
      }
//      //addConfAudio(StringConstants.answerConfirmationOptions, confPrompt, confPromptStrings);
//      confPrompt.append("</if>");
      VxmlField answerField = createField("answer", 
          createPrompt(promptSegments.toArray(new String[]{})), 
          VxmlUtils.createGrammar(grammarKeys.toArray(new String[]{}), grammarTags.toArray(new String[]{})),
          VxmlUtils.createVar("action", VoiceAction.SET_LANGUAGE.name(), true) + 
          VxmlUtils.createSubmit(FormVxmlServlet.ADDR, new String[]{"action","answer"}));
      
//      
//      VxmlField actionField = createField("action", 
//          createPrompt(StringConstants.answerConfirmationOptions),
//          VxmlUtils.actionGrammar,
//          VxmlUtils.actionFilled(this));
      
      VxmlForm mainForm = new VxmlForm("main", answerField);
      
      VxmlDocument d = new VxmlDocument(sessionid, mainForm);
      d.write(out);
  }
}
