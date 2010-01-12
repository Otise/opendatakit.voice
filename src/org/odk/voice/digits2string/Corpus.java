package org.odk.voice.digits2string;
import java.util.Iterator;


public interface Corpus {
  public int size();
  public Iterator<String> getIterator();
  public double getPrior(String s);
  public void updatePrior(String s, double prior);
}
