/*
 * 
 */
package gui;

import java.awt.BorderLayout;
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

  private maingui() {
    setTitle("Website Ranking System");
    pack();
    setVisible(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
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
//    double scaling = getScaling();
//    Dimension size = MathUtils.scaleDimension(getInstance().getSize(), scaling);
    getInstance().setMinimumSize(getInstance().getSize());
    setLocationRelativeTo(null);
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
