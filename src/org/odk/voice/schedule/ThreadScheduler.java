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
 * 
 * @author alerer
 *
 */
public class ThreadScheduler implements ServletContextListener{

  static LinkedBlockingQueue<Thread> q;
  
  static Timer timer;
  
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
    //q = new LinkedBlockingQueue<Thread>();
//    Thread schedThread = new Thread(){
//      public void run(){
//        log.info("Entering queue-polling loop");
//        while (true) {
//          try{q.take().start();} catch (InterruptedException e){log.error(e);}
//          //try{sleep(1000);}catch(InterruptedException e) {log.warn("Interrupted");}
//          log.info("Started a thread.");
//        }
//      }
//    };
//    schedThread.setDaemon(true);
//    schedThread.start();
    
    timer = new Timer("ThreadScheduler", true);
//    
//    TimerTask task = new TimerTask(){
//      @Override
//      public void run() {
//        log.info("Begin threadtask");
//        Thread t = q.poll();
//        if (t!= null) {
//          t.start();
//          log.info("Ran a thread");
//        }
//        log.info("End threadtask");
//      }
//    };
    
    //timer.scheduleAtFixedRate(task, new Date(), TIMER_TASK_RATE);
    
    ThreadScheduler.scheduleThread(new Thread(){public void run(){log.info("Test thread");}},0);
  }
  
}
