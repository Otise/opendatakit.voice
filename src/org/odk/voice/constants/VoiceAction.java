package org.odk.voice.constants;

/**
 * VoiceActions control call flow between successive VoiceXML dialogues.
 * An 'action' parameter is passed to the FormVxmlServlet from each VoiceXML 
 * dialogue indicating which action the user took.
 * 
 * For example a question prompt dialog could return 
 * <ul>
 * <li>SAVE_ANSWER if the user submitted an answer to the question</li>
 * <li>NEXT_PROMPT if the user skipped the question</li>
 * <li>HANGUP if the user hung up</li>
 * </ul>
 * etc.
 * 
 * @author alerer
 *
 */
public enum VoiceAction {
  SELECT_FORM,
  CURRENT_PROMPT,
  NEXT_PROMPT,
  PREV_PROMPT,
  SAVE_ANSWER, 
  RESUME_FORM,
  LANGUAGE_MENU,
  SET_LANGUAGE,
  MAIN_MENU,
  ADMIN,
  HANGUP, 
  GET_STRING_MATCHES, 
  RESTART_SESSION, 
  NO_RESPONSE,
}
