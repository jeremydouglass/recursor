package com.jeremydouglass.recursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * A double-ended queue (deque) for recursive operations.
 * Its purpose is to model recursive processes such that they can be
 * stepped through for progressive visualization or other output.
 * This is particularly useful to animate recursive operations
 * within the Processing draw() loop.
 * 
 * The queue holds call elements as arguments to a recurse method.
 * 
 * Override `recurse()` with a method that takes a call, performs an operation,
 * (for example, drawing a square), then returns zero or more calls (more squares).
 * This method is not itself recursive and does not operate on the queue directly.
 * Instead, once defined it is called step functions.
 * 
 * The step functions step() / stepAll() / stepUntil() all operate on the queue
 * using recurse(). They can be configured with popMode and addMode.
 * -  Mode.FIRST pops or adds to the front of the queue,
 * -  Mode.LAST to the end,
 * -  Mode.EITHER is randomly resolved on each operation to either FIRST or LAST.
 * 
 * step() returns a call, which can be useful for further processing outside
 * recurse().
 * 
 * stepAll() runs the queue until empty. It is important that recurse() have
 * a terminating case, or this will run forever!
 * 
 * stepUntil() sets limits on the maximum pops or adds may be performed before it
 * returns. This is useful for progressive visualization.
 * 
 * shuffle() shuffles the queue. Performing this periodically -- e.g. once per stepUntil()
 * can help produce random growth over a large recursive call tree.
 * 
 * @author Jeremy Douglass
 * @param <E>  The type of call elements
 *  
 */
public class RecursorQueue<E> extends LinkedList<E> {
  private static final long serialVersionUID = -3584631587915680024L;
  /** Running count of calls added to the queue since initialize or reset(). */
  private int adds;
  /** Running count of calls popped off the queue since initialize or reset(). */
  private int pops;
  private int addsLast;
  private int popsLast;
  /** Mode for add operations: FIRST, LAST, EITHER. */
  public Mode addMode;
  /** Mode for pop operations: FIRST, LAST, EITHER. */
  public Mode popMode;
  
  /*
   * How can we progressively animate a recursive process in Processing
   * without using threads?
   *
   * One way is to define our recursion as a function that takes a call
   * and returns a list of calls. For example, recursiveSqure() takes a
   * call (float[]), draws a square, then decides whether to draw 0 or 4
   * more squares -- and instead of calling itself directly, it returns
   * a list of 0 or 4 calls (float[]) to be acted on later.

   * Now we can create a queue of calls with a recurse() method that calls
   * our recursion step function. We can then step() through our calls --
   * popping them off the queue, expanding them recurse, and pushing
   * the new calls back onto the queue.
   * 
   * RecurseQueue is such a wrapper for a recursive step function.
   */
  
  /**
   * Constructor defaulting to pop first, add last.
   */
  public RecursorQueue() {
    super();
    this.popMode = Mode.FIRST;
    this.addMode = Mode.LAST;
  }
  
  /**
   * Constructor requiring configuration of how steps pop and add.
   * @param addMode  Mode for add operations: FIRST, LAST, EITHER.
   * @param popMode  Mode for pop operations: FIRST, LAST, EITHER.
   */
  public RecursorQueue( Mode addMode, Mode popMode) {
    super();
    this.addMode = addMode;
    this.popMode = popMode;
  }
  
  /**
   * REQUIRED OVERRIIDE
   * It is required to override this method with a recursive step function.
   * The function accepts a call and returns a list of calls. 
   * `recurse()` is not recursive in itself -- it performs recursion
   * when it is called by step() / stepUntil().
   * 
   * The default implementation generates no new calls, making step
   * methods consumptive-only and non-recursive.
   * 
   * @param call  A call -- arguments in a primitive array or object.
   * @return      An ArrayList of zero or more calls (empty, but not null).
   */
  public ArrayList<E> recurse(E call) {
    ArrayList<E> result = new ArrayList<E>();
    // result.add(call);
    return result;
  }

  public int getAdds() {
    return adds;
  }
  /** 
   * Returns the number of pops since initialization or last reset().
   * @return The number of pops since initialization or last reset().
   */
  public int getPops() {
    return pops;
  }
  
