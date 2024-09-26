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

    // background
    g.setColor(Color.DARK_GRAY);
    g.fillRect(0, 0, d.width, d.height);
    // center
    g.setColor(Color.white);
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
      int r = twoDist(mouseLoc, wheelStart);
      Point tl = topLeft(wheelStart, radius);
      g.setColor(Color.yellow);
      g.drawOval(tl.x, tl.y, r, r);
    }
    // drawing wheels

    for (Map.Entry<Point, Integer> entry : wheels.entrySet()) {
      Point center = entry.getKey();
      int rad = entry.getValue();
      g.setColor(Color.orange);
      g.drawOval(center.x - rad, center.y - rad, rad*2, rad*2);
    }

  }

  private int twoDist (Point a, Point b) {
    return (int) Math.hypot(a.x-b.x, a.y-b.y);
  }

  private Point topLeft (Point center, int rad) {
    return new Point(center.x - rad, center.y-rad-30);
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

  private void perFrame() {
    repaint();
    center.setLocation(center.x + vect.x * (1.0 / frameRate), center.y + vect.y * (1.0 / frameRate));
    vect = new Point2D.Double((vect.x * friction), (vect.y * friction));
    // wall collision check
    wallColls(new Point(getSize().width, getSize().height));
  }

  @Override
  public void mouseClicked(MouseEvent e) {

  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (Math.hypot(e.getX() - center.x, e.getY() - center.y - 30) <= radius) {
      draggingVect = true;
      System.out.println("dragging");
    } else {
      wheelStart = new Point(e.getX(), e.getY() - 30);
      draggingWheel = true;
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (draggingVect) {
      draggingVect = false;
      vect.setLocation(2 * (center.x - e.getX()), 2 * (center.y - e.getY()));
    } else if (draggingWheel) {
      draggingWheel = false;
      wheels.put(wheelStart, (int) Math.hypot(e.getX() - wheelStart.x, e.getY() - wheelStart.y));
    }
    System.out.println("released");
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    System.out.println("clicked on");
  }

  @Override
  public void mouseExited(MouseEvent e) {
    System.out.println("clicked off");
  }

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    radius = Math.min((Math.max(5, (int) (radius / (1 + e.getPreciseWheelRotation() / 6)))),
        Math.min(getSize().height / 2, getSize().width / 2));
    repaint();
  }
}
