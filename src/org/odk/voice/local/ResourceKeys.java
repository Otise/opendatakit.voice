package org.odk.voice.local;

/**
 * To add a new string resource to ODK Voice: 
 * <ol>
 * <li>Add a new key to ResourceKeys.java</li>
 * <li>Add a new mapping from the key to its default English value in Resources.java</li>
 * <li>Add mappings from the key to its language-specific value in Resources_xx.java</li>
 * </ol>
 * 
 * To use a string resource, call <code>resources.getString(ResourceKeys.KEY_NAME)</code>.
 * 
 * @author alerer
 *
 */
public class ResourceKeys {

  public static final String FORM_START = "formstartprompt";
  public static final String FORM_START_LANGUAGES = "formstartlanguagesprompt";
  public static final String FORM_END = "formendprompt";
  public static final String FORM_RECONNECT = "reconnectprompt";
  public static final String QUESTION_X_OF_Y = "questionxofy";
  public static final String QUESTION_X = "questionx";
  public static final String SELECT_1_INSTRUCTIONS = "select1instructions";
  public static final String INT_INSTRUCTIONS = "intinstructions";
  public static final String SELECT_1_PRESS = "select1press";
  public static final String SELECT_INSTRUCTIONS = "selectinstructions";
  public static final String SELECT_NONE = "selectnone";
  public static final String ANSWER_INVALID = "answerinvalid";
  public static final String ANSWER_REQUIRED_BUT_EMPTY = "answerrequiredbutempty";
  public static final String DATE_INSTRUCTIONS_YEAR = "dateinstructionsyear";
  public static final String DATE_INSTRUCTIONS_MONTH = "dateinstructionsmonth";
  public static final String DATE_INSTRUCTIONS_DAY = "dateinstructionsday";
  public static final String AUDIO_INSTRUCTIONS = "audioinstructions";
  public static final String ANSWER_CONFIRMATION_KEYPAD = "answerconfirmationkeypad";
  public static final String ANSWER_CONFIRMATION_VOICE = "answerconfirmationvoice";
  public static final String ANSWER_CONFIRMATION_OPTIONS = "answerconfirmationoptions";
  public static final String INFO_CONFIRMATION = "infoconfirmation";
  public static final String THANK_YOU = "thankyou";
  public static final String RECORD_PROMPT_INSTRUCTIONS = "recordpromptinstructions";
  public static final String PLEASE_HOLD = "pleasehold";
  public static final String CHANGE_LANGUAGE_INSTRUCTIONS = "changelanguageinstructions";
  public static final String STRING_INSTRUCTIONS = "stringinstructions";
  public static final String STRING_CONFIRM_INSTRUCTIONS = "stringconfirminstructions";
  public static final String STRING_CONFIRM_ITEM = "stringconfirmitem";
  public static final String STRING_CONFIRM_NO_MORE_MATCHES = "stringconfirmnomorematches";
  public static final String BEGIN_SESSION_INSTRUCTIONS = "beginsessioninstructions";
  public static final String GOODBYE = "goodbye";
  public static final String NO_INPUT = "noinput";
  public static final String NO_MATCH = "nomatch";
}