  /**
   * Resets counts for adds and pops to 0.
   * To clear the queue, use clear().
   */
  public void resetCounts() {
    pops = 0;
    adds = 0;
    popsLast = 0;
    addsLast = 0;
  }
  
  /** Shuffle the call queue. */
  public void shuffle() {
    Collections.shuffle(this);
  }

  /**
   * Pop one call, recurse, and add any new calls.
   * @return The popped call element.
   */
  public E step() {
    return step(this.popMode, this.addMode);
  }
  
  /**
   * Pop one call, recurse, and add any new calls.
   * @param popMode  Mode for pop operations: FIRST, LAST, EITHER.
   * @param addMode  Mode for add operations: FIRST, LAST, EITHER.
   * @return The popped call element.
   */
  public E step(Mode popMode, Mode addMode) {
    // pop from first or last end of deque
    if(popMode==Mode.EITHER) popMode = Mode.either();
    E call = (popMode==Mode.FIRST) ? this.removeFirst() : this.removeLast();
    pops++;
    ArrayList<E> calls = recurse(call);  
    
    // add to first or last end of deque
    if(addMode==Mode.EITHER) addMode = Mode.either();
    if(addMode==Mode.FIRST) {
      for (int i=0; i<calls.size(); i++) {
        this.addFirst(calls.get(i));
      }
    } else {
      for (int i=0; i<calls.size(); i++) {
        this.addLast(calls.get(i));
      }      
    }
    adds += calls.size();
    return call;
  }
  
  /**
   * Step through the call queue until empty.
   * @return An int[]{pops, adds} containing the counts for this operation.
   */
  public int[] stepAll() {
    return stepUntil(0, 0, popMode, addMode);
  }
  /**
   * 
   * @param   popMax  Return if pops exceed this amount. 0 = unlimited.
   * @param addCheck  Return if adds exceed this amount. 0 = unlimited. Not a hard limit.
   * @return An int[]{pops, adds} containing the counts for this operation.
   */
  public int[] stepUntil(int popMax, int addCheck) {
    return stepUntil(popMax, addCheck, popMode, addMode);
  }
  
  /**
   * Step through the call queue while the number of pops does
   * not exceed popMax, or until the number of adds exceeds addCheck,
   * or until call queue is empty. Arguments of 0 = unlimited.
   * 
   * NOTE: addCheck is a check, not a hard limit.
   * The value of adds on return may be higher than addCheck.
   * For example, a queue at addCheck-1 may in a single step add +10,
   * returning at size addCheck+9.
   * 
   * @param   popMax  Return if pops exceed this amount. 0 = unlimited.
   * @param addCheck  Return if adds exceed this amount. 0 = unlimited. Not a hard limit.
   * @param  popMode  Mode for pop operations: FIRST, LAST, EITHER.
   * @param  addMode  Mode for add operations: FIRST, LAST, EITHER.
   * @return An int[]{pops, adds} containing the counts for this operation.
   */
  public int[] stepUntil(int popMax, int addCheck, Mode popMode, Mode addMode) {
    // offset current counts
    this.popsLast = this.pops;
    this.addsLast = this.adds;
    int popsStop = popMax + popsLast;
    int addsStop = addCheck + addsLast;
    //System.out.println(popMax +" "+ pops +" "+ addCheck +" "+ adds +" "+ this.isEmpty());
    while ((popMax==0 || pops<popsStop) && (addCheck==0 || adds<addsStop) && !this.isEmpty()) {
      step(popMode, addMode);
    }
    return new int[] {getPopsLast(), getAddsLast()};
  }
  
  /**
   * Return the change in adds since the last stepAll or stepUtil.
   * @return The change in adds since the last stepAll or stepUtil.
   */
  public int getAddsLast() {
    return adds-addsLast;
  }
  
  /**
   * Return the change in pops since the last stepAll or stepUtil.
   * @return The change in pops since the last stepAll or stepUtil.
   */
  public int getPopsLast() {
    return pops-popsLast;
  }
  
  /**
   * Describe the status: modes and counts.
   * @return String describing the status: modes and counts.
   */
  public String status() {
    return "pop,add[ " + popMode + "," + addMode + " ]  cnt[ " + pops + "," + adds + " ]  chg[ " + getPopsLast() + "," + getAddsLast() + " ]  size[" + size() + "]";
  }

  public String toString() {
    return getClass().getName() + "" + status();
  }
}
