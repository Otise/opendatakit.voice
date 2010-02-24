package org.odk.voice.widgets;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.data.SelectOneData;
import org.javarosa.core.model.data.helper.Selection;
import org.odk.voice.constants.VoiceAction;
import org.odk.voice.db.DbAdapter.FormMetadata;
import org.odk.voice.local.ResourceKeys;
import org.odk.voice.servlet.FormVxmlServlet;
import org.odk.voice.storage.MultiPartFormData;
import org.odk.voice.vxml.VxmlDocument;
import org.odk.voice.vxml.VxmlField;
import org.odk.voice.vxml.VxmlForm;
import org.odk.voice.vxml.VxmlUtils;

public class SelectFormWidget extends WidgetBase {
  
  private static org.apache.log4j.Logger log = Logger
  .getLogger(SelectFormWidget.class);
  
  List<FormMetadata> forms;
  
  public SelectFormWidget(List<FormMetadata> forms) {
    super();
    this.forms = forms;
  }
  
  public void getPromptVxml(Writer out) throws IOException{
    List<String> promptSegments = new ArrayList<String>();
    List<String> grammarKeys = new ArrayList<String>();
    List<String> grammarTags = new ArrayList<String>();
    //StringBuilder confPrompt = new StringBuilder();
    
    promptSegments.add(getString(ResourceKeys.BEGIN_SESSION_INSTRUCTIONS));
    
    if (forms.size() > 9) {
      log.warn("ODK Voice cannot handle more than 9 forms.");
      return;
    }
    
//    addPromptString(getString(ResourceKeys.ANSWER_CONFIRMATION_KEYPAD));
//    confPrompt.append(VxmlUtils.getAudio(getString(ResourceKeys.ANSWER_CONFIRMATION_KEYPAD)));
    
    for (int i = 0; i < forms.size(); i++) {
      FormMetadata f = forms.get(i);
      promptSegments.add(String.format(getString(ResourceKeys.SELECT_1_PRESS), i + 1));
      promptSegments.add(f.getTitle());
      grammarKeys.add(Integer.toString(i + 1));
      grammarTags.add(f.getName());
    }
    
    VxmlField answerField = createField("answer", 
        createPrompt(promptSegments.toArray(new String[]{})), 
        VxmlUtils.createGrammar(grammarKeys.toArray(new String[]{}), grammarTags.toArray(new String[]{})),
        VxmlUtils.createVar("action", VoiceAction.SELECT_FORM.name(), true) +
        VxmlUtils.createSubmit(FormVxmlServlet.ADDR, "action", "answer"));
      
    VxmlForm mainForm = new VxmlForm("main", answerField);
    VxmlDocument d = new VxmlDocument(sessionid, mainForm);
    d.write(out);
  }
 
}
