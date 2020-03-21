import com.jeremydouglass.recursor.*;
/**
 * RecursorConfig
 * 2020-03-19 Jeremy Douglass - Processing 3.4
 * Progressively animate recursion without threads.
 * Press a key to test different queue configurations:
 *    1 2 3 4 5 (modes)
 *    q w e r t (modes + shuffle)
 */
boolean shuffle = false;

RecursorQueue rq = new RecursorQueue<float[]>() {
  public ArrayList<float[]> recurse(float[] call) {
    return recursiveSquare(call);
  }
};

void setup() {
  size(640, 640);
  frameRate(32);
  noStroke();
  rectMode(RADIUS);
  // add seed
  rq.add(new float[]{width/2, height/2, width/2.1, 0.98, 3});  
  background(255);
}

void draw() {
  if (shuffle) rq.shuffle();
  rq.stepUntil(128, 0);
  statusBox(12, 3, rq);
}

/**
 * Renders a call, then returns zero or more new calls. Does not call itself.
 * @param x       x location of this square
 * @param y       y location of this square
 * @param width   width of this square
 * @param scale   relative size <1 of the next recursive squares (i.e. 0.01 - 0.99)
 * @param min     when a next square will be below this width, it will not be called
 * @return ArrayList of zero or more new calls.
 */
ArrayList<float[]> recursiveSquare(float[] call) {
  // validate
  if (call.length != 5) throw new IllegalArgumentException("bad call.length");
  if (call[3] >= 1) throw new IllegalArgumentException("bad scale, not <1");
  // unpack
  float x = call[0];
  float y = call[1];
  float w = call[2];
  float scale = call[3];
  float min = call[4];
  // render
  fill(random(128, 255), random(128, 255), random(128, 255), 64);
  rect(x, y, w, w);
  // decide if recursing
  ArrayList<float[]> calls = new ArrayList<float[]>();
  // calculate
  float nextw = w/2.0 * scale;
  if (nextw > min) {
    // expand calls
    calls.add(new float[]{x-w/2.0, y-w/2.0, nextw, scale, min}); 
    calls.add(new float[]{x+w/2.0, y-w/2.0, nextw, scale, min}); 
    calls.add(new float[]{x-w/2.0, y+w/2.0, nextw, scale, min}); 
    calls.add(new float[]{x+w/2.0, y+w/2.0, nextw, scale, min});
  }
  return calls;
}

/**
 * Prints an infobox on the top of the screen with the current
 * frame, mode, and the number of calls waiting in the CallQueue.
 */
void statusBox(int isize, int margin, RecursorQueue rq) {
  String info = rq.status();
  if (shuffle) info += "  [ SHUFFLE ]";
  pushStyle();
  fill(0);
  rect(0, 0, width, isize+(2*margin));
  fill(255);
  textSize(isize);
  text(info, margin, isize+margin);
  text("  fr:" + str(frameCount), width-isize*8, isize+margin);
  popStyle();
  if (!rq.isEmpty()) println(info);
}

void keyReleased() {
  switch(key) {
  case '1': // BROAD
    rq.popMode = Mode.FIRST;
    rq.addMode = Mode.LAST;
    shuffle = false;
    break;
  case '2': 
    rq.popMode = Mode.FIRST;
    rq.addMode = Mode.FIRST;
    shuffle = false;
    break;
  case '3': 
    rq.popMode = Mode.FIRST;
    rq.addMode = Mode.EITHER;
    shuffle = false;
    break;
  case '4': 
    rq.popMode = Mode.EITHER;
    rq.addMode = Mode.FIRST;
    shuffle = false;
    break;
  case '5': 
    rq.popMode = Mode.EITHER;
    rq.addMode = Mode.EITHER;
    shuffle = false;
    break;
    // shuffled versions
  case 'q': // BROAD
    rq.popMode = Mode.FIRST;
    rq.addMode = Mode.LAST;
    shuffle = true;
    break;
  case 'w': 
    rq.popMode = Mode.FIRST;
    rq.addMode = Mode.FIRST;
    shuffle = true;
    break;
  case 'e': 
    rq.popMode = Mode.FIRST;
    rq.addMode = Mode.EITHER;
    shuffle = true;
    break;
  case 'r': 
    rq.popMode = Mode.EITHER;
    rq.addMode = Mode.FIRST;
    shuffle = true;
    break;
  case 't': 
    rq.popMode = Mode.EITHER;
    rq.addMode = Mode.EITHER;
    shuffle = true;
    break;
  }
  // reset on any key
  rq.resetCounts();
  rq.clear();
  frameCount = -1; // restart: rerun setup() after this frame
}
