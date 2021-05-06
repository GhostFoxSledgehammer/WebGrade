/*
 * 
 */
package simpsanghatan.dbmsproject;

import java.awt.BorderLayout;
import javax.swing.JFrame;
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
    setSize(1000, 1000);
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
    }
    getInstance().getContentPane().removeAll();
    getInstance().getContentPane().add(newpanel);
    getInstance().setSize(1500, 1500);
    getInstance().pack();
  }

  public static void main(String[] args) {
    getInstance().replacePanel(new dblogin());
  }
}
