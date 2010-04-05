package org.odk.voice.digits2string;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;


public class CorpusFactory implements ServletContextListener{

  private static org.apache.log4j.Logger log = Logger
  .getLogger(CorpusFactory.class);
  
  private static CorpusFactory theFactory = null;
  
  private Map<String, Corpus> m = new HashMap<String, Corpus>();
  
  public static CorpusFactory get(){
    return theFactory;
  }
  
  public CorpusFactory(){}
  
  public void contextDestroyed(ServletContextEvent event)
  {
  }

  public void contextInitialized(ServletContextEvent event)
  {
    log.info("Created CorpusFactory");
    String servletPath = event.getServletContext().getRealPath("/");
    registerCorpus("colors", new FileCorpus(new String[]{"red", "yellow", "blue", "green", "purple", "orange"}));
    try {
      registerCorpus("dict.en", new FileCorpus(new FileInputStream(new File(servletPath + "/corpus/usr-dict-words.en.txt"))));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    theFactory = this;
  };
  
  public Corpus registerCorpus(String name, Corpus c){
    return m.put(name, c);
  }
  
  public Corpus getCorpus(String name){
    return m.get(name);
  }
}
