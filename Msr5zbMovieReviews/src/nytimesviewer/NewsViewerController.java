/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nytimesviewer;

import java.awt.Desktop;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dale/mike msr5zb 12358133
 * http://stackoverflow.com/questions/26227786/loading-urls-in-javafx-webview-is-crashing-the-jvm
 */
public class NewsViewerController implements Initializable {

    private Stage stage;
    
    private NYTNewsManager newsManager;
    ArrayList<NYTNewsStory> stories;
    
    @FXML
    private TextField searchTextField;
    
    @FXML
    private ListView newsListView;
   
    
    private String searchString = "Space";
    ObservableList<String> newsListItems;
    @FXML
    private Label movieTitle;
    @FXML
    private Label movieMpaaRating;
    @FXML
    private Label movieByline;
    @FXML
    private Label movieHeadline;
    @FXML
    private Label movieOpeningDate;
    @FXML
    private Label moviePublicationDate;
    @FXML
    private Label movieDateUpdated;
    @FXML
    private Button movieFullReview;
    @FXML
    private TextArea movieDescription;
    @FXML
    private ImageView movieImage;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    public void ready(Stage stage) {
        this.stage = stage;
        newsManager = new NYTNewsManager();
        
        newsListItems = FXCollections.observableArrayList();
        //Everytime a story is selected, run this protocol.
        newsListView.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
            // When the contents of the newsListView changes a situation can be created
            // where autoselection results in a new_val that is out of range of the stories.
            // The following makes sure the new_val is within the bounds of stories.
            if ((int)new_val < 0 || (int)new_val > (stories.size() - 1)) {
                return;
            }
            NYTNewsStory story = stories.get((int)new_val);
            if(!story.imageUrl.equals("")){
                movieImage.setImage(new Image(story.imageUrl));
            }
                
            movieTitle.setText("Title: " + story.displayTitle);
            movieMpaaRating.setText("MPAA Rating: " + story.mpaaRating);
            movieByline.setText("Byline: " + story.byline);
            movieHeadline.setText("Headline: " + story.headline);
            movieOpeningDate.setText("Opening Date: " + story.openingDate);
            moviePublicationDate.setText("Publication Date: " + story.publicationDate);
            movieDateUpdated.setText("Date Updated: " + story.dateUpdated);
            movieFullReview.setOnAction((ActionEvent event) -> {
                if(Desktop.isDesktopSupported()){
                    try {
                        Desktop.getDesktop().browse(new URI(story.webUrl));
                    } catch (IOException | URISyntaxException ex) {
                        Logger.getLogger(NewsViewerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
            });
            movieDescription.setText(story.summaryShort);
        });
        
        // put initial search string in searchTextField and load news based
        // on that search
        searchTextField.setText(searchString);
        movieDescription.setWrapText(true);
        loadNews();
    }
    
    private void loadNews() {
        try {
            newsManager.load(searchString);
        } catch(Exception ex) {
            displayExceptionAlert(ex);
            return;
        }
        
        stories = newsManager.getNewsStories();
        newsListItems.clear();
        
        for (NYTNewsStory story : stories) {
            newsListItems.add(story.headline);
        }
        //newsListView.getItems().clear();
        newsListView.setItems(newsListItems);
        if (stories.size() > 0) {
            newsListView.getSelectionModel().select(0);
            newsListView.getFocusModel().focus(0);
            newsListView.scrollTo(0);
        }
        // The above is used to tell the list view to select, focus on, and
        // scroll to the first item which will cause the listener to treat
        // this item as being selected.
        // Below is the way I could just tell the webView's webEngine to display
        // the first story.  ...which I don't need to do if the listener on
        // the list view is told the first item is selected.
        /*
        if (stories.size() > 0) {
            webEngine.load(stories.get(0).webUrl);
        }
        */
    }
    
    @FXML
    private void handleSearch(ActionEvent event) {
        if (searchTextField.getText().equals("")) {
            displayErrorAlert("The search field cannot be blank. Enter one or more search words.");
            return;
        }
        searchString = searchTextField.getText();
        loadNews();
    }
    
    @FXML
    private void handleUpdate(ActionEvent event) {
        loadNews();
    }
    
    @FXML
    private void handleAbout(ActionEvent event) {
        displayAboutAlert();
    }
    
    private void displayErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error!");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void displayExceptionAlert(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception");
        alert.setHeaderText("An Exception Occurred!");
        alert.setContentText("An exception occurred.  View the exception information below by clicking Show Details.");

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }
    
    private void displayAboutAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("New York Times Viewer");
        alert.setContentText("This application was developed by Dale Musser, extended by Mike Rallo, for CS4330 at the University of Missouri.");
        
        TextArea textArea = new TextArea("The New York Times API is used to obtain a news feed.  Developer information is available at http://developer.nytimes.com. ");
        textArea.appendText("Mike's api-key is used in this application.  If you develop your own applicatyion get your own api-key from the New York Times.");
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
            
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expContent);
        
        alert.showAndWait();
    }
    
}
