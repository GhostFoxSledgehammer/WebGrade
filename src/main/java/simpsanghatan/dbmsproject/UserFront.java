package simpsanghatan.dbmsproject;

import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JButton;

import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.awt.event.ActionEvent;
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

public class UserFront extends JPanel {

  private JTextField searchField;
  private JTable table;
  Vector<Vector> rowData;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          maingui.getInstance().replacePanel(new UserFront());
          JOptionPane.showMessageDialog(null, "Welcome! \nHave a great time!");
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  private JButton btnRead;
  private JButton btnGo;
  private JButton btnFeedback;
  private JButton btnLogOut;
  private JLabel lblLogo;
  private JLabel lblResults;
  private JLabel lblEnterTheSearch;
  private JLabel lblClickOnThe;
  private final JScrollPane scrollPane;

  /**
   * Create the frame.
   */
  public UserFront() {
    Border border = BorderFactory.createTitledBorder("Search for Sites");
    setBorder(border);
    setBackground(new Color(255, 255, 255));
    setLayout(new GridBagLayout());

    createLabels();
    createFields();
    createButtons();

    table = new JTable();

    table.setCellSelectionEnabled(true);
    scrollPane = new JScrollPane(table);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridwidth = 4;
    add(lblLogo, gbc);

    gbc.gridy++;
    gbc.gridx = 0;
    gbc.gridwidth = 1;
    add(lblEnterTheSearch, gbc);
    gbc.gridx++;
    add(searchField, gbc);
    gbc.gridx++;
    add(btnGo, gbc);
    gbc.gridx++;
    add(btnRead, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 4;
    add(lblResults, gbc);

    gbc.gridy++;
    add(scrollPane, gbc);

    gbc.gridy++;
    add(lblClickOnThe, gbc);
    gbc.gridy++;
    gbc.gridx = 0;
    gbc.gridwidth = 2;
    add(btnFeedback, gbc);
    gbc.gridx++;
    add(btnLogOut, gbc);

    //add(table);
  }

  private void createButtons() {
    btnRead = new JButton("Read");
    btnRead.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        //todo
      }
    });

    btnGo = new JButton("Go");

    btnGo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        if (searchField.getText().equals("")) {
          //	System.out.println("Entering if");
          JOptionPane.showMessageDialog(null, "Please enter a search key");
        } else {
          //TODO
        }
      }

    });

    btnFeedback = new JButton("Give Feedback");
    btnFeedback.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Feedback.main(null);
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

  private void createLabels() {
    lblLogo = new JLabel("");
    lblLogo.setIcon(ImageUtil.scaleImageIcon(getIcon("logo.png"), 200));

    lblResults = new JLabel("Results");
    lblResults.setForeground(new Color(139, 0, 0));
    lblResults.setFont(new Font("Agency FB", Font.PLAIN, 30));

    lblEnterTheSearch = new JLabel("Enter the search key");
    lblEnterTheSearch.setForeground(new Color(139, 0, 0));
    lblEnterTheSearch.setFont(new Font("Agency FB", Font.PLAIN, 30));

    lblClickOnThe = new JLabel("Click on the link to open in a browser");
  }

  private void createFields() {
    searchField = new JTextField(20);
  }
}
