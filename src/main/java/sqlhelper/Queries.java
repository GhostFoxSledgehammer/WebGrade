/*
 * 
 */
package sqlhelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static sqlhelper.settings.DB_URL;

/**
 *
 * @author kshan
 */
public class Queries {

  static Connection conn = null;
  static Statement stmt = null;

  public static boolean connect(String user, String pass) {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager.getConnection(DB_URL, user, pass);
      stmt = conn.createStatement();
      createdatabase();
      createtables();
      return true;
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ConnectionLostError ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public static boolean createdatabase() throws ConnectionLostError {
    checkConnection();
    String sql = "CREATE DATABASE IF NOT EXISTS webranking";
    String use = "USE webranking";
    try {
      stmt.executeUpdate(sql);
      stmt.executeUpdate(use);
      return true;
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public static boolean createtables() throws ConnectionLostError {
    checkConnection();
    String logintab = "CREATE TABLE IF NOT EXISTS login "
            + "(id INTEGER unsigned AUTO_INCREMENT, "
            + " password VARCHAR(70) NOT NULL, "
            + " username VARCHAR(20) NOT NULL UNIQUE,"
            + "isadmin BOOL NOT NULL,"
            + "PRIMARY KEY (id))";
    String websitetab = "CREATE TABLE IF NOT EXISTS website "
            + "(id INTEGER unsigned AUTO_INCREMENT, "
            + " url VARCHAR(200) NOT NULL, "
            + "title VARCHAR(100),"
            + " hits INTEGER Default 0,"
            + "PRIMARY KEY (id))";
    String feedback_tab = "CREATE TABLE IF NOT EXISTS feedbacks("
            + "id BIGINT NOT NULL AUTO_INCREMENT,"
            + "feedback VARCHAR(500),"
            + "seen BOOL DEFAULT 0,"
            + "PRIMARY KEY (id))";
    try {
      stmt.executeUpdate(logintab);
      stmt.executeUpdate(websitetab);
      stmt.executeUpdate(feedback_tab);
      if (checkUser("admin") == -1) {
        Queries.createUser("admin", "admin", 2);
      }
      return true;
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public static int loginUser(String username, String password) throws ConnectionLostError {
    checkConnection();
    String sql = "Select id,password,isadmin from login WHERE username = '"
            + username + "'";
    try {
      ResultSet rs = stmt.executeQuery(sql);
      if (rs.next()) {
        int id = rs.getInt(1);
        String token = rs.getString(2);
        if (settings.passwordAuth.authenticate(password.toCharArray(), token)) {
          settings.logIn(id, rs.getInt(3));
          return id;
        }
      }
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return -1;
  }

  public static int checkUser(String username) throws ConnectionLostError {
    checkConnection();
    String sql = "Select id from login WHERE username = '"
            + username + "'";
    try {
      ResultSet rs = stmt.executeQuery(sql);
      if (rs.next()) {
        return rs.getInt(1);
      }
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return -1;
  }

  public static boolean createUser(String username, String password) throws ConnectionLostError {
    return createUser(username, password, 0);
  }

  public static boolean createUser(String username, String password, int adminType) throws ConnectionLostError {
    checkConnection();
    password = settings.passwordAuth.hash(password.toCharArray());
    String sql = "INSERT INTO login(username, password, isadmin) "
            + "VALUES ('" + username + "','" + password + "'," + adminType
            + ")";
    try {
      stmt.executeUpdate(sql);
      return true;
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public static int checkLink(String link) throws ConnectionLostError {
    checkConnection();
    String sql = "Select id from website WHERE url = '" + link + "'";
    try {
      ResultSet rs = stmt.executeQuery(sql);
      if (rs.next()) {
        return rs.getInt(1);
      }
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return -1;
  }

  public static int isAdmin(int id) throws ConnectionLostError {
    checkConnection();
    String sql = "SELECT isadmin from login WHERE id = " + id;
    try {
      ResultSet rs = stmt.executeQuery(sql);
      if (rs.next()) {
        return rs.getInt(1);
      }
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return 0;
  }

  public static boolean insertLink(String title, String link) throws ConnectionLostError {
    checkConnection();
    if (checkLink(link) != -1) {
      return false;
    }
    try {
      PreparedStatement newstmt = conn.prepareStatement("INSERT INTO  website(url, title) "
              + "VALUES (?,?)");
      newstmt.setString(1, link);
      newstmt.setString(2, title);
      newstmt.executeUpdate();
      return true;
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return true;
  }

  public static void checkConnection() throws ConnectionLostError {
    if (conn == null) {
      throw new ConnectionLostError();
    }
  }

  public static ArrayList<link> searchKeys(String[] keys) throws ConnectionLostError {
    checkConnection();
    int l = keys.length;
    StringBuilder sql = new StringBuilder("SELECT title, url FROM website WHERE (title LIKE '%"
            + keys[0] + "%' ");
    for (int i = 1; i < l; i++) {
      sql.append("\n OR title LIKE '%").append(keys[i]).append("% \' ");
    }
    sql.append(") ORDER BY hits DESC");
    try {
      ResultSet rs = stmt.executeQuery(sql.toString());
      ArrayList<link> links = new ArrayList();
      while (rs.next()) {
        links.add(new link(rs.getString(1), rs.getString(2)));
      }
      return links;
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public static boolean updateHits(String link) throws ConnectionLostError {
    checkConnection();
    int id = checkLink(link);
    if (id == -1) {
      System.out.println("not found");
      return false;
    } else {
      try {
        String sql = "UPDATE website \n"
                + "SET hits = hits + 1 \n"
                + "WHERE id = " + id;
        stmt.executeUpdate(sql);
        return true;
      } catch (SQLException ex) {
        Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return false;
  }

  public static boolean submitFeedback(String feedback) throws ConnectionLostError {
    checkConnection();
    if (feedback.length() > 500) {
      return false;
    }
    String sql = "INSERT INTO feedbacks(feedback)\n"
            + "VALUES('" + feedback + "')";
    try {
      stmt.executeUpdate(sql);
      return true;
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public static ArrayList<feedback> getFeedbacks() throws ConnectionLostError {
    checkConnection();
    String sql = "SELECT * FROM feedbacks";
    try {
      ResultSet rs = stmt.executeQuery(sql);
      ArrayList<feedback> feedbacks = new ArrayList();
      while (rs.next()) {
        feedbacks.add(new feedback(rs.getInt(1), rs.getString(2), rs.getBoolean(3)));
      }
      return feedbacks;
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public static boolean markAsRead(feedback feed) throws ConnectionLostError {
    checkConnection();
    String sql = "UPDATE feedbacks \n"
            + "SET seen = 1 \n"
            + "WHERE id = " + feed.id;
    try {
      stmt.executeUpdate(sql);
      return true;
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public static boolean deleteFeedback(feedback feed) throws ConnectionLostError {
    checkConnection();
    String sql = "DELETE from feedbacks \n"
            + "WHERE id = " + feed.id;
    try {
      stmt.executeUpdate(sql);
      return true;
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public static ArrayList<user> getUsers() throws ConnectionLostError {
    checkConnection();
    String sql = "SELECT id,username,isadmin FROM login";
    try {
      ResultSet rs = stmt.executeQuery(sql);
      ArrayList<user> users = new ArrayList();
      while (rs.next()) {
        users.add(new user(rs.getInt(1), rs.getString(2), rs.getInt(3)));
      }
      return users;
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public static boolean deleteUser(int id) throws ConnectionLostError {
    checkConnection();
    String sql = "DELETE from login \n"
            + "WHERE id  = " + id;
    try {
      stmt.executeUpdate(sql);
      return true;
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public static boolean makeAdmin(int id) throws ConnectionLostError {
    checkConnection();
    String sql = "UPDATE login \n"
            + "SET isadmin = 1\n"
            + "WHERE id  = " + id;
    try {
      stmt.executeUpdate(sql);
      return true;
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public static boolean removeAdmin(int id) throws ConnectionLostError {
    checkConnection();
    String sql = "UPDATE login \n"
            + "SET isadmin = 0\n"
            + "WHERE id  = " + id;
    try {
      stmt.executeUpdate(sql);
      return true;
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public static class link {

    public String url, title;

    public link(String title, String url) {
      this.url = url;
      this.title = title;
    }
  }

  public static class feedback {

    public int id;
    public String feedback;
    public boolean seen;

    public feedback(String feed, boolean seen) {
      this.feedback = feed;
      this.seen = seen;
    }

    public feedback(int id, String feed, boolean seen) {
      this.id = id;
      this.feedback = feed;
      this.seen = seen;
    }

    @Override
    public String toString() {
      return feedback;
    }
  }

  public static class user {

    public String username;
    public int type, id;

    public user(int id, String username, int type) {
      this.id = id;
      this.username = username;
      this.type = type;
    }

    @Override
    public String toString() {
      return username;
    }
  }
}
