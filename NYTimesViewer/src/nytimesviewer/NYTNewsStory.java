/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nytimesviewer;

/**
 *
 * @author mike
 */
public class NYTNewsStory {
    public String displayTitle;
    public String mpaaRating;
    public String byline;
    public String headline;
    public String openingDate;
    public String publicationDate;
    public String dateUpdated;
    public String webUrl;
    public String summaryShort;
    public String imageUrl;
    
    public NYTNewsStory(String displayTitle, String mpaaRating, String byline, String headline, String openingDate,
            String publicationDate, String dateUpdated, String webUrl, String summaryShort, String imageUrl) {
        this.displayTitle = displayTitle;
        this.mpaaRating = mpaaRating;
        this.byline = byline;
        this.headline = headline;
        this.openingDate = openingDate;
        this.publicationDate = publicationDate;
        this.dateUpdated = dateUpdated;
        this.webUrl = webUrl;
        this.summaryShort = summaryShort;
        this.imageUrl = imageUrl;
        
    }
    
    
}
