package cs1302.omega;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import java.io.IOException;
import cs1302.api.Tools;

public class SettleScoreUtil {

    public SettleScoreUtil() {
    }

    /**
     * This method will return an image of the user specified location.
     * API URL: https://image.maps.ls.hereapi.com/mia/1.6/mapview?apiKey=gy619M0V4cf7rDPwQhHxC93OFiDosU17jDgdLd89DWs&c=33.95,-83.375&u=5k&h=500&w=500
     * API URL INFO FROM ZIPCODE: http://ZiptasticAPI.com/30062
     * API  URL TURNING GETTING COORDINATES: https://api.geoapify.com/v1/geocode/search?text=38%20Upper%20Montagu%20Street%2C%20Westminster%20W1H%201LJ%2C%20United%20Kingdom&apiKey=7c56182fbbf1470faca613b0fe5262fe
     * @return
     */
    public static Image getMapImage(int zip) throws IOException {
        String zipUrl = "http://ziptasticapi.com/" + zip;
        String mapUrl = ""; //url which will be extracted from the
        String coorUrl = "";

        String city = Tools.get((Tools.getJson(zipUrl,"GET")), "city", 0).getAsString();
        System.err.println("made an elemetn");
        String state = Tools.get((Tools.getJson(zipUrl)), "state", 0).getAsString();

        coorUrl = "https://api.geoapify.com/v1/geocode/search?city=" + city + "&state=" + state + "&country=US&format=json&apiKey=7c56182fbbf1470faca613b0fe5262fe";
        String lon = Tools.get((Tools.getJson(coorUrl)), "lon", 0).getAsString();
        String lat = Tools.get((Tools.getJson(coorUrl)), "lat", 0).getAsString();

        mapUrl = "https://image.maps.ls.hereapi.com/mia/1.6/mapview?apiKey=gy619M0V4cf7rDPwQhHxC93OFiDosU17jDgdLd89DWs&c=" + lon + "," + lat + "&u=5k&h=500&w=500";


        return new Image(mapUrl);
        }

        public static double getSettleScore(int x, int y) throws IOException {
        return getCrimeScore(x) - getRecScore(y);
        }

    /**
     * This method will take a zip code and return a number 1-10 representing the crime rate.
     * Calculated from ((population/ total crimes) / (us average) ) * 5.
     *
     * @param zip the zip code of a location of interest
     * @return
     */
    public static double getCrimeScore(int zip) throws IOException {
        //String currentYear = Tools.get((Tools.getJson("http://worldclockapi.com/api/json/est/now")), "ordinalDate", 0).getAsString().substring(0,4);
        //for now assume previous year is 2020, in the future you can make it variable to the current year
        double crimeScore = 0;
        int totalCrimes = 0;
        int population = 0;
        double avgCrimeRate = .04743; //ballpark number from 2020

        String zipUrl = "http://ziptasticapi.com/" + zip;
        String state = Tools.get((Tools.getJson(zipUrl)), "state", 0).getAsString();

        String censusUrl = "https://api.usa.gov/crime/fbi/sapi/api/estimates/states/" + state + "/2020/2021?API_KEY=KuoKPSs0imhj6UEU8aSObTvUXj05KqZu67cltA76";

        population += Tools.get((Tools.getJson(censusUrl)), "population", 0).getAsInt();

        totalCrimes += Tools.get((Tools.getJson(censusUrl)), "violent_crime", 0).getAsInt();
        totalCrimes += Tools.get((Tools.getJson(censusUrl)), "homicide", 0).getAsInt();
        totalCrimes += Tools.get((Tools.getJson(censusUrl)), "robbery", 0).getAsInt();
        totalCrimes += Tools.get((Tools.getJson(censusUrl)), "aggravated_assault", 0).getAsInt();
        totalCrimes += Tools.get((Tools.getJson(censusUrl)), "property_crime", 0).getAsInt();
        totalCrimes += Tools.get((Tools.getJson(censusUrl)), "burglary", 0).getAsInt();
        totalCrimes += Tools.get((Tools.getJson(censusUrl)), "larceny", 0).getAsInt();
        totalCrimes += Tools.get((Tools.getJson(censusUrl)), "motor_vehicle_theft", 0).getAsInt();
        totalCrimes += Tools.get((Tools.getJson(censusUrl)), "arson", 0).getAsInt();

        crimeScore = 5 * ((totalCrimes / population) / avgCrimeRate);

        return crimeScore;
    }

    /**
     * This method will take in a zip code, find relevant geo coordinates and return a score
     * of how many recreational facilities there are nearby.
     * @param zip
     * @return
     */
    public static int getRecScore(int zip) throws IOException{
        return 1;
    }

    /**
     * Given an exception, this helper method will automatically create a properly formatted alert to be thrown.
     * call like this in the catch block : Platform.runLater(() -> getAlert(e).showAndWait());.
     * @param e
     * @return
     */
    public static Alert getAlert(Exception e) {
        Alert rtn = new Alert(Alert.AlertType.WARNING);
        rtn.setTitle("An Exception Occurred");
        rtn.setContentText(e.getMessage() + ": see API for more information");
        rtn.setResizable(true);

        return rtn;
    }


}
