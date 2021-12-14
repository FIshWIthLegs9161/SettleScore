package cs1302.omega;

import cs1302.omega.SettleScoreUtil;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * This class represents my omega application itself, excluding banners
 * and my options bar. Settle score's description is within omegaApp.
 **/
public class SettleScore extends VBox {

    //these nodes represent the hotbar of the application
    HBox hotBox;
    Label searchTxt;
    TextField searchBar;
    Button generateBtn;

    //these nodes represent the content of the application
    HBox content;
    ImageView mapView;
    Text report;

    //these nodes represent the progress bar
    HBox loadBox;
    Text loadMsg;
    ProgressBar loadBar;

    /**
     * This is the constructor of SettleScore which sets all the javafx components.
     **/
    public SettleScore() {
        hotBox = new HBox();
        searchTxt = new Label("Enter a valid US Zipcode: ");
        searchBar = new TextField();
        generateBtn = new Button("Generate");
        hotBox.getChildren().addAll(searchTxt,searchBar,generateBtn);
        hotBox.setAlignment(Pos.CENTER);
        hotBox.setSpacing(10);
        HBox.setHgrow(searchBar, Priority.ALWAYS); //make textfield dynamicly change size
        generateBtn.setOnAction(this::generateUpdate); //give an action to button
        hotBox.setPadding(new Insets(5,10,5,10));

        content = new HBox();
        mapView = new ImageView(new Image("https://sienaconstruction.com/wp-content/uploads/2017/05/test-image.jpg"));
        report = new Text("This is your settle score report, you got a TEST!");
        content.getChildren().addAll(mapView,report);
        content.setSpacing(10);
        content.setPadding(new Insets(5,0,5,0));
        mapView.setFitWidth(400);
        mapView.setFitHeight(400);

        loadBox = new HBox();
        loadMsg = new Text("Data from US Census & HERE.com");
        loadBar = new ProgressBar(1);
        loadBox.getChildren().addAll(loadMsg,loadBar);
        loadBox.setSpacing(10);
        loadBox.setPadding(new Insets(5,10,5,10));

        this.getChildren().addAll(hotBox,content,loadBox);

    }

    /**
     * This method ties my programs single button to its function of generating a report on an area.
     * @param e
     */
    private void generateUpdate(ActionEvent e) {
        Runnable threadUpdate = () -> { //create a runnable thread
            try {
                generate(); //update urls
            } catch (Exception x) { //catch statement will trigger alert
                Platform.runLater(() -> SettleScoreUtil.getAlert(x).showAndWait());
            }
        };

        runThread(threadUpdate); //run previous thread
    }

    /**
     * Dr Barnes method from the 4th project documentation.
     *
     * @param progress this final double represents the percent of loading complete in the bar
     **/
    private void setProgress(final double progress) {
        Platform.runLater(() -> loadBar.setProgress(progress));
    } // setProgress

    /**
     *  This method will generate all the proper content for the settle score app.
     *  It should be called whenever the generate button is pressed, making sure to
     *  update the progress bar periodically.
     *
     */
    public void generate() throws Exception {
        String report = ""; //the report we will place
        setProgress(0); //at first progress will be 0
        loadMsg.setText("Loading..."); //make sure to periodically update the load message

        int zip = 0; //get zipcode from textfield
        String searchContents = searchBar.getText();
        //search contents should be formatted as "athens, ga" with ONE SPACE BEFORE THE STATE and a
        //comma FOLLOWING THE CITY
        String city = searchContents.substring(0,searchContents.indexOf(","));
        String cityCp = city; //store name of city with normal spaces
        String state = searchContents.substring(searchContents.indexOf(' ') + 1);

        setProgress(.25);
        //if the city name has any spaces in it ie:los angeles, new york etc, fil the space
        // with "%20" strings
        city = city.replaceAll(" ", "%20");
        System.err.println(city);

        //USING HELPER METHODS, get image, and craft a report of the zipcode
        SettleScoreUtil.getRecScore(state,city);
        setProgress(.5);
        mapView.setImage(SettleScoreUtil.getMapImage(state,city));
        setProgress(.75);
        System.err.println("generate was clicked on a new thread");
        double test = SettleScoreUtil.getCrimeScore(state);
        System.err.println("thrcrime score query was successful");

        setProgress(1); //set progress to 1
        loadMsg.setText("Crime data from the 2020 US Census"); //reset dynamic text

    }

    /**
     * This method is used as a helper method to run any threads which are defined by
     * a lambda expression which extends runnable.
     *
     * @param x tis refers to some class which extends runnable and is therefore- runnable lol
     **/
    public static void runThread(Runnable x) {
        Thread t = new Thread(x);
        t.setDaemon(false);
        t.start();
    }

}
