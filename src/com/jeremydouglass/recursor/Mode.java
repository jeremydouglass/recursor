package com.jeremydouglass.recursor;

import java.util.Random;

/**
 * Enumerate strategies for popping or adding to the ends of a deque (double-ended queue).
 * 
 * FIRST corresponds to pop, push, remove, addFirst, getFirst, peakFirst, pollFirst, removeFirst etc.
 * 
 * LAST corresponds to add, addLast, getLast, peakLast, pollLast, removeLast etc.
 * 
 * EITHER may be resolved to FIRST/LAST using the built-in `either()`:
 * 
 *     if(myMode==Mode.EITHER) myMode = Mode.either();
 * 
 * @author jeremydouglass
 *
 */
public enum Mode {
  FIRST, LAST, EITHER;
  
  private static final Mode[] VALUES = values();
  private static final Random RANDOM = new Random();
  /**
   * @return Returns either FIRST or LAST, randomly.
   */
  public static Mode either() {
    return VALUES[RANDOM.nextInt(2)];
  }
  /**
   * @return Returns any one of FIRST, LAST, EITHER.
   */
  public static Mode random() {
    return VALUES[RANDOM.nextInt(VALUES.length)];
  }
}
