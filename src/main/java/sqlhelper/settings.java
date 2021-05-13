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
}
