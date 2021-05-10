package gui;

import gui.LoginPage;
import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import sqlhelper.ConnectionLostError;
import sqlhelper.Queries;
import static sqlhelper.Queries.checkUser;
import static utils.IOUtils.getIcon;
import utils.ImageUtil;

public class SignUp extends JPanel {

  private JTextField usernameField;
  private JPasswordField passwordField;
  private JPasswordField repasswordField;
  private JLabel lblPassword;
  private JLabel logoLabel;
  private JLabel lblName;
  private JLabel lblHelloUser;
  private JLabel lblRetypePassword;
  private JButton btnCreate;
  private JButton btnCancel;


  /**
   * Create the frame.
   */
  public SignUp() {
    setBackground(Color.WHITE);
    Border border = BorderFactory.createTitledBorder("Sign Up");
    setBorder(border);
    setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    createLabels();
    createFields();
    createButtons();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    this.add(logoLabel, gbc);
    gbc.gridwidth = 1;

    gbc.gridy++;
    gbc.gridx = 1;
    this.add(lblHelloUser, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    this.add(lblName, gbc);
    gbc.gridx++;
    this.add(usernameField, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    this.add(lblPassword, gbc);
    gbc.gridx++;
    this.add(passwordField, gbc);

    gbc.gridy++;
    gbc.gridx = 0;
    this.add(lblRetypePassword, gbc);
    gbc.gridx++;
    this.add(repasswordField, gbc);

    gbc.gridy++;
    gbc.gridy++;
    gbc.gridx = 0;
    this.add(btnCreate, gbc);
    gbc.gridx++;
    this.add(btnCancel, gbc);
  }

  private void createLabels() {
    lblName = new JLabel("Username");
    lblName.setForeground(new Color(139, 0, 0));
    lblName.setFont(new Font("Agency FB", Font.PLAIN, 22));

    lblPassword = new JLabel("Password");
    lblPassword.setForeground(new Color(139, 0, 0));
    lblPassword.setFont(new Font("Agency FB", Font.PLAIN, 22));

    lblHelloUser = new JLabel("Please Enter your details!");
    lblHelloUser.setForeground(new Color(139, 0, 0));
    lblHelloUser.setFont(new Font("Agency FB", Font.PLAIN, 25));

    lblRetypePassword = new JLabel("Re-Type Password");
    lblRetypePassword.setForeground(new Color(139, 0, 0));
    lblRetypePassword.setFont(new Font("Agency FB", Font.PLAIN, 22));

    logoLabel = new JLabel("");
    logoLabel.setIcon(ImageUtil.scaleImageIcon(getIcon("logo.png"), 150));
  }

  private void createFields() {
    usernameField = new JTextField(10);

    passwordField = new JPasswordField(10);

    repasswordField = new JPasswordField(10);
  }

  private void createButtons() {
    btnCreate = new JButton("Create");
    btnCreate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
          JOptionPane.showMessageDialog(null, "Please enter the required details");
        } else {
          if (!passwordField.getText().equals(repasswordField.getText())) {
            JOptionPane.showMessageDialog(null, "Passwords don't match");
          } else try {
            if (checkUser(username, password) > 0) {
              JOptionPane.showMessageDialog(null, "Username Already registered, try Loggin in or use another username");
            } else {
              boolean success = Queries.createUser(username, password);
              if (success) {
                JOptionPane.showMessageDialog(null, "User Account Created");
                maingui.getInstance().replacePanel(UserFront.getInstance());
              }
            }
          } catch (ConnectionLostError ex) {
            maingui.getInstance().ConnectionLost();
          }
        }
      }
    });

    btnCancel = new JButton("Cancel");
    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        maingui.getInstance().replacePanel(new LoginPage());
      }
    });
  }
}
