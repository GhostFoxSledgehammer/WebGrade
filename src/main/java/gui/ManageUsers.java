/*
 * 
 */
package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import sqlhelper.ConnectionLostError;
import sqlhelper.Queries;
import sqlhelper.Queries.user;
import sqlhelper.settings;
import static utils.IOUtils.getIcon;
import utils.ImageUtil;

/**
 *
 * @author kshan
 */
public class ManageUsers extends JPanel {

  private static ManageUsers instance;

  private static void getUsers() {
  }

  private JButton btnShow;
  private JButton btnDelete;
  private JButton btnMakeAdmin;
  private JTable table;
  private DefaultTableModel model;
  private JButton btnRemoveAdmin;
  private JButton btnBack;
  private int usercol;
  private final JScrollPane scrollPane;
  private JLabel logoLabel;
  private int typecol;

  public enum UserType {
    User(0),
    Admin(1),
    SuperAdmin(2);
    private final int value;

    UserType(final int type) {
      value = type;
    }

    public int getValue() {
      return value;
    }

    public static Optional<UserType> valueOf(int value) {
      return Arrays.stream(values())
              .filter(userType -> userType.value == value)
              .findFirst();
    }
  }

  private ManageUsers() {
    Border border = BorderFactory.createTitledBorder("Manage Users");
    setBorder(border);
    setBackground(new Color(255, 255, 255));
    setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    createLabels();
    createButtons();
    createTable();
    scrollPane = new JScrollPane(table);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.gridwidth = 4;
    add(logoLabel, gbc);
    gbc.gridwidth = 1;

    gbc.gridy++;
    add(btnShow, gbc);
    gbc.gridx++;
    add(btnDelete, gbc);
    gbc.gridx++;
    add(btnMakeAdmin, gbc);
    gbc.gridx++;
    add(btnRemoveAdmin, gbc);

    gbc.gridy++;
    gbc.gridx = 0;
    gbc.gridwidth = 4;
    add(scrollPane, gbc);

    gbc.gridy++;
    add(btnBack, gbc);
  }

  public static ManageUsers getInstance() {
    if (instance == null) {
      instance = new ManageUsers();
    }
    return instance;
  }

  private void createLabels() {
    logoLabel = new JLabel("");
    logoLabel.setIcon(ImageUtil.scaleImageIcon(getIcon("logo.png"), 200));
  }

  private void createButtons() {
    btnShow = new JButton("Show Users");
    btnShow.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        clearTable();
        new Thread(() -> {
          getUsers();
        }).start();
      }
    });

    btnBack = new JButton("Back");
    btnBack.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        maingui.getInstance().replacePanel(AdminFront.getInstance());
      }
    });
    btnDelete = new JButton("Delete User");
    btnDelete.setEnabled(false);
    btnDelete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        deleteUser();
      }
    });
    btnMakeAdmin = new JButton("Make Admin");
    btnMakeAdmin.setEnabled(false);
    btnMakeAdmin.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        makeAdmin();
      }
    });
    btnRemoveAdmin = new JButton("Remove from Admin");
    btnRemoveAdmin.setEnabled(false);
    btnRemoveAdmin.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeAdmin();
      }
    });
  }

  private void createTable() {
    table = new JTable();
    model = new DefaultTableModel();
    model.addColumn("S.no");
    model.addColumn("Username");
    usercol = model.findColumn("Username");
    model.addColumn("Type");
    typecol = model.findColumn("Type");
    table.setModel(model);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setDefaultEditor(Object.class, null);//Uneditable
    table.getColumnModel().getColumn(0).setMinWidth(50);
    table.getColumnModel().getColumn(0).setMaxWidth(70);
    table.getTableHeader().setReorderingAllowed(false);
    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent event) {
        if (table.getSelectionModel().isSelectionEmpty()) {
          btnMakeAdmin.setEnabled(false);
          btnDelete.setEnabled(false);
          btnRemoveAdmin.setEnabled(false);
        } else {
          int row = table.getSelectedRow();
          int col = model.findColumn("Type");
          user auser = (user) model.getValueAt(row, usercol);
          btnMakeAdmin.setEnabled(auser.type < settings.usertype);
          btnDelete.setEnabled(auser.type < settings.usertype);
          btnRemoveAdmin.setEnabled(auser.type > 0 && auser.type < settings.usertype);
        }
      }
    });
  }

  private void clearTable() {
    try {
      ArrayList<user> users = Queries.getUsers();
      updateUsers(users);
    } catch (ConnectionLostError ex) {
      maingui.getInstance().ConnectionLost();
    }
  }

  private void updateUsers(ArrayList<user> users) {
    if (!SwingUtilities.isEventDispatchThread()) {
      SwingUtilities.invokeLater(() -> updateUsers(users));
      return;
    }
    Iterator<user> it = users.iterator();
    int i = 1;
    while (it.hasNext()) {
      user auser = it.next();
      model.addRow(new Object[]{i, auser, UserType.valueOf(auser.type).get()});
      i++;
    }
    model.fireTableDataChanged();
  }

  private void deleteUser() {
    String[] options = {"Yes", "No"};
    int x = JOptionPane.showOptionDialog(maingui.getInstance(), "This is permanent, are you sure?",
            "Delete User", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
    if (x == 0) {
      int row = table.getSelectedRow();
      user auser = (user) model.getValueAt(row, usercol);
      try {
        boolean deleted = Queries.deleteUser(auser.id);
        if (deleted) {
          model.removeRow(row);
          model.fireTableRowsDeleted(row, row + 1);
        }
      } catch (ConnectionLostError ex) {
        maingui.getInstance().ConnectionLost();
      }
    }
  }

  private void makeAdmin() {
    int row = table.getSelectedRow();
    user auser = (user) model.getValueAt(row, usercol);
    try {
      boolean updated = Queries.makeAdmin(auser.id);
      if (updated) {
        auser.type = 1;
        model.setValueAt(UserType.valueOf(1).get(), row, typecol);
        model.fireTableRowsUpdated(row, row + 1);
        table.getSelectionModel().clearSelection();
      }
    } catch (ConnectionLostError ex) {
      maingui.getInstance().ConnectionLost();
    }
  }

  private void removeAdmin() {
    int row = table.getSelectedRow();
    user auser = (user) model.getValueAt(row, usercol);
    try {
      boolean updated = Queries.removeAdmin(auser.id);
      if (updated) {
        auser.type = 0;
        model.setValueAt(UserType.valueOf(0).get(), row, typecol);
        model.fireTableRowsUpdated(row, row + 1);
        table.getSelectionModel().clearSelection();
      }
    } catch (ConnectionLostError ex) {
      maingui.getInstance().ConnectionLost();
    }
  }

  public static void main(String[] args) {
    maingui.getInstance().replacePanel(getInstance());
  }
}
