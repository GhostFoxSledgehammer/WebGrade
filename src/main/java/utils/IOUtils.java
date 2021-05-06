/*
 * 
 */
package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author kshan
 */
public class IOUtils {

  public static InputStream getFileFromResourceAsStream(String fileName) {

    // The class loader that loaded the class
    Class currentClass = new Object() {
    }.getClass().getEnclosingClass();
    ClassLoader classLoader = currentClass.getClassLoader();
    InputStream inputStream = classLoader.getResourceAsStream(fileName);

    // the stream holding the file content
    if (inputStream == null) {
      return null;
    } else {
      return inputStream;
    }

  }

  public static ImageIcon getIcon(String filename) {
    ImageIcon icon = new ImageIcon();
    try {
      icon = new ImageIcon(ImageIO.read(getFileFromResourceAsStream(filename)));
    } catch (IOException ex) {
      Logger.getLogger(IOUtils.class.getName()).log(Level.SEVERE, null, ex);
    }
    return icon;
  }
}
