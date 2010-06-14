package org.odk.voice.constants;

public class FormAttribute {
  
  /**
   * For int question, reads the response as digits (e.g. one two three) instead of integer 
   * (e.g. one hundred twenty three).
   */
  public static final String INT_DIGITS = "digits";
  
  /**
   * Skip question instructions (only for audio questions currently).
   */
  public final static String AUDIO_SKIP_INSTRUCTIONS = "skipInstructions";
  
  /**
   * Skip the question count (e.g. `Question 1')
   */
  public static final String SKIP_QUESTION_COUNT = "skipQuestionCount";
  
  /**
   * Repeat the `Press * to repeat the question' text at eh question prompt.
   */
  public static final String REPEAT_QUESTION_OPTION = "repeatQuestionOption";
  
  /**
   * Skip confirmation dialogue for the question. Note: not implemented for all question types.
   */
  public static final String SKIP_CONFIRMATION = "skipConfirmation";
  
  /**
   * Use custom intro prompts instead of the default prompts. The intro prompt can be 
   * broken up into several prompts for recording purposes using {@link CUSTOM_PROMPTS_DELIM}, 
   * e.g. "Prompt 1|Prompt 2|Prompt 3".
   */
  public static final String CUSTOM_INTRO_PROMPTS = "customIntroPrompts";
  
  /**
   * See {@link CUSTOM_INTRO_PROMPTS}.
   */
  public static final String CUSTOM_END_PROMPTS = "customEndPrompts";
  
  /**
   * Delimiter for CUSTOM_INTRO_PROMPTS and CUSTOM_END_PROMPTS between successive prompts.
   */
  public static final String CUSTOM_PROMPTS_DELIM = "\\|"; //this is an escaped version of '|'
  
  /**
   * Disable resume functionality; i.e. when a user hangs up in the middle and a survey and calls 
   * back, he/she will not be given the option to resume if this attribute is set.
   */
  public static final String RESUME_DISABLED = "resumeDisabled";
  
  /**
   * For audio questions, set the max time that a user can record for..
   */
  public static final String AUDIO_MAX_TIME = "maxTime";
  
  /**
   * For string questions, set a custom corpus ('dictionary') to select answers from.
   * Not yet implemented.
   */
  public static final String STRING_CORPUS = "stringCorpus";
  
  /**
   * Enables functionality which attempts to make the user be quiet during the initial 
   * instructions, and will hang up and call back if they don't.
   * 
   * If forceQuiet is used, its value should be an integer n where n is the first 
   * intro prompt where forceQuiet should begin to apply.
   */
  public static final String FORCE_QUIET = "forceQuiet"; 
}
