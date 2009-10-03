package org.odk.voice.constants;

public class StringConstants {
  
  public static String formStartPrompt(String formName) {
    return 
    "Welcome to the " + formName + ". You can press star at " +
    "any time to reach the main menu. Also, if you need to hang up, " + 
    "you can call back and continue the survey where you left off. " + 
    "Press 1 to begin the survey, or press 9 at any time for help and " + 
    "other options.";
  }
  
  public static String formEndPrompt(String formName) {
    return
    "Thank you for completing the " + formName + ". Press star to review " + 
    "and change your answers, or press pound to submit this survey.";
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
    "Please enter the number on the keypad. Press the pound key when you are finished.";
  
  public static String select1Instructions = 
    "";
  
  public static String select1Press(int index) { 
    return "Press " + index + " for ";
  }
  
  public static String selectInstructions = 
    "For each option, press 1 for yes and 2 for no.";
  
  public static String dateInstructionsYear = 
    "First, enter the four-digit year.";
  
  public static String dateInstructionsMonth = 
    "Now, enter the two-digit month; for example, you can enter zero one for January, " + 
    "or eleven for November. You can also press pound to skip the month and date.";
  
  public static String dateInstructionsDay = 
    "Now, enter the two-digit day of the month. You can also press pound to skip the day.";
  
  public static String audioInstructions = 
    "Please record your answer after the beep, and press the pound key when you are finished recording.";
  
  public static String answerConfirmationKeypad =
    "You entered {%answer%}.";
  
  public static String answerConfirmationAnd = 
    "and";
  
  public static String answerConfirmationVoice = 
    "You said {%answer%}.";
    
  public static String answerConfirmationOptions = 
    "Press 1 if that is correct, or 2 to try again.";
  
  public static String thankYou = 
    "Thank you.";
}
