/*
 * 
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author kshan
 */
public class maingui extends JFrame {

  private static maingui instance;
  private boolean workaround;

  private maingui() {
    setTitle("Website Ranking System");
    pack();
    setVisible(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
  }

  @Override
  public void setSize(int width, int height) {
    if (workaround) {
      return;
    }
    super.setSize(width, height);
  }

  public static maingui getInstance() {
    if (instance == null) {
      instance = new maingui();
    }
    return instance;
  }

  public void replacePanel(JPanel newpanel) {
    if (!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(() -> replacePanel(newpanel));
      return;
    }
    getContentPane().removeAll();
    getContentPane().add(newpanel);
    setMinimumSize(null);
    pack();
    Dimension dorig=getSize();
    AffineTransform at = getGraphicsConfiguration().getDefaultTransform();
    Dimension dmin = new Dimension((int) (dorig.width * at.getScaleX()) +1, (int) (dorig.height * at.getScaleY()) +1);
//    double scaling = getScaling();
//    Dimension size = MathUtils.scaleDimension(getInstance().getSize(), scaling);
    setLocationRelativeTo(null);
    workaround = true;
    setMinimumSize(dmin);
    workaround = false;
  }

  public static void main(String[] args) {
    getInstance().replacePanel(dblogin.getInstance());
  }

  public void ConnectionLost() {
    JOptionPane.showMessageDialog(this, "Connection lost, Please Login again!");
    maingui.getInstance().replacePanel(dblogin.getInstance());
  }

  private double getScaling() {
    int sr = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
    double x = (double) sr / 96;
    return x;
  }
}
