package org.odk.voice.schedule;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

/**
 * An {@link TaskScheduler} simply provides a way to execute tasks outside of 
 * the request handler thread. These tasks are NOT executed sequentially.
 * Set as a ContextListener in web.xml.
 * 
 * @author alerer
 *
 */
public class ThreadScheduler implements ServletContextListener{

  //static LinkedBlockingQueue<Thread> q;
  
  static Timer timer;
  
  /**
   * Runs a thread at some time in the future.
   * @param t The thread.
   * @param msFromNow Time to wait before executing, in ms.
   */
  public static void scheduleThread(Thread t, long msFromNow){
    final Thread tt = t;
    timer.schedule(new TimerTask(){public void run(){tt.start();}}, 
        new Date(new Date().getTime() + msFromNow));
    //log.info("Thread added to the queue. Size: " + q.size());
  }
  
  private static org.apache.log4j.Logger log = Logger
  .getLogger(ThreadScheduler.class);
  
  public ThreadScheduler() {}

  public void contextDestroyed(ServletContextEvent event)
  {

  }
  
  public static final long TIMER_TASK_RATE = 1000;
  
  public void contextInitialized(ServletContextEvent event)
  {
    timer = new Timer("ThreadScheduler", true);
    ThreadScheduler.scheduleThread(new Thread(){public void run(){log.info("Test thread");}},0);
  }
  
}
