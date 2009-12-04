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

public class ChangeLanguageWidget extends WidgetBase {
 
  private static org.apache.log4j.Logger log = Logger
  .getLogger(ChangeLanguageWidget.class);
  
  private FormHandler fh;
  
  public ChangeLanguageWidget(FormHandler fh) {
    this.fh = fh;
  }
  
  public void getPromptVxml(Writer out) throws IOException{
    
    
    String[] languages = fh.getLanguages();
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
      VxmlField answerField = new VxmlField("answer", 
          createPrompt(promptSegments.toArray(new String[]{})), 
          VxmlUtils.createGrammar(grammarKeys.toArray(new String[]{}), grammarTags.toArray(new String[]{})),
          "<var name=\"action\" value=\"" + VoiceAction.SET_LANGUAGE.name() + "\"/>" +
          VxmlUtils.createSubmit(FormVxmlServlet.ADDR, new String[]{"action","answer"}));
      
//      
//      VxmlField actionField = new VxmlField("action", 
//          createPrompt(StringConstants.answerConfirmationOptions),
//          VxmlUtils.actionGrammar,
//          VxmlUtils.actionFilled(this));
      
      VxmlForm mainForm = new VxmlForm("main", answerField, getActionField(false));
      
      VxmlDocument d = new VxmlDocument(sessionid, mainForm);
      d.write(out);
  }
}
