/*
 * 
 */
package sqlhelper;

import utils.PasswordAuthentication;

/**
 *
 * @author kshan
 */
public class settings {

  // JDBC driver name and database URL
  public static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
  public static String DB_URL = "jdbc:mysql://localhost/";
  public static int userId = -1;
  public static PasswordAuthentication passwordAuth = new PasswordAuthentication();
  public static int usertype = -1;

  public static void logOut() {
    userId = -1;
    usertype = -1;
  }

  public static void logIn(int id, int type) {
    settings.userId = id;
    settings.usertype = type;
  }
}
