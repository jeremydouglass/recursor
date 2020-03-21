import com.jeremydouglass.recursor.RecursorQueue;
/**
 * RecursorTriangle
 * 2020-03-20 Jeremy Douglass - Processing 3.4
 * Progressively animate a recursive Sierpinski Triangle.
 * Press space to restart.
 */
RecursorQueue rq = new RecursorQueue<CallST>() {
  public ArrayList<CallST> recurse(CallST call) {
    return call.recurse();
  }
};

void setup() {
  size(600, 550);
  smooth();
  noStroke();
  colorMode(HSB, 360, 100, 100);
  // seed
  rq.add(new CallST(0, 450, 400, 0, 500, 550, 8));
  background(0, 0, 40);
}

void draw() {
  rq.stepUntil(3, 0);
}

void keyReleased() {  
  if (key==' ') {  // reset
    rq.clear();
    frameCount=-1;
  }
}

/**
 * Class representing a single Sierpinski Triangle unit
 * with a single method recurse() representing its
 * expansion rule.
 */
class CallST {
  // Adapted from Triangle of Sierpinski by Maeln
  // https://www.openprocessing.org/sketch/17026/
  // CC0 1.0 Universal
  // http://creativecommons.org/publicdomain/zero/1.0/
  float x1, y1, x2, y2, x3, y3;
  int n;
  
  CallST(float x1, float y1, float x2, 
    float y2, float x3, float y3, int n) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.x3 = x3;
    this.y3 = y3;
    this.n = n;
  }
  
  ArrayList<CallST> recurse() {
    ArrayList<CallST> calls = new ArrayList<CallST>();
    if ( n > 0 ) {
      int h = (110+n*(360/8))%360;
      fill(h, 100, 100);
      triangle(x1, y1, x2, y2, x3, y3);
      // calculate segment midpoints
      float h1 = (x1+x2)/2.0;
      float w1 = (y1+y2)/2.0;
      float h2 = (x2+x3)/2.0;
      float w2 = (y2+y3)/2.0;
      float h3 = (x3+x1)/2.0;
      float w3 = (y3+y1)/2.0;
      // call three subtriangles at corners
      calls.add(new CallST(x1, y1, h1, w1, h3, w3, n-1));
      calls.add(new CallST(h1, w1, x2, y2, h2, w2, n-1));
      calls.add(new CallST(h3, w3, h2, w2, x3, y3, n-1));
    }
    return calls;
  }
}
