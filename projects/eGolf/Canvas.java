import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class Canvas extends JPanel implements MouseListener, MouseWheelListener {

  private HashMap<Point, Integer> wheels = new HashMap<>();
  private int radius = 15;
  private Point2D.Double center;
  private Point2D.Double vect;
  private boolean draggingVect = false;
  private boolean draggingWheel = false;
  private Point wheelStart;
  private int frameRate = 60;
  private double friction = 0.99; // 1 = no loss, 0 = loses all in one frame

  public Canvas() {
    center = new Point2D.Double(getSize().width / 2, getSize().height / 2); // this is broken
    vect = new Point2D.Double(Math.random() * 2000, Math.random() * 2000);
    Timer timer = new Timer(1000 / frameRate, (ActionEvent e) -> {
      perFrame();
    });
    timer.start();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    var d = getSize();

    Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
    // BROKEN -- REQUIRES PANEL TO BE AT TOP LEFT OF SCREEN! looking for fix

    // background
    g.setColor(Color.DARK_GRAY);
    g.fillRect(0, 0, d.width, d.height);
    // center
    g.setColor(Color.gray);
    g.drawOval((int) center.x - 3, (int) center.y - 3, 6, 6);
    // outer edge
    g.setColor(Color.RED);
    g.drawOval((int) center.x - radius, (int) center.y - radius, radius * 2, radius * 2);
    // drag vect
    g.setColor(Color.green);
    if (draggingVect) {
      g.drawLine((int) center.x, (int) center.y,
          (int) (center.x + 2 * (center.x - mouseLoc.x)), // stole from stack overflow
          (int) (center.y + 2 * (center.y - mouseLoc.y)) - 30);
    }

    // motion in 1s
    g.setColor(Color.blue);
    g.drawLine((int) center.x, (int) center.y, (int) (center.x + vect.x), (int) (center.y + vect.y));

    // new wheel
    if (draggingWheel) {
      int r = distToMouse(mouseLoc, wheelStart);
      // Point tl = topLeft(wheelStart, radius);
      g.setColor(Color.yellow);
      g.drawOval(wheelStart.x - r, wheelStart.y - r, r * 2, r * 2);
      g.setColor(Color.gray);
      g.drawOval(wheelStart.x - 3, wheelStart.y - 3, 6, 6);
    }

    // drawing wheels
    for (Map.Entry<Point, Integer> entry : wheels.entrySet()) { // found entry set on java docs
      Point center = entry.getKey();
      int rad = entry.getValue();
      g.setColor(Color.orange);
      g.drawOval(center.x - rad, center.y - rad, rad * 2, rad * 2);
      g.setColor(Color.gray);
      g.drawOval(center.x - 3, center.y - 3, 6, 6);
    }

  }

  private int distToMouse(Point p, Point mouse) {
    return (int) Math.hypot(p.x - mouse.x, p.y - mouse.y - 30);
  }

  private void wallColls(Point dim) {
    if (center.x + radius > dim.x) {
      center.x = dim.x - radius;
      vect.x *= -1;
    }
    if (center.x - radius < 0) {
      center.x = radius;
      vect.x *= -1;
    }
    if (center.y + radius > dim.y) {
      center.y = dim.y - radius;
      vect.y *= -1;
    }
    if (center.y - radius < 0) {
      center.y = radius;
      vect.y *= -1;
    }
  }

  private double distToCenter(Point a, Point2D.Double b) {
    return Math.hypot(a.x - b.x, a.y - b.y);
  }

  private Point2D.Double leaveWheel(int wheelRad, Point wheelCenter) {// shift the circle outside of the wheel
    // distance on each axis to the center
    Point2D.Double wheelDeltas = new Point2D.Double(center.x - wheelCenter.x, center.y - wheelCenter.y);

    // (distance to move the circle outside of the wheel) / distance between the
    // circles' centers = triangle ratio
    double toShiftRatio = (radius + wheelRad - distToCenter(wheelCenter, center) + 1)
        / distToCenter(wheelCenter, wheelDeltas);

    // use triangle delta on proportional sides
    Point2D.Double toShiftDeltas = new Point2D.Double(wheelDeltas.x * toShiftRatio, wheelDeltas.y * toShiftRatio);
    center.x += toShiftDeltas.x;
    center.y += toShiftDeltas.y;

    return wheelDeltas;
  }

  private void wheelColls() {
    for (Map.Entry<Point, Integer> entry : wheels.entrySet()) {
      int wheelRad = entry.getValue();
      Point wheelCenter = entry.getKey();
      if (radius + wheelRad >= distToCenter(wheelCenter, center)) {

        Point2D.Double wheelDeltas = new Point2D.Double(center.x - wheelCenter.x, center.y - wheelCenter.y);
        
        // ratio of distance from center to collision point to distance between both centers
        //double radRat = 1.0*radius/ (radius + wheelRad);
        // distances to collision point
        //wheelDeltas.x *= radRat;
        //wheelDeltas.y *= radRat;
        
        // slope ratios
        double slopeWheel = wheelDeltas.y/wheelDeltas.x;
        double slopeVect = vect.y/vect.x;
        // new vector slope (i did a lot of math on paper for this it could still be wrong though)
        // where m is wheel slope, and n is vect slope: new slope = 
        // (2n(m^2) + 2m - n) / (2mn - m^2 + 3)
        double bumpedSlope = ((2*(slopeWheel*slopeWheel) * slopeVect )+ (2* slopeWheel) - (slopeVect))/
        (2* slopeWheel * (slopeVect) - (slopeWheel* slopeWheel) + 3);
        System.out.println(slopeVect + " " + bumpedSlope);
        double vectAmp = Math.hypot(vect.x, vect.y);
        vect = new Point2D.Double (vect.x*slopeVect/bumpedSlope, vect.y/slopeVect*bumpedSlope);
        //vect.x *= bumpedSlope/slopeVect;
        //vect.y *= bumpedSlope/slopeVect;
        leaveWheel(wheelRad, wheelCenter);
      }
    }
  }

  private void perFrame() {
    center.setLocation(center.x + vect.x * (1.0 / frameRate), center.y + vect.y * (1.0 / frameRate));
    vect = new Point2D.Double((vect.x * friction), (vect.y * friction));

    // wheel collision check
    wheelColls();
    // wall collision check
    wallColls(new Point(getSize().width, getSize().height));
    repaint();
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (e.getButton() == 1) {
      if (Math.hypot(e.getX() - center.x, e.getY() - center.y - 30) <= radius) {
        draggingVect = true;
        System.out.println("dragging");
      } else {
        wheelStart = new Point(e.getX(), e.getY() - 30);
        draggingWheel = true;
      }
    } else if (e.getButton() == 3) {
      System.out.println("right click");
      Point clicked = new Point(e.getX(), e.getY());
      Point toRemove = new Point(-1, -1); // never defined in map
      // System.out.println(clicked.toString());
      for (Map.Entry<Point, Integer> entry : wheels.entrySet()) {
        if (distToMouse(clicked, entry.getKey()) <= entry.getValue()) {
          toRemove = entry.getKey(); // the order of the map is random!!! wtfrick!!
        }
      }
      wheels.remove(toRemove);
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (e.getButton() == 1) {
      if (draggingVect) {
        draggingVect = false;
        vect.setLocation(2 * (center.x - e.getX()), 2 * (center.y - e.getY()));
      } else if (draggingWheel) {
        draggingWheel = false;
        int rad = (int) Math.hypot(e.getX() - wheelStart.x, e.getY() - 30 - wheelStart.y);
        if (rad > 5) {
          wheels.put(wheelStart, rad);
        }
      }
    }
    System.out.println("released");
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    radius = Math.min((Math.max(5, (int) (radius / (1 + e.getPreciseWheelRotation() / 6)))),
        Math.min(getSize().height / 2, getSize().width / 2));
    repaint();
  }
}
