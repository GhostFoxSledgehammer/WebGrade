package gui;

import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import sqlhelper.ConnectionLostError;
import sqlhelper.Queries;
import sqlhelper.Queries.feedback;
import static utils.IOUtils.getIcon;
import utils.ImageUtil;

public class ViewFeedback extends JPanel {

  private static ViewFeedback instance;
  private int feedcol;
  private JTable table;
  private JLabel lblFeedbacks;
  private JLabel logoLabel;
  private JButton btnViewFeedbacks, btnBack, btnRead, btnMarkRead;
  private final JScrollPane scrollPane;

  private DefaultTableModel model;
  private JButton btnDelete;

  /**
   * Create the frame.
   */
  private ViewFeedback() {
    Border border = BorderFactory.createTitledBorder("View Feedbacks");
    setBorder(border);
    setBackground(Color.WHITE);
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

    createLabels();
    createButtons();
    createTables();
    scrollPane = new JScrollPane(table);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.gridwidth = 3;
    gbc.insets = new Insets(5, 5, 5, 5);

    add(logoLabel, gbc);

    gbc.gridwidth = 1;
    gbc.gridy++;
    add(lblFeedbacks, gbc);
    gbc.gridx++;
    add(btnViewFeedbacks, gbc);
    gbc.gridx++;
    add(btnRead, gbc);
    gbc.gridy++;
    add(btnDelete, gbc);

    gbc.gridwidth = 3;
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.fill = GridBagConstraints.BOTH;
    add(scrollPane, gbc);
    gbc.fill = GridBagConstraints.NONE;

    gbc.gridy++;
    add(btnBack, gbc);
  }

  public static ViewFeedback getInstance() {
    if (instance == null) {
      instance = new ViewFeedback();
    }
    return instance;
  }

  private void createLabels() {
    lblFeedbacks = new JLabel("Feedbacks for us");
    lblFeedbacks.setForeground(new Color(139, 0, 0));
    lblFeedbacks.setFont(new Font("Agency FB", Font.PLAIN, 25));

    logoLabel = new JLabel("");
    logoLabel.setIcon(ImageUtil.scaleImageIcon(getIcon("logo.png"), 200));
  }

  private void createButtons() {

    btnViewFeedbacks = new JButton("View Feedbacks");
    btnViewFeedbacks.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        clearTable();
        new Thread(() -> {
          getFeedback();
        }).start();
      }
    });

    btnBack = new JButton("Back");
    btnBack.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        maingui.getInstance().replacePanel(AdminFront.getInstance());
      }
    });

    btnRead = new JButton("Read");
    btnRead.setEnabled(false);
    btnRead.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        readFeedbacks();
      }
    });

    btnDelete = new JButton("Delete");
    btnDelete.setEnabled(false);
    btnDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        deleteFeedback();
      }
    });
  }

  private void createTables() {
    model = new DefaultTableModel();
    model.addColumn("S.no");
    model.addColumn("Feedback");
    feedcol = 1;
    table = new JTable();
    table.setModel(model);
    table.setRowSelectionAllowed(true);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setDefaultEditor(Object.class, null);//Uneditable
    table.getColumnModel().getColumn(0).setMinWidth(50);
    table.getColumnModel().getColumn(0).setMaxWidth(70);
    table.getTableHeader().setReorderingAllowed(false);
    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent event) {
        if (table.getSelectionModel().isSelectionEmpty()) {
          btnRead.setEnabled(false);
          btnDelete.setEnabled(false);
        } else {
          btnRead.setEnabled(true);
          btnDelete.setEnabled(true);
        }
      }
    });
    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table,
              Object value, boolean isSelected, boolean hasFocus, int row, int col) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        int column = table.getColumn("Feedback").getModelIndex();
        feedback feed = (feedback) table.getModel().getValueAt(row, column);
        if (feed.seen) {
          setBackground(table.getBackground().darker());
          setForeground(table.getForeground().darker());
        } else {
          setBackground(table.getBackground());
          setForeground(table.getForeground());
        }
        return this;
      }
    });
  }

  private void clearTable() {
    int rowCount = model.getRowCount();
    for (int i = rowCount - 1; i >= 0; i--) {
      model.removeRow(i);
    }
  }

  private void getFeedback() {
    try {
      ArrayList<feedback> feedbacks = Queries.getFeedbacks();
      updateFeedbacks(feedbacks);
    } catch (ConnectionLostError ex) {
      maingui.getInstance().ConnectionLost();
    }
  }

  private void updateFeedbacks(ArrayList<Queries.feedback> feedbacks) {
    if (!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(() -> updateFeedbacks(feedbacks));
      return;
    }
    Iterator<feedback> it = feedbacks.iterator();
    int i = 1;
    while (it.hasNext()) {
      feedback afeed = it.next();
      model.addRow(new Object[]{i, afeed});
    }
    model.fireTableDataChanged();
  }

  private void readFeedbacks() {
    int row = table.getSelectedRow();
    feedback feed = (feedback) model.getValueAt(row, feedcol);
    JTextArea jta = new JTextArea(feed.feedback);
    String[] options = {"Marks as Read", "Close"};
    int x = JOptionPane.showOptionDialog(maingui.getInstance(), jta,
            "View Feedback", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    if (x == 0) {
      try {
        boolean marked = Queries.markAsRead(feed);
        if (marked) {
          feed.seen = true;
          model.fireTableRowsUpdated(row, row + 1);
        }
      } catch (ConnectionLostError ex) {
        maingui.getInstance().ConnectionLost();
      }
    }

  }

  private void deleteFeedback() {
    int row = table.getSelectedRow();
    feedback feed = (feedback) model.getValueAt(row, feedcol);
    String[] options = {"Yes", "No"};
    int x = JOptionPane.showOptionDialog(maingui.getInstance(), "This is permanent, are you sure?",
            "Delete Feedback", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    if (x == 0) {
      try {
        boolean deleted = Queries.deleteFeedback(feed);
        if (deleted) {
          model.removeRow(row);
          model.fireTableRowsDeleted(row, row + 1);
        }
      } catch (ConnectionLostError ex) {
        maingui.getInstance().ConnectionLost();
      }
    }
  }
}
