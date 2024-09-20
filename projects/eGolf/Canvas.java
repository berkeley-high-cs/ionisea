import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Canvas extends JPanel implements MouseListener, MouseWheelListener {

  //private ArrayList<Shape> activeShapes = new ArrayList<>();
  private int radius = 5;
  private Point center = new Point(getSize().width/2, getSize().height/2);
  public Canvas() {
    // 17 milliseconds is roughly 60fps
    //Timer timer = new Timer(17, (ActionEvent e) -> { repaint(); });
    //timer.start();
  }

  @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      var d = getSize();
      
      g.setColor(Color.DARK_GRAY);
      g.fillRect(0, 0, d.width, d.height);
      g.setColor(Color.RED);
      g.drawOval(center.x-radius/2, center.y-radius/2, radius, radius);
  }

    @Override
    public void mouseClicked(MouseEvent e) {
      center.x = e.getX();
      center.y = e.getY();
      repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
      System.out.println("pressed");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      System.out.println("releaased");
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
       radius = Math.max(5,((int)(10 * e.getPreciseWheelRotation())) + radius);
       repaint();
    }
}
