package gui;

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
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import sqlhelper.ConnectionLostError;
import sqlhelper.Queries;
import sqlhelper.Queries.link;
import sqlhelper.settings;
import static utils.IOUtils.getIcon;
import utils.ImageUtil;

public class UserFront extends JPanel {

  private static UserFront instance;

  private JTextField searchField;
  private JTable table;
  Vector<Vector> rowData;

  private JButton btnRead;
  private JButton btnGo;
  private JButton btnFeedback;
  private JButton btnLogOut;
  private JLabel lblLogo;
  private JLabel lblResults;
  private JLabel lblEnterTheSearch;
  private JLabel lblClickOnThe;
  private final JScrollPane scrollPane;
  private DefaultTableModel model;

  /**
   * Create the frame.
   */
  private UserFront() {
    Border border = BorderFactory.createTitledBorder("Search for Sites");
    setBorder(border);
    setBackground(new Color(255, 255, 255));
    setLayout(new GridBagLayout());

    createLabels();
    createFields();
    createButtons();
    createTable();

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
    gbc.fill = GridBagConstraints.BOTH;
    add(scrollPane, gbc);
    gbc.fill = GridBagConstraints.NONE;

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

  public static UserFront getInstance() {
    if (instance == null) {
      instance = new UserFront();
    }
    return instance;
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
        String key = searchField.getText();
        if (key.equals("")) {
          JOptionPane.showMessageDialog(null, "Please enter a search key");
          return;
        } else {
          clearTable();
          new Thread(() -> {
            UserFront.search(key);
          }).start();
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

  private static void search(String key) {
    try {
      String[] keys = key.split("\\W+");
      ArrayList<link> links = Queries.searchKeys(keys);
      if (links != null) {
        updateResult(links);
      }
    } catch (ConnectionLostError ex) {
      Logger.getLogger(UserFront.class.getName()).log(Level.SEVERE, null, ex);
      maingui.getInstance().ConnectionLost();
    }

  }

  private static void updateResult(ArrayList<link> links) {
    if (!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(() -> updateResult(links));
      return;
    }
    Iterator<link> it = links.iterator();
    while (it.hasNext()) {
      link alink = it.next();
      getInstance().model.addRow(new Object[]{alink.title, alink.url});
    }
    getInstance().model.fireTableDataChanged();
  }

  private void createTable() {

    model = new DefaultTableModel();
    model.addColumn("Title");
    model.addColumn("Link");

    table = new JTable();
    table.setModel(model);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setDefaultEditor(Object.class, null);//Uneditable
    table.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          JTable target = (JTable) e.getSource();
          int row = target.getSelectedRow();
          int colIndex = model.findColumn("Link");
          Object link = model.getValueAt(row, colIndex);
          if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
              Desktop.getDesktop().browse(new URI(link.toString()));
              Queries.updateHits(link.toString());
            } catch (URISyntaxException ex) {
              Logger.getLogger(AdminFront.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
              Logger.getLogger(AdminFront.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ConnectionLostError ex) {
              maingui.getInstance().ConnectionLost();
            }
          }
        }
      }
    });
  }

  private void clearTable() {
    int rowCount = model.getRowCount();
    for (int i = rowCount - 1; i >= 0; i--) {
      model.removeRow(i);
    }
  }
}
