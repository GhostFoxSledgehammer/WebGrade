 /*
 * 
 */
package simpsanghatan.dbmsproject;

import sqlhelper.Queries;
import sqlhelper.settings;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 *
 * @author kshan
 */
public class dblogin extends JPanel {

  JButton login;
  JTextField userfield, passfield;
  JLabel userlabel, passlabel;
  private JFrame jFrame;

  public dblogin() {
    Border border = BorderFactory.createTitledBorder("Database Login");
    setBorder(border);
    login = new JButton("Login");
    login.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        settings.user = userfield.getText();
        settings.pass = passfield.getText();
        boolean connect = Queries.connect();
        if (!connect) {
          JOptionPane.showMessageDialog(jFrame, "Unable to connect to database, please check your username and password",
                  "Connection Error", JOptionPane.ERROR_MESSAGE);
        } else {
          maingui.getInstance().replacePanel(new LoginPage());
        }
      }
    });
    userfield = new JTextField(10);
    userfield.setToolTipText("Username");
    passfield = new JPasswordField(10);
    passfield.setToolTipText("Password");
    userlabel = new JLabel("Username");
    passlabel = new JLabel("Password");
    GridBagLayout gbl = new GridBagLayout();
    this.setLayout(gbl);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;
    this.add(userlabel, gbc);
    gbc.gridx++;
    this.add(userfield, gbc);
    gbc.gridx++;
    this.add(passlabel, gbc);
    gbc.gridx++;
    this.add(passfield, gbc);
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    this.add(login, gbc);
  }

  public static void main(String[] args) {
    maingui.getInstance().replacePanel(new dblogin());
  }
}
