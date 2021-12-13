package cs1302.omega;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * My application, titled SettleScore, takes in a valid US zip code and
 * generates a "SettleScore" based on the crime rate and number of recreational
 * oppurtunities in the surrounding area. It uses multiple APIS and also will
 * display a map of the city closest to the zip code using geo coordinates.
 */
public class OmegaApp extends Application {

    Alert alert;

    /**
     * Constructs an {@code OmegaApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     * If you are reading this, i successuflly connected to github.
     */
    public OmegaApp() {}

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        // demonstrate how to load local asset using "file:resources/"
        // I will load my own custom app banner at the end of the project
        Image bannerImage = new Image("file:resources/readme-banner.png");
        ImageView banner = new ImageView(bannerImage);
        banner.setPreserveRatio(true);
        banner.setFitWidth(720);

        VBox pane = new VBox();
        Scene scene = new Scene(pane, 720, 720);
        stage.setMaxWidth(720);
        stage.setMaxHeight(720);
        stage.setTitle("SettleScore V1.00");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

        //this block of code will setup a menubar atop the program
        MenuBar menuBar = new MenuBar();
        Menu fTab = new Menu("File");
        Menu hTab = new Menu("Help");
        MenuItem exit = new MenuItem("Exit");
        MenuItem about = new MenuItem("About");
        fTab.getItems().add(exit);
        hTab.getItems().add(about);
        menuBar.getMenus().addAll(fTab, hTab);

        //this block of code will setup the about section
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Tahsin Nabi");
        ButtonType close = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
        alert.setContentText("Tahsin Nabi, tjn92948@uga.edu, Version 1.00");
        Image aboutImg = new Image("https://scontent-atl3-1.xx.fbcdn.net/v/t1.6435-9/72687449_2519325251446909_5658522496542965760_n.jpg?_nc_cat=107&ccb=1-5&_nc_sid=09cbfe&_nc_ohc=KDvpmWbpk8cAX9msVcH&_nc_ht=scontent-atl3-1.xx&oh=97dfbd9aa31c78381893b05a8800f1bb&oe=61C0C9F7");
        ImageView imgView = new ImageView(aboutImg);
        imgView.setPreserveRatio(true);
        imgView.setFitHeight(300);
        alert.setGraphic(imgView);
        alert.setResizable(true);
        //alert.showAndWait();

        //when about is pressed, pop an alert
        about.setOnAction(this::aboutAlert);
        //awhen file is pressed, exit the program
        exit.setOnAction(this::exitApp);

        VBox mBar = new VBox(menuBar); //place menu bar into vbox
        VBox sTab = new SettleScore();

        pane.getChildren().addAll(mBar,banner, sTab); //populate the pane

    } // start


    /**
     * This method will pop up an about me alert.
     *
     * @param e this actionevent will allow for the method to connect to a physical menuitem
     **/
    public void aboutAlert(ActionEvent e) {
        alert.showAndWait();
    }

    /**
     * This method will forcefully close the application.
     *
     * @param e this actionevent will allow for the method to be connected to a physical menuitem
     **/
    public void exitApp(ActionEvent e) {
        System.exit(0);
    }

} // OmegaApp
