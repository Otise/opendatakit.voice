package org.odk.voice.digits2string;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class CorpusFactory {
  private static Map<String, Corpus> m = new HashMap<String, Corpus>();
  
  static {
    registerCorpus("colors", new FileCorpus(new String[]{"red", "yellow", "blue", "green", "purple", "orange"}));
    try {
      registerCorpus("dict.en", new FileCorpus(new FileInputStream(new File("C:/Users/alerer/workspace/StringPredictorUI/war/corpus/usr-dict-words.en.txt"))));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  public static Corpus registerCorpus(String name, Corpus c){
    return m.put(name, c);
  }
  
  public static Corpus getCorpus(String name){
    return m.get(name);
  }
}
