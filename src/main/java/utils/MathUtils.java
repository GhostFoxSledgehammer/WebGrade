/*
 * 
 */
package utils;

import java.awt.Dimension;

/**
 *
 * @author kshan
 */
public class MathUtils {

  /**
   * This program coverts Dimension of scaling value 1 to given scaling value.
   * @param size The original dimension
   * @param scaling The windows scaling value, 125% will be 1.25.
   * @return The java dimension with  height and width of component in the given scaling value.
   */
  public static Dimension scaleDimension(Dimension size, double scaling) {
    int width = (int) (size.getWidth() * scaling);
    int height = (int) (size.getHeight() * scaling);
    return new Dimension(width, height);
  }
  
}
