package gui;

import java.awt.EventQueue;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import sqlhelper.ConnectionLostError;
import sqlhelper.Queries;
import static utils.IOUtils.getIcon;
import utils.ImageUtil;
import utils.TextLimit;

public class Feedback extends JPanel {

  private static Feedback instance;
  private JLabel lblanonymous;
  private JLabel lblHelpful;
  private JLabel logoLabel;
  private JTextArea textArea;
  private JButton btnSubmit;
  private JButton btnCancel;

  /**
   * Launch the application.
//   */
//  public static void main(String[] args) {
//    EventQueue.invokeLater(new Runnable() {
//      public void run() {
//        try {
//          maingui.getInstance().replacePanel(new Feedback());
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//      }
//    });
//  }

  /**
   * Create the frame.
   */
  private Feedback() {
    Border border = BorderFactory.createTitledBorder("Give Feedback");
    setBorder(border);
    setBackground(new Color(255, 255, 255));
    setLayout(new GridBagLayout());

    createLabels();
    createFields();
    createButtons();
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(5, 5, 5, 5);

    add(logoLabel, gbc);

    gbc.gridy++;
    add(lblHelpful, gbc);

    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    add(textArea, gbc);
    gbc.fill = GridBagConstraints.NONE;

    gbc.gridy++;
    add(lblanonymous, gbc);

    gbc.gridy++;
    gbc.gridx = 0;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.CENTER;
    add(btnSubmit, gbc);
    gbc.gridx++;
    add(btnCancel, gbc);
  }

  public static Feedback getInstance() {
    if (instance == null) {
      instance = new Feedback();
    }
    return instance;
  }

  private void createLabels() {
    lblanonymous = new JLabel("Your feedback will remain anonymous ");
    lblanonymous.setForeground(new Color(139, 0, 0));
    lblanonymous.setFont(new Font("Agency FB", Font.PLAIN, 25));

    lblHelpful = new JLabel("Your Feedback is very helpful for us!");
    lblHelpful.setFont(new Font("Agency FB", Font.PLAIN, 25));
    lblHelpful.setForeground(new Color(139, 0, 0));

    logoLabel = new JLabel("");
    logoLabel.setIcon(ImageUtil.scaleImageIcon(getIcon("logo.png"), 200));
    logoLabel.setBounds(143, 11, 147, 129);
  }

  private void createFields() {
    textArea = new JTextArea(10, 70);
    textArea.setBackground(Color.LIGHT_GRAY);
    textArea.setDocument(new TextLimit(500));
  }

  private void createButtons() {
    btnSubmit = new JButton("Submit");
    btnSubmit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String feedback = textArea.getText();
        if (feedback.isEmpty()) {
          JOptionPane.showMessageDialog(null, "Please write something to submit :)");
        } else {
          boolean success;
          try {
            success = Queries.submitFeedback(feedback);
            if (success) {
              JOptionPane.showMessageDialog(null, "We received your feedback. \n Thank you");
              maingui.getInstance().replacePanel(UserFront.getInstance());
            } else {
              JOptionPane.showMessageDialog(null, "There was a problem submiting the feedback.");
            }
          } catch (ConnectionLostError ex) {
            maingui.getInstance().ConnectionLost();
          }
        }
      }
    });

    btnCancel = new JButton("Cancel");
    btnCancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        maingui.getInstance().replacePanel(UserFront.getInstance());
      }
    });
  }
}
