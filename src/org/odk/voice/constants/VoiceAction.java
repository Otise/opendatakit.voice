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
  SELECT_FORM,        // If multiple uploaded forms, selects a form from the list
  CURRENT_PROMPT,     // Replay current prompt
  NEXT_PROMPT,        // Goto next prompt
  PREV_PROMPT,        // Goto previous prompt
  SAVE_ANSWER,        // Save answer and go to next prompt
  RESUME_FORM,        // Resume a partially completed form from a previous call
  LANGUAGE_MENU,      // Enter the language menu to select a language
  SET_LANGUAGE,       // Set the language to the answer field
  MAIN_MENU,          // Not implemented
  ADMIN,              // Enter the admin menu for recording prompts
  HANGUP,             // User hung up
  GET_STRING_MATCHES, // For string question, find the matching words for a key sequence
  RESTART_SESSION,    // Delete partically completed form from a previous call, and start again
  NO_RESPONSE,        // User did not respond to a question after multiple prompts
  TOO_LOUD,           // If forceQuiet enabled, sent if calls is disconnected because user is too loud
}
