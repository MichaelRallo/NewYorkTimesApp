/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nytimesviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.simple.*;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author dale/mike msr5zb 12358133
 * https://docs.oracle.com/javase/tutorial/networking/urls/creatingUrls.html
 */
public class NYTNewsManager {
    private String urlString = "";
    
    // sample url:
    //private String urlString = "http://api.nytimes.com/svc/search/v2/articlesearch.json?q=Microsoft&api-key=2bf424fd20964fc0bfad8011786cdcad";
   
    // NOTE!!  The api key below is Dale Musser's api key.  If you build an app that uses the New York Times API
    // get your own api key!!!!!  Get it from: http://developer.nytimes.com
    // I also cannot guarantee that the api key provided will be valid in the future.
    private final String baseUrlString = "http://api.nytimes.com/svc/movies/v2/reviews/search.json";
    
    private final String apiKey = "810a7f74e1e244d2a67a7c5ab7d5e7ca";
    private String searchString = "Space";
    
    private URL url;
    private ArrayList<NYTNewsStory> newsStories;
    
    
    public NYTNewsManager() {
        newsStories = new ArrayList<>();
    }
    
    public void load(String searchString) throws Exception {
        if (searchString == null || searchString.equals("")) {
            throw new Exception("The search string was empty.");
        }
        
        this.searchString = searchString;
        
        // create the urlString
        String encodedSearchString;
        try {
            // searchString must be URL encoded to put in URL
            encodedSearchString = URLEncoder.encode(searchString, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw ex;
        }
        
        urlString = baseUrlString + "?query=" + encodedSearchString + "&api-key=" + apiKey;
        System.out.println("URL SEARCH: " + urlString);
        try {
            url = new URL(urlString);
        } catch(MalformedURLException muex) {
           throw muex;
        }
        
        String jsonString = "";
        try {
            BufferedReader in = new BufferedReader(
            new InputStreamReader(url.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
                jsonString += inputLine;
            in.close();
        } catch (IOException ioex) {
            throw ioex;
        }
        
        try {
            parseJsonNewsFeed(jsonString);
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private void parseJsonNewsFeed(String jsonString) throws Exception {
        
        // start with clean list
        newsStories.clear();
        
        if (jsonString == null || jsonString.equals("")) return;
        
        JSONObject jsonObj;
        try {
            jsonObj = (JSONObject)JSONValue.parse(jsonString);
        } catch (Exception ex) {
            throw ex;
        }
        
        if (jsonObj == null) return;
        
        String status = "";
        try {
            status = (String)jsonObj.get("status");
        } catch (Exception ex) {
            throw ex;
        }
        
        if (status == null || !status.equals("OK")) {
            throw new Exception("Status returned from API was not OK.");
        }
        
//        JSONObject response;
//        try {
//            response = (JSONObject)jsonObj.get("response");
//        } catch (Exception ex) {
//            throw ex;
//        }
        
        JSONArray docs;
        try {
            docs = (JSONArray)jsonObj.get("results");
        } catch (Exception ex) {
            throw ex;
        }
      
        for (Object doc : docs) {
            try {
                JSONObject story = (JSONObject)doc;
                
                StringEscapeUtils.unescapeHtml4("hello");
                String headline = StringEscapeUtils.unescapeHtml4((String)story.getOrDefault("headline", ""));
                String displayTitle = StringEscapeUtils.unescapeHtml4((String)story.getOrDefault("display_title", ""));
                String mpaaRating = StringEscapeUtils.unescapeHtml4((String)story.getOrDefault("mpaa_rating", ""));
                if(mpaaRating == null || mpaaRating.equals("")){mpaaRating = "No Rating Available";}
                String byline = StringEscapeUtils.unescapeHtml4((String)story.getOrDefault("byline", ""));
                if(byline == null){byline = "N/A";}
                String openingDate = StringEscapeUtils.unescapeHtml4((String)story.getOrDefault("opening_date", ""));
                if(openingDate == null){openingDate = "N/A";}
                String publicationDate = StringEscapeUtils.unescapeHtml4((String)story.getOrDefault("publication_date", ""));
                if(publicationDate == null){publicationDate = "N/A";}
                String dateUpdated = StringEscapeUtils.unescapeHtml4((String)story.getOrDefault("date_updated", ""));
                if(dateUpdated == null){dateUpdated = "N/A";}
                String summaryShort = StringEscapeUtils.unescapeHtml4((String)story.getOrDefault("summary_short", ""));
                if(summaryShort == null){summaryShort = "Summary Not Available.";}
                
                
            
                JSONObject webUrlObj = (JSONObject)story.getOrDefault("link", null);
                String webUrl = "";
                if (webUrlObj != null) {
                    webUrl = StringEscapeUtils.unescapeHtml4((String)webUrlObj.getOrDefault("url", ""));
                }
                
                JSONObject mediaObj = (JSONObject)story.getOrDefault("multimedia", null);
                String imageUrl = "notAvailable.jpg";
                if (mediaObj != null) {
                    imageUrl = StringEscapeUtils.unescapeHtml4((String)mediaObj.getOrDefault("src", ""));
                }
                
                System.out.println("Title: " + displayTitle + "\n");
                System.out.println("MPAA Rating: " + mpaaRating + "\n");
                System.out.println("Byline: " + byline + "\n");
                System.out.println("Headline: " + headline + "\n");
                System.out.println("Opening Date: " + openingDate + "\n");
                System.out.println("Publication Date: " + publicationDate + "\n");
                System.out.println("Date Updated: " + dateUpdated + "\n");
                System.out.println("WebUrl: " + webUrl + "\n");
                System.out.println("Summary: " + summaryShort + "\n");
                System.out.println("------------------------------------------------------\n");
                
                NYTNewsStory newsStory = new NYTNewsStory(displayTitle, mpaaRating, byline, headline, openingDate, publicationDate, dateUpdated, webUrl, summaryShort, imageUrl);
                newsStories.add(newsStory);
                
            } catch (Exception ex) {
                throw ex;
            }
            
        }
        
    }
    
    public ArrayList<NYTNewsStory> getNewsStories() {
        return newsStories;
    }
    
    public int getNumNewsStories() {        
        return newsStories.size();
    }
    
}
