package com.jeremydouglass.recursor;

import processing.core.PApplet;

/**
 * Save frames from a Processing Sketch based on the activity of RecursorQueue.
 * Call the `check()` method to periodically test the queue state against
 * the value stepsPerFrame. If too many steps have elapsed then the grabber
 * calls PApplet.saveFrame() to grab a named, labeled, and numbered a png.
 * 
 *   RecursorGrabber grabber = new RecursorGrabber(this, rq, 128);
 * 
 *   String sketchSlug = "RecursorSquares";
 *   if(shuffle) sketchSlug+="-shuffle";
 *   grabber.check(sketchSlug);
 *
 * @author jeremydouglass
 *
 * @param <E> The element type of the RecursorQueue.
 */
public class RecursorGrabber<E> {
  private PApplet pa;
  public RecursorQueue<E> queue;
  public boolean running = false;
  public boolean stopOnEmpty = true;
  public int lastFrame = 0;
  public int stepsPerFrame = 1;

  /**
   * @param pa             The Processing PApplet. This gives access to saveFrame().
   * @param queue          The queue to monitor for screengrab timing.
   * @param stepsPerFrame  The elapsed pops count that triggers a new screengrab.
   */
  public RecursorGrabber(PApplet pa, RecursorQueue<E> queue, int stepsPerFrame) {
    this.pa = pa;
    this.queue = queue;
    if (stepsPerFrame<1) throw new IllegalArgumentException("bad call.length");
    this.stepsPerFrame = stepsPerFrame;
  }
  
  /**
   * Check if the queue.getPops() and stepsPerFrame are aligned, if too many steps have elapsed,
   * or if it is the final frame and the queue is empty. If so, save a screen-grab.
   * @param sketchSlug  The beginning identifier in filename strings.
   * @return True if a screenshot was taken during the check, false otherwise.
   */
  public boolean check(String sketchSlug) { // "RecursorSquares"
    if (running) {  
      if (queue.isEmpty() && stopOnEmpty) running = false;
      if (queue.getPops()%stepsPerFrame==0            // assumes rq.popMax is divisible by stepsPerFrame
        || queue.getPops()-lastFrame > stepsPerFrame  // or check if too many steps have elapsed
        || running == false                           // always grab final frame once queue is emptied
        ) {
        this.grab(sketchSlug);
        return true;
      }
    }
    return false;
  }
  
  /**
   * Save a screen-grab.
   * @param sketchSlug  The beginning identifier of the filename string for the screenshot.
   */
  public void grab(String sketchSlug) {
    String saveFile=sketchSlug+"-"+queue.popMode+"-"+queue.addMode+"-######.png";
    lastFrame = queue.getPops();
    pa.saveFrame(saveFile);
  }
  
  /**
   * Start the screen grabber, enabling the check() method.
   */
  public void start() {
    running = true;
  }
  
  /**
   * Stop the screen grabber, disabling the check() method.
   */
  public void stop() {
    running = false;
  }
}
