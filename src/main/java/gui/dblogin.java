/*
 * 
 */
package gui;

import sqlhelper.Queries;
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
import utils.PasswordAuthentication;

/**
 *
 * @author kshan
 */
public class dblogin extends JPanel {

  private static dblogin instance;

  JButton login;
  JTextField userfield, passfield;
  JLabel userlabel, passlabel;
  private JFrame jFrame;

  private dblogin() {
    Border border = BorderFactory.createTitledBorder("Database Login");
    setBorder(border);
    login = new JButton("Login");
    login.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String user = userfield.getText();
        String pass = passfield.getText();
        if (user.isEmpty() || pass.isEmpty()) {
          JOptionPane.showMessageDialog(jFrame, "Username and Password cannot be empty",
                  "No Username/Password", JOptionPane.ERROR_MESSAGE);
        } else if (!PasswordAuthentication.isValid(pass)) {
          JOptionPane.showMessageDialog(jFrame, "Invalid Password, make sure thier are no spaces in your password",
                  "Invalid Password", JOptionPane.ERROR_MESSAGE);
        } else {
          boolean connect = Queries.connect(user, pass);
          if (!connect) {
            JOptionPane.showMessageDialog(jFrame, "Unable to connect to database, please check your username and password",
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
          } else {
            maingui.getInstance().replacePanel(new LoginPage());
          }
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

  public static dblogin getInstance() {
    if (instance == null) {
      instance = new dblogin();
    }
    return instance;
  }
}
