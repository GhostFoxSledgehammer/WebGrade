/*
 * 
 */
package crawler;

/**
 *
 * @author kshan
 */
public interface CrawlerListener {
  /**
   * Fired when new links are added
   */
  void linkAdded(String URL, String title);
}
