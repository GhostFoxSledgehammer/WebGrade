package gui;

import crawler.Crawler;
import crawler.CrawlerListener;
import java.awt.EventQueue;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import sqlhelper.settings;
import static utils.IOUtils.getIcon;
import utils.ImageUtil;

public class AdminFront extends JPanel implements CrawlerListener {

  private static AdminFront instance;

  private JTextField urlField;
  private JTable table;
  private JScrollPane scrollPane;
  private JLabel spiderLabel;
  private JButton btnViewFeedbacks;
  private JButton btnManageAdmins;
  private JButton btnLogOut;

  private JLabel lblGiveTheUrl;
  private JButton btnGo;
  private DefaultTableModel model;

  /**
   * Create the frame.
   */
  private AdminFront() {
    Crawler.getInstance().addListener(this);
    Border border = BorderFactory.createTitledBorder("Admin");
    setBorder(border);
    setBackground(new Color(255, 255, 255));
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();

    createLabels();
    createFields();
    createButtons();
    createTable();

    scrollPane = new JScrollPane(table);

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.insets = new Insets(5, 5, 5, 5);

    add(spiderLabel, gbc);

    gbc.gridy++;
    add(lblGiveTheUrl, gbc);

    gbc.gridy++;
    add(urlField, gbc);

    gbc.gridx = 2;
    add(btnGo, gbc);

    gbc.gridy++;
    gbc.gridx = 0;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.BOTH;
    add(scrollPane, gbc);
    gbc.fill = GridBagConstraints.NONE;

    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 1;
    gbc.anchor = GridBagConstraints.CENTER;
    add(btnViewFeedbacks, gbc);

    gbc.gridx = 2;
    add(btnManageAdmins, gbc);

    gbc.gridx = 1;
    gbc.gridy++;
    gbc.gridwidth = 1;
    add(btnLogOut, gbc);
    JOptionPane.showMessageDialog(this, "Welcome Admin!\nHave a great time!");
  }

  public static AdminFront getInstance() {
    if (instance == null) {
      instance = new AdminFront();
    }
    return instance;
  }
  private void createLabels() {
    lblGiveTheUrl = new JLabel("Give the URL to start crawling from ");
    lblGiveTheUrl.setForeground(new Color(139, 0, 0));
    lblGiveTheUrl.setFont(new Font("Agency FB", Font.PLAIN, 30));

    spiderLabel = new JLabel("");
    spiderLabel.setIcon(ImageUtil.scaleImageIcon(getIcon("spider.jpg"), 200));
  }

  private void createFields() {
    urlField = new JTextField(30);
  }

  private void createButtons() {
    btnGo = new JButton("Go");
    btnGo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        String link = urlField.getText();
        if (link.isEmpty()) {
          JOptionPane.showMessageDialog(null, "Seems like you have not entered a URL :)");
        } else {
          clearTable();
          Crawler.getInstance().startCrawling(link, 0);
        }
      }
    });
    btnViewFeedbacks = new JButton("View Feedbacks");
    btnViewFeedbacks.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
         maingui.getInstance().replacePanel(new ViewFeedback());
      }
    });

    btnManageAdmins = new JButton("Manage Admins");
    btnManageAdmins.addActionListener(new ActionListener() {
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

  private void clearTable() {
    int rowCount = model.getRowCount();
    for (int i = rowCount - 1; i >= 0; i--) {
      model.removeRow(i);
    }
  }

  @Override
  public void linkAdded(String URL, String title) {
    if (!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(() -> linkAdded(URL, title));
      return;
    }
    model.addRow(new Object[]{title, URL});
    model.fireTableCellUpdated(model.getRowCount(), model.getColumnCount());
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
            } catch (URISyntaxException ex) {
              Logger.getLogger(AdminFront.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
              Logger.getLogger(AdminFront.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        }
      }
    });
  }
}
