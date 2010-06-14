package org.odk.voice.digits2string;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class StringPredictor {  
  
  private static org.apache.log4j.Logger log = Logger
  .getLogger(StringPredictor.class);
  
  public char[] charToInt = new char[256];
  private void initCharToInt(String chars){
    Arrays.fill(charToInt, '@');
    String[] charsArray = chars.split(",");
    for (int i = 0; i < 10; i++) {
      char[] cs = charsArray[i].toCharArray();
      for (char c : cs)
        charToInt[c] = String.valueOf(i).charAt(0);
    }
  }
  
  Corpus c;
  
  public StringPredictor(Corpus c) {
    this.c = c;
    initCharToInt(",,abc,def,ghi,jkl,mno,pqrs,tuv,wxyz");
  }

  public static final double ERROR_PROB = 0.1;
  public static final int MAX_WORD_SIZE = 30;
  public static final Object PREDICTION_INCORRECT = "@@@";
  
  public String wordToDigits(String word) {
    int len = word.length();
    char[] res = new char[len];
    word.getChars(0, len, res, 0);
    for (int i = 0; i < len; i++)
      res[i] = charToInt[res[i]];
    return new String(res);
  }
  
  public WordScore[] predict(String input, int nbest) {
    if (input == null)
      return null;
    String[] words = input.split("0");
    if (words.length == 1) {
      return predictWord(input, nbest);
    } else { 
      // if multiple words, can only predict one thing, otherwise exponential growth
      // alternatively, we could give choices for each word.
      WordScore[] predWords = new WordScore[words.length];
      for (int i = 0; i < words.length; i++)
        predWords[i] = predictWord(words[i]);
      double totalScore = 1;
      for (int i = 0; i < predWords.length; i++) {
        totalScore *= predWords[i].score;
      }
      return new WordScore[]{new WordScore(join(predWords, " "), totalScore)};
    }
  }
  
  public static String join(Object[] s, String sep) {
    String res = "";
    if (s.length == 0)
      return res;
    for (int i = 0; i < s.length - 1; i++)
      res += s[i] + sep;
    res += s[s.length-1];
    return res;
  }
  
  public WordScore predictWord(String input) {
    return predictWord(input, 1)[0];
  }
  
  public WordScore[] predictWord(String input, int nbest) {
    if (input.length() > MAX_WORD_SIZE) {
      throw new IllegalArgumentException("Input too large!");
    }
    if (nbest < 1) {
      throw new IllegalArgumentException("nmax < 0");
    }
    
    // TODO: use a heap here for better performance
    WordScore[] top = new WordScore[nbest];
    Arrays.fill(top, new WordScore("???", 0.0));
    int min_index = 0;
    
    LevenshteinDistanceCalculator ldc = new LevenshteinDistanceCalculator(MAX_WORD_SIZE);
    Iterator<String> iter = c.getIterator();
    
    
    while (iter.hasNext()) {
      String word = iter.next();
      if (word.length() > MAX_WORD_SIZE) {
        throw new IllegalArgumentException("Corpus word " + word + " too large!");
      }
      String wordDigits = wordToDigits(word.toLowerCase());
      int dist = ldc.getDistance(input, wordDigits);
      double prior = c.getPrior(word);
      //System.out.println(word + ", " + wordDigits + ", " + input + ", " + prior);
      if (prior < 0) {
        throw new IllegalArgumentException("Corpus word " + word + " has negative prior!");
      }
      double score = prior * Math.pow(ERROR_PROB, dist);
      // System.out.println(word + ": " + score);
      if (score > top[min_index].score) {
        top[min_index] = new WordScore(word, score);
        min_index = minIndex(top);
      }
    }
    //System.out.println("Max::: " + max_word + ": " + max_score);
    Arrays.sort(top, new Comparator<WordScore>(){

      @Override
      public int compare(WordScore o1, WordScore o2) {
        // TODO Auto-generated method stub
        return - new Double(o1.score).compareTo(new Double(o2.score));
      }
    });
    
    return top;
  }
  
  public void updatePrior(String selected) {
    c.updatePrior(selected, c.getPrior(selected) + 1);
  }
  
  public void updatePrior(String[] options, int selectedIndex) {
    updatePrior (options[selectedIndex]);
    // we might want to decrease the priors for the other options ???
  }
  
  private int minIndex(WordScore[] top) {
    int minIndex = -1;
    double minScore = Double.MAX_VALUE;
    for (int i = 0; i < top.length; i++) {
      if (top[i].score < minScore) {
        minScore = top[i].score;
        minIndex = i;
      }
    }
    return minIndex;
  }
   
}
