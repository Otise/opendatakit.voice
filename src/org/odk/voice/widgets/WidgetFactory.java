/*
 * Copyright (C) 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.voice.widgets;

import org.apache.log4j.Logger;
import org.javarosa.core.model.Constants;
import org.odk.voice.constants.FormAttribute;
import org.odk.voice.xform.PromptElement;

/**
 * Convenience class that handles creation of widgets.
 * 
 * @author Adam Lerer (adam.lerer@gmail.com)
 */
public class WidgetFactory {
  
  private static org.apache.log4j.Logger log = Logger
  .getLogger(WidgetFactory.class);

    /**
     * Returns the appropriate QuestionWidget for the given PromptElement.
     * 
     * @param pe prompt element to be rendered
     * @param instancePath path to the instance file
     */
    static public QuestionWidget createWidgetFromPrompt(String sessionid, PromptElement pe, int instanceId) {
        QuestionWidget questionWidget = null;
        switch (pe.getQuestionType()) {
            case Constants.CONTROL_INPUT:
                if (pe.isReadonly()){
                  questionWidget = new InfoWidget(pe);
                } else {
                  switch (pe.getAnswerType()) {
                      case Constants.DATATYPE_DATE:
                          questionWidget = new DateWidget(pe);
                          break;
                      case Constants.DATATYPE_DECIMAL:
                          questionWidget = new DecimalWidget(pe);
                          break;
                      case Constants.DATATYPE_INTEGER:
                          questionWidget = new IntegerWidget(pe);
                          break;
                      default:
                        if (pe.getAttribute(FormAttribute.INT_DIGITS, true)) {
                          questionWidget = new DigitsWidget(pe);
                        } else {
                          questionWidget = new StringWidget(pe);
                        }
                        break;
                  }
                }
                break;
            case Constants.CONTROL_AUDIO_CAPTURE:
                questionWidget = new AudioCaptureWidget(pe, instanceId);
                break;
            case Constants.CONTROL_SELECT_ONE:
                questionWidget = new SelectOneWidget(pe);
                break;
            case Constants.CONTROL_SELECT_MULTI:
                questionWidget = new SelectMultiWidget(pe);
                break;
            default:
                log.error("String controls not supported.");
                questionWidget = new StringWidget(pe);
                break;
        }
        questionWidget.setSessionid(sessionid);
        return questionWidget;
    }

}
