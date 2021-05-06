package simpsanghatan.dbmsproject;

import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import sqlhelper.settings;
import static utils.IOUtils.getIcon;
import utils.ImageUtil;

public class AdminFront extends JPanel {

  private JTextField textField;
  private JTable table;
  private JScrollPane scrollPane;
  private JLabel spiderLabel;
  private JButton btnViewFeedbacks;
  private JButton btnViewSearchHistory;
  private JButton btnLogOut;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          settings.user = "root";
          settings.pass = "stcdalex";
          maingui.getInstance().replacePanel(new AdminFront());
          JOptionPane.showMessageDialog(null, "Welcome Admin!\nHave a great time!");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  private JLabel lblGiveTheUrl;
  private JButton btnGo;

  /**
   * Create the frame.
   */
  public AdminFront() {
    Border border = BorderFactory.createTitledBorder("Admin");
    setBorder(border);
    setBackground(new Color(255, 255, 255));
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

    createLabels();
    createFields();
    createButtons();
    table = new JTable();
    table.setEnabled(false);

    scrollPane = new JScrollPane(table);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.insets = new Insets(5,5,5,5);

    add(spiderLabel, gbc);

    gbc.gridy++;
    add(lblGiveTheUrl, gbc);

    gbc.gridy++;
    add(textField, gbc);

    gbc.gridx = 2;
    add(btnGo, gbc);

    gbc.gridy++;
    gbc.gridx=0;
    gbc.gridwidth = 3;
    add(scrollPane, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.CENTER;
    add(btnViewFeedbacks, gbc);

    gbc.gridx = 2;
    add(btnViewSearchHistory, gbc);

    gbc.gridx = 1;
    gbc.gridy++;
    gbc.gridwidth = 1;
    add(btnLogOut, gbc);
  }

  private void createLabels() {
    lblGiveTheUrl = new JLabel("Give the URL to start crawling from ");
    lblGiveTheUrl.setForeground(new Color(139, 0, 0));
    lblGiveTheUrl.setFont(new Font("Agency FB", Font.PLAIN, 30));

    spiderLabel = new JLabel("");
    spiderLabel.setIcon(ImageUtil.scaleImageIcon(getIcon("spider.jpg"), 200));
  }

  private void createFields() {
    textField = new JTextField(30);
  }

  private void createButtons() {
    btnGo = new JButton("Go");
    btnGo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (textField.getText().equals("")) {
          JOptionPane.showMessageDialog(null, "Seems like you have not entered a URL :)");
        } else {
          Vector<Vector> rowData;
          Vector<Vector> colData;
          //
        }
      }
    });
    btnViewFeedbacks = new JButton("View Feedbacks");
    btnViewFeedbacks.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        //TODO
      }
    });

    btnViewSearchHistory = new JButton("Manage Admins");
    btnViewSearchHistory.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        
      }
    });

    btnLogOut = new JButton("Log out");
    btnLogOut.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        settings.userId = -1;
        maingui.getInstance().replacePanel(new LoginPage());
      }
    });
  }
}
