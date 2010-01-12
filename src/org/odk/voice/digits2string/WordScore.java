package org.odk.voice.digits2string;

public class WordScore {
  public String word;
  public double score;
  
  public WordScore(String word, double score) {
    this.word = word;
    this.score = score;
  }
  
  @Override
  public String toString(){
    return word;
  }
}
