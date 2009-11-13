package org.odk.voice.constants;

public class StringConstants {
  

  public static String formStartPrompt(String formName) {
    return 
    "Welcome to the " + formName + ". You can press star at " +
    "any time to reach the main menu. Press 1 to begin the survey." +
    "If you need to hang up, you can call back and continue the survey "  +
    "where you left off. ";
  }
  
  public static String formEndPrompt(String formName) {
    return
    "Thank you for completing the " + formName + ". You can press star to review " + 
    "and change your answers, or simply hang up and the survey will be saved.";
  }
  
  public static String reconnectPrompt(String formName) {
    return
    "Welcome back to the " + formName + ". You currently have an uncompleted " + 
    "survey in progress. If you would like to continue with that survey, press 1. " + 
    "If you'd like to start over, press 2.";
  }
  
  public static String questionXOfY (int current, int total) {
    return
    "Question " + current + " of " + total;
  }
  
  public static String intInstructions = 
    "Please enter the number on the keypad.";
  
  public static String select1Instructions = 
    "";
  
  public static String select1Press(int index) { 
    return "Press " + index + " for ";
  }
  
  public static String selectInstructions = 
    "For each option, press 1 for yes and 2 for no.";
  
  public static final String selectNone = 
    "none of the above.";
  
  public static String dateInstructionsYear = 
    "First, enter the four-digit year.";
  
  public static String dateInstructionsMonth = 
    "Now, enter the two-digit month; for example, you can enter zero one for January, " + 
    "or eleven for November. You can also press pound to skip the month and date.";
  
  public static String dateInstructionsDay = 
    "Now, enter the two-digit day of the month. You can also press pound to skip the day.";
  
  public static String audioInstructions = 
    "Press 1 to begin recording, and record your answer after the beep. You can press any key when " +
    "you are finished recording.";
  
  public static String answerConfirmationKeypad =
    "You entered ";
  
  public static String answerConfirmationAnd = 
    "and";
  
  public static String answerConfirmationVoice = 
    "You said ";
    
  public static String answerConfirmationOptions = 
    "Press 1 if that is correct, or 2 to try again.";
  
  public static String thankYou = 
    "Thank you.";

  public static String recordPromptInstructions = 
    "Press 1 to start recording this prompt or 3 to skip it; press any key when you're finished recording.";

  public static String pleaseHold = 
    "Please hold.";
}
