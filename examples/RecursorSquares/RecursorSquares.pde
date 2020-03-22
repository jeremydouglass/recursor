import com.jeremydouglass.recursor.*;
/**
 * RecursorSquares
 * 2020-03-19 Jeremy Douglass - Processing 3.4
 * Progressively animate recursion without threads.
 * Squares example.
 * Press any key to randomize recurse mode and restart.
 */

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
  rq.stepUntil(128, 0);
  if(!rq.isEmpty()) println(rq.status());
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

void keyReleased() {
  // randomize settings
  rq.popMode = Mode.random();
  rq.addMode = Mode.random();
  // reset
  rq.resetCounts();
  rq.clear();
  frameCount = -1; // restart: rerun setup() after this frame
}
