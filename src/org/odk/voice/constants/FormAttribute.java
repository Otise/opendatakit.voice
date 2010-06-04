package org.odk.voice.constants;

public class FormAttribute {
  public static final String INT_DIGITS = "digits";
  public final static String AUDIO_SKIP_INSTRUCTIONS = "skipInstructions";
  public static final String SKIP_QUESTION_COUNT = "skipQuestionCount";
  public static final String REPEAT_QUESTION_OPTION = "repeatQuestionOption";
  public static final String SKIP_CONFIRMATION = "skipConfirmation";
  public static final String CUSTOM_INTRO_PROMPTS = "customIntroPrompts";
  public static final String CUSTOM_INTRO_PROMPTS_DELIM = "\\|"; //this is an escaped version of '|'
  public static final String RESUME_DISABLED = "resumeDisabled";
  public static final String AUDIO_MAX_TIME = "maxTime";
  public static final String STRING_CORPUS = "stringCorpus";
  // if forceQuiet is used, its value should be an integer n where n is the first 
  // intro prompt where forceQuiet should begin to apply.
  public static final String FORCE_QUIET = "forceQuiet"; 
}
