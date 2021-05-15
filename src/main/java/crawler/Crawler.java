/*
 * 
 */
package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import sqlhelper.ConnectionLostError;
import sqlhelper.Queries;

/*
http://www.mkyong.com/
 */
public class Crawler {

  private static int MAX_DEPTH = 2;
  private static Crawler instance;
  private LinkedHashMap<String, String> links;
  private ArrayList<CrawlerListener> listeners = new ArrayList();
  private Thread thread;

  private Crawler() {
    links = new LinkedHashMap<>();
    thread = new Thread();
  }

  public static Crawler getInstance() {
    if (instance == null) {
      instance = new Crawler();
    }
    return instance;
  }

  public void getPageLinks(String URL, int depth) {
    if (Thread.currentThread().isInterrupted()) {
      return;
    }
    if ((!links.containsKey(URL) && (depth <= MAX_DEPTH))) {
      try {
        System.out.println(depth);
        Document document = Jsoup.connect(URL).get();
        String title = document.title();
        try {
          title = java.net.URLDecoder.decode(title, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
          // not going to happen - value came from JDK's own StandardCharsets
        }
        addLink(URL, title);
        Elements linksOnPage = document.select("a[href]");
        depth++;
        for (Element page : linksOnPage) {
          getPageLinks(page.attr("abs:href"), depth);
        }
      } catch (IOException | IllegalArgumentException e) {
        System.err.println("For '" + URL + "': " + e.getMessage());
      }
    }
  }

  public void startCrawling(String URL, int depth) {
    stop();
    MAX_DEPTH = depth;
    thread = new Thread(new Runnable() {
      @Override
      public void run() {
        getPageLinks(URL, 0);
      }
    });
    thread.start();
  }

  public void stop() {
    thread.interrupt();
    links.clear();
  }
//  public static void main(String[] args) {
//    new Crawler().getPageLinks("http://www.mkyong.com/", 1);
//  }

  private void addLink(String URL, String title) {
    try {
      Queries.insertLink(title, URL);
    } catch (ConnectionLostError ex) {
      stop();
      return;
    }
    links.put(URL, title);
    listeners.stream().filter(Objects::nonNull).forEach(lis -> lis.linkAdded(URL, title));
  }

  private HashMap<String, String> getLinks() {
    return links;
  }

  public final void addListener(CrawlerListener lis) {
    listeners.add(lis);
  }

  public void removeListener(CrawlerListener lis) {
    listeners.remove(lis);
  }
}
