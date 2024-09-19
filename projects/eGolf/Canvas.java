import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class Canvas extends JPanel {

  private ArrayList<Shape> activeShapes = new ArrayList<Shape>();

  public Canvas() {
    // 17 milliseconds is 60fps
    Timer timer = new Timer(17, (ActionEvent e) -> { repaint(); });
    timer.start();
  }


  private void dispatchMousePress(MouseEvent evt, Graphics2D g) {
    // Figure out what cell the mouse click occurred
    int mX = evt.getX();
    int mY = evt.getY();
    

    g.fillRect(mX, mY, 400, 400);
  }

  
  @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      var d = getSize();
      g.setColor(Color.GRAY);
      g.fillRect(0, 0, d.width, d.height);
      g.setColor(Color.BLUE);
      var r = (int)(Math.min(d.width, d.height) * 0.125);
      var t = System.nanoTime();
      g.fillOval((d.width - r) / 2, (int)(t / 5e6 % d.height), r, r);
  }
}
