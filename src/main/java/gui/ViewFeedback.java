package gui;

import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JButton;
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
import static utils.IOUtils.getIcon;
import utils.ImageUtil;

public class ViewFeedback extends JPanel {

  private JTable table;
  private JLabel lblFeedbacks;
  private JLabel logoLabel;
  private JButton btnViewFeedbacks;
  private JButton btnBack;
  private final JScrollPane scrollPane;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          maingui.getInstance().replacePanel(new ViewFeedback());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the frame.
   */
  public ViewFeedback() {
    Border border = BorderFactory.createTitledBorder("View Feedbacks");
    setBorder(border);
    setBackground(Color.WHITE);
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    createLabels();
    createButtons();

    table = new JTable();
    table.setEnabled(false);
    table.setRowSelectionAllowed(false);
    scrollPane = new JScrollPane(table);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(5, 5, 5, 5);

    add(logoLabel, gbc);

    gbc.gridwidth = 1;
    gbc.gridy++;
    add(lblFeedbacks, gbc);
    gbc.gridx++;
    add(btnViewFeedbacks, gbc);

    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridx = 0;
    gbc.gridy++;
    add(scrollPane, gbc);
    gbc.fill =  GridBagConstraints.NONE;

    gbc.gridy++;
    add(btnBack, gbc);
  }

  private void createLabels() {
    lblFeedbacks = new JLabel("Feedbacks for us");
    lblFeedbacks.setForeground(new Color(139, 0, 0));
    lblFeedbacks.setFont(new Font("Agency FB", Font.PLAIN, 25));

    logoLabel = new JLabel("");
    logoLabel.setIcon(ImageUtil.scaleImageIcon(getIcon("logo.png"), 150));
  }

  private void createButtons() {

    btnViewFeedbacks = new JButton("View Feedbacks");
    btnViewFeedbacks.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        //TODO
      }
    });

    btnBack = new JButton("Back");
    btnBack.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        maingui.getInstance().replacePanel(AdminFront.getInstance());
      }
    });
  }
}
