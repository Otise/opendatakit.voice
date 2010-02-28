package org.odk.voice.local;

import java.util.ListResourceBundle;


/**
 * The default ResourceBundle for local content.
 * ODK Voice can be translated to other languages by creating locale-specific Resource bundles,
 * e.g. Resources_de for German.
 * 
 * The locale code needs to be mapped to the language code in an XForm using the OdkLocales class.
 * 
 * @author alerer
 *
 */
public class Resources extends ListResourceBundle {
  public Object[][] getContents() {
      return contents;
  }
  
  static final Object[][] contents = {
  // LOCALIZE THIS
      {ResourceKeys.BEGIN_SESSION_INSTRUCTIONS,
        "Please select a survey from the following options."
      },
      {ResourceKeys.FORM_START, 
        "Welcome to the %1$s."
      },
      {ResourceKeys.FORM_START_PRESS_1_TO_BEGIN,
        "Press 1 to begin the survey."
      },
      {ResourceKeys.FORM_START_LANGUAGES, 
        "To change language, press 9."
      },
      {ResourceKeys.FORM_END, 
        "Thank you for completing the %1$s. You can press star to review " + 
        "and change your answers, or simply hang up and the survey will be saved."
      },                              
      {ResourceKeys.FORM_RECONNECT, 
        "Welcome back to the %1$s. You currently have an uncompleted " + 
        "survey in progress. If you would like to continue with that survey, press 1. " + 
        "If you'd like to start over, press 2."
      },                         
      {ResourceKeys.QUESTION_X_OF_Y, 
          "Question %1$d of %2$d."
      },
      {ResourceKeys.QUESTION_X,
          "Question %1$d."
      },
      {ResourceKeys.INT_INSTRUCTIONS,
          "Please enter the number on the keypad."
      },
      {ResourceKeys.SELECT_1_INSTRUCTIONS,
        ""
      },
      {ResourceKeys.SELECT_1_PRESS,
        "Press %1$d for "
      },
      {ResourceKeys.SELECT_INSTRUCTIONS,
        "For each option, press 1 for yes and 2 for no."
      },
      {ResourceKeys.SELECT_NONE,
        "none of the above."
      },
      {ResourceKeys.ANSWER_INVALID,
        "Sorry, this answer was invalid. Please try again."
      },
      {ResourceKeys.ANSWER_REQUIRED_BUT_EMPTY,
        "Sorry, this question is required. Please answer before continuing the survey."
      },
      {ResourceKeys.DATE_INSTRUCTIONS_YEAR,
        "First, enter the four-digit year."
      },
      {ResourceKeys.DATE_INSTRUCTIONS_MONTH,
        "Now, enter the two-digit month; for example, you can enter zero one for January, " + 
        "or eleven for November."
      },
      {ResourceKeys.DATE_INSTRUCTIONS_DAY,
        "Now, enter the two-digit day of the month."
      },
      {ResourceKeys.AUDIO_INSTRUCTIONS,
        //"Press 1 to begin recording, and record your answer after the beep. You can press any key when " +
        //"you are finished recording."
        "After the beep, please slowly and clearly say your answer. " +
        "You can press any key when you are finished recording."
      },
      {ResourceKeys.ANSWER_CONFIRMATION_KEYPAD,
        "You entered "
      },
      {ResourceKeys.ANSWER_CONFIRMATION_VOICE,
        "You said "
      },
      {ResourceKeys.ANSWER_CONFIRMATION_OPTIONS,
        "Press 1 if that is correct, or 2 to try again."
      },
      {ResourceKeys.INFO_CONFIRMATION,
        "Press 1 to continue, or 2 to repeat this information."
      },
      {ResourceKeys.THANK_YOU,
        "Thank you."
      },
      {ResourceKeys.RECORD_PROMPT_INSTRUCTIONS,
        "Press 1 to start recording this prompt or 3 to skip it; " + 
        "press any key when you're finished recording."
      },
      {ResourceKeys.PLEASE_HOLD,
        "Please hold."
      },
      {ResourceKeys.CHANGE_LANGUAGE_INSTRUCTIONS,
        "Please select your language."
      },
      {ResourceKeys.STRING_INSTRUCTIONS,
        "Please enter your answer letter by letter. " +
        "Press the pound key when you are finished."
      },
      {ResourceKeys.STRING_CONFIRM_INSTRUCTIONS,
        "Press 1 if that is correct, 2 for more options, 3 to try again, and 4 to continue."
      },
      {ResourceKeys.STRING_CONFIRM_ITEM,
        "Did you type "
      },
      {ResourceKeys.STRING_CONFIRM_NO_MORE_MATCHES,
        "Sorry, no more matches. Press 1 to continue, or 2 to try again."
      },
      {ResourceKeys.GOODBYE,
        "Goodbye."
      },
      {ResourceKeys.NO_INPUT,
        "You didn't enter anything on the keypad. Please listen to the options again."
      },
      {ResourceKeys.NO_MATCH,
        "The key you pressed was not one of the choices. Please listen to the options again."
      },
      {ResourceKeys.PRESS_STAR_TO_REPEAT,
        "Press star to repeat the question."
      },
      {ResourceKeys.PRESS_STAR_TO_REPEAT_INITIAL,
        "You can press star at any time to repeat the current question."
      },
  // END OF MATERIAL TO LOCALIZE
  };
}
