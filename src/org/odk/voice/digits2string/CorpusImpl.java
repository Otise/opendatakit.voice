package org.odk.voice.digits2string;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.omg.CORBA_2_3.portable.OutputStream;

/**
 * Corpus implementation.
 * @author alerer
 *
 */
public class CorpusImpl implements Corpus {
  String[] words;
  Map<String, Double> priors;
  
  /**
   * Creates a corpus from an array of word strings.
   * @param words
   */
  public CorpusImpl(String[] words){
    this(words, null);
  }
  
  /**
   * Creates a corpus from an array of word strings and prior weights.
   * @param words
   * @param priors
   */
  public CorpusImpl(String[] words, double[] priors){
    if (priors != null && priors.length != words.length) {
      throw new IllegalArgumentException("Words and priors must have the same length");
    }
    this.words = words;
    this.priors = new HashMap<String, Double>();
    if (priors != null) {
      for (int i = 0; i < priors.length; i++){
        if (priors[i] >= 0)
          this.priors.put(words[i], priors[i]);
      }
    }
  }
  
  /**
   * Create a corpus from an input stream containing either a list of newline-separated words or 
   * newline-separated pairs of word/prior pairs. 
   * @param is
   */
  public CorpusImpl(InputStream is) {
    List<String> words = new ArrayList<String>();
    priors = new HashMap<String, Double>();
    Reader r = null;
    try {
      r = new InputStreamReader(is);
      StreamTokenizer st = new StreamTokenizer(r);
      st.wordChars(21, 255);
      st.whitespaceChars(0, 20);
      while(st.nextToken() != StreamTokenizer.TT_EOF) {
        //System.out.println(st.nval + "," + st.sval);
        String[] entry = st.sval.split(" ");
        words.add(entry[0]);
        if (entry.length > 1) { 
          try {
            double p = Double.parseDouble(entry[1]);
            priors.put(entry[0], p);
          } catch (NumberFormatException e) {}
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.words = words.toArray(new String[]{});
  }

  @Override
  public Iterator<String> getIterator() {
    return new Iterator<String>(){
      int index = 0;
      @Override
      public boolean hasNext() {
        return (index < words.length);
      }

      @Override
      public String next() {
        if (index >= words.length) {
          throw new NoSuchElementException();
        }
        return words[index++];
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public double getPrior(String s) {
    Double res = priors.get(s);
    if (res == null) {
      return 1.0;
    } else {
      return res;
    }
  }

  @Override
  public int size() {
    return words.length;
  }
  
  /**
   * Export the corpus to an OutputStream in the same form accepted by the InputStream constructor.
   * @param os
   */
  public void export(OutputStream os){
    try {
      Writer w = new OutputStreamWriter(os);
      for (String word : words) {
        Double prior = priors.get(word);
        if (prior == null)
          w.write(word + "\n");
        else
          w.write(word + " " + prior + "\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void updatePrior(String s, double prior) {
    priors.put(s, prior);
  }
  
  
}
