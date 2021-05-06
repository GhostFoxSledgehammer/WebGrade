/*
 * 
 */
package sqlhelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import static sqlhelper.settings.DB_URL;
import static sqlhelper.settings.pass;
import static sqlhelper.settings.user;

/**
 *
 * @author kshan
 */
public class Queries {

  static Connection conn = null;
  static Statement stmt = null;

  public static boolean connect() {
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
    }
    return false;
  }

  public static boolean createdatabase() {
    if (conn == null || stmt == null) {
      connect();
    }
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

  public static boolean createtables() {
    if (conn == null || stmt == null) {
      connect();
    }
    String logintab = "CREATE TABLE IF NOT EXISTS login "
            + "(id INTEGER unsigned AUTO_INCREMENT, "
            + " password VARCHAR(70) NOT NULL, "
            + " username VARCHAR(20) NOT NULL,"
            + "isadmin BOOL NOT NULL,"
            + "PRIMARY KEY (id))";
    String websitetab = "CREATE TABLE IF NOT EXISTS website "
            + "(id INTEGER unsigned AUTO_INCREMENT, "
            + " url VARCHAR NOT NULL, "
            + "title VARCHAR(100),"
            + " hits INTEGER NOT NULL,"
            + "PRIMARY KEY (id))";
    String keywordtab = "CREATE TABLE IF NOT EXISTS keyword"
            + "(id INTEGER unsigned AUTO_INCREMENT,"
            + "keyword VARCHAR(15) NOT NULL,"
            + "PRIMARY KEY (id))";
    String keyword_website_tab = "CREATE TABLE IF NOT EXISTS keyword_website ("
            + "webid int unsigned not null,"
            + "keyid int unsigned not null, "
            + "CONSTRAINT keyword_website_keyword foreign key (webid) references website(id),"
            + "CONSTRAINT keyword_website_website foreign key (keyid) references keyword(id), "
            + "CONSTRAINT keyword_website_unique UNIQUE (webid, keyid))";
    try {
      stmt.executeUpdate(logintab);
      stmt.executeUpdate(websitetab);
      stmt.executeUpdate(keywordtab);
      stmt.executeUpdate(keyword_website_tab);
      return true;
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public static int checkUser(String username, String password) {
    if (conn == null || stmt == null) {
      connect();
    }
    String sql = "Select * from login WHERE username = '"
            + username + "' and password = '" + password + "'";
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

  public static void main(String[] args) {
    settings.user = "root";
    settings.pass = "stcdalex";
    connect();
  }

  public static void createUser(String username, String password) {
    if (conn == null || stmt == null) {
      connect();
    }
    String sql = "INSERT INTO login(username, password, isadmin) " + 
            "VALUES ('" + username + "','" + password + "',0)";
    try {
      stmt.executeUpdate(sql);
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  

  public static void createUser(String username, String password, boolean isadmin) {
    if (conn == null || stmt == null) {
      connect();
    }
    String sql = "INSERT INTO login(username, password, isadmin) " + 
            "VALUES ('" + username + "','" + password + "'," + (isadmin ? "1" : "0")
            + ")";
    try {
      stmt.executeUpdate(sql);
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static boolean isAdmin(int id) {
    if (conn == null || stmt == null) {
      connect();
    }
    String sql = "SELECT isadmin from login WHERE id = " + id;
    try {
      ResultSet rs = stmt.executeQuery(sql);
      if (rs.next()) {
        return rs.getInt(1) != 0;
      }
    } catch (SQLException ex) {
      Logger.getLogger(Queries.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public static void insertLink(String title, String link) {
  }
}
