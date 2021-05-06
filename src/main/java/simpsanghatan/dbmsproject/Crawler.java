/*
 * 
 */
package simpsanghatan.dbmsproject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sqlhelper.Queries;

public class Crawler {

  private String htmlCode;

  public String getTitle(String input) {
    String title = "";

    System.out.println("get title called");
    String theTitleRegex = Pattern.quote("<title>") + "(.*?)" + Pattern.quote("</title>");
    Pattern checkTitleRegex = Pattern.compile(theTitleRegex);
    Matcher matcher = checkTitleRegex.matcher(input);
    while (matcher.find()) {
      title = matcher.group(1);
    }
    return title;
  }

  public Vector<Vector> titleFetcher(ArrayList<String> cleanedUrls) {

    String subHtmlCode = "";
    Vector<Vector> rowData = new Vector<>();

    //for(int j=0;j<10;j++) 
    for (String subH : cleanedUrls) {
      if (!subH.contains(".css") && !subH.contains(".js") && !subH.contains(".aspx")) {
        String subTitle = "";
        subHtmlCode = "";
        subH = "";
        //String subH=cleanedUrls.get(j);
        try {
          //url = new URL("https://www.google.com");   /////check
          URL url = new URL(subH);
          URLConnection urlcon = url.openConnection();
          urlcon.addRequestProperty("User-Agent", "Chrome/62.0.3202.94");
          InputStream stream = urlcon.getInputStream();
          int i;
          while ((i = stream.read()) != -1) {
            subHtmlCode = subHtmlCode + (char) i;
          }
          //System.out.println(htmlCode);   //check2
        } catch (IOException e) {
          System.out.println("IOxception caught");
          //	e.printStackTrace();
        }
        Vector<String> row = new Vector<>();
        subTitle = getTitle(subHtmlCode).toUpperCase();
        row.add(subTitle);
        row.add(subH);
        if (subTitle.isEmpty()) {
          rowData.add(row);
          Queries.insertLink(subTitle, subH);
        }

      } else {
      }
    }
    return rowData;
  }

  public Vector<Vector> crawlStart(String startURL) throws MalformedURLException { 	//CRAWL METHODS HERE

    //____________TO GET THE HTML STRING_____________________________________
    try {
      //url = new URL("https://www.google.com");   /////check
      URL url = new URL(startURL);

      htmlCode = "";
      URLConnection urlcon = url.openConnection();

      //added user agent
      urlcon.addRequestProperty("User-Agent", "Chrome/62.0.3202.94"); //>>>>check if reequired or not

      InputStream stream = urlcon.getInputStream();
      int i;
      while ((i = stream.read()) != -1) {
        htmlCode = htmlCode + (char) i;
      }
      //System.out.println(htmlCode);   //check2
    } catch (IOException e) {
      return null;
      //	e.printStackTrace();
    }

    String title = getTitle(htmlCode);
    //SHOULD ADD <<  matcher.group(1)    >>  TO THE INDEX TABLE(TITLE)
    ArrayList<String> listOfCleanUrl = regexChecker(htmlCode);

    return titleFetcher(listOfCleanUrl);

  }

  public static ArrayList<String> regexChecker(String st2checkin) {
    ArrayList<String> urlList = new ArrayList<String>();
    String theHrefRegex = "href=\"([^\"]*)\"";
    Pattern checkHrefRegex = Pattern.compile(theHrefRegex);
    Matcher regexMatcher = checkHrefRegex.matcher(st2checkin);
    int count = 1;
    while (regexMatcher.find()) {
      System.out.println(count);

      if (regexMatcher.group().length() != 0) {
        count++;
        System.out.println(regexMatcher.group().trim());

        if (cleanedURL(regexMatcher.group().trim()) == null) {
          System.out.println("just go");
        } else {
          urlList.add(cleanedURL(regexMatcher.group().trim()));
        }

      }
      //	System.out.println("Starting Index:"+regexMatcher.start());
      //	System.out.println("End Index" + regexMatcher.end());
    }

    return urlList;
  }

  public static String cleanedURL(String rawurl) {
    int last;
    String url = rawurl;
    if (url.contains("http") || url.contains("https")) {
      if (url.contains("href=\"")) {
        url = url.replace("href=\"", "");
        if (url.contains("/url?q=")) {
          url = url.replace("/url?q=", "");
        }
        last = url.lastIndexOf("\"");
        //System.out.println(last);
        url = url.substring(0, last);
        return url;
      }
    }
    return null;
  }
}
