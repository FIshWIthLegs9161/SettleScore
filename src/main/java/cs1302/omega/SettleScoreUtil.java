package cs1302.omega;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import java.io.IOException;
import cs1302.api.Tools;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

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
    public static Image getMapImage(String state, String city) throws IOException {

        URL coorUrl = new URL("https://api.geoapify.com/v1/geocode/search?city="
        + city +"&state=" + state +
        "&country=US&format=json&apiKey=7c56182fbbf1470faca613b0fe5262fe");
        String jsonText = getUrlText(coorUrl);

        //coorUrl = "https://api.geoapify.com/v1/geocode/search?city=" + city + "&state="
        //+ state + "&country=US&format=json&apiKey=7c56182fbbf1470faca613b0fe5262fe";


        //URL mapUrl = new URL("https://image.maps.ls.hereapi.com/mia/1.6/mapview" +
        //"?apiKey=gy619M0V4cf7rDPwQhHxC93OFiDosU17jDgdLd89DWs&c=" + lon +
        //"," + lat + "&u=5k&h=500&w=500");
        //String jsonText = getUrlText(coorUrl);

        String lon = getCoorElement("lon",jsonText);
        String lat = getCoorElement("lat",jsonText);
        System.err.println("Long: " + lon + "   Lati: " + lat);

        String mapUrl = "https://image.maps.ls.hereapi.com/mia/1.6/mapview?" +
            "apiKey=gy619M0V4cf7rDPwQhHxC93OFiDosU17jDgdLd89DWs&c=" +
            lon + "," + lat + "&u=5k&h=500&w=500";


        return new Image(mapUrl);
        }

        public static double getSettleScore(double x, double y) throws IOException {
        return 1.0;
        }

    /**
     * This method will take a zip code and return a number 1-10 representing the crime rate.
     * Calculated from ((population/ total crimes) / (us average) ) * 5.
     *
     * @param zip the zip code of a location of interest
     * @return
     */
    public static double getCrimeScore(String state) throws IOException {
        //String currentYear = Tools.get((Tools.getJson
        //("http://worldclockapi.com/api/json/est/now")),
        //"ordinalDate", 0).getAsString().substring(0,4);
        //for now assume previous year is 2020, in the
        //future you can make it variable to the current year

        double crimeScore = 0;
        double totalCrimes = 0;
        int population = 0;
        double avgCrimeRate = .04743; //ballpark number from 2020

        //String zipUrl = "http://ziptasticapi.com/" + zip;
        //String state = Tools.get((Tools.getJson(zipUrl)), "state", 0).getAsString();

        URL censusUrl = new URL("https://api.usa.gov/crime/fbi/sapi/api/estimates/states/"
        + state + "/2020/2021?API_KEY=KuoKPSs0imhj6UEU8aSObTvUXj05KqZu67cltA76");
        String jsonText = getUrlText(censusUrl);

        population += Integer.parseInt(getElement("population",jsonText));
        //System.err.println(population);
        totalCrimes += Integer.parseInt(getElement("violent_crime",jsonText));
        totalCrimes += Integer.parseInt(getElement("homicide",jsonText));
        totalCrimes += Integer.parseInt(getElement("robbery",jsonText));
        totalCrimes += Integer.parseInt(getElement("aggravated_assault",jsonText));
        totalCrimes += Integer.parseInt(getElement("property_crime",jsonText));
        totalCrimes += Integer.parseInt(getElement("burglary",jsonText));
        totalCrimes += Integer.parseInt(getElement("larceny",jsonText));
        totalCrimes += Integer.parseInt(getElement("motor_vehicle_theft",jsonText));
        //totalCrimes += Integer.parseInt(getElement("arson",jsonText));

        System.err.println(totalCrimes);
        System.err.println(population);
        crimeScore = 5 * ((totalCrimes / population) / avgCrimeRate);

        System.err.println(crimeScore);
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

    /**
     * I got modified code from another stack user. It takes the text from a url.
     * @param url
     * @return
     */
    public static String getUrlText(URL url) {
        try
        {
            String rtn = "";
            BufferedReader input = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = input.readLine()) != null) {
                rtn += line;
            }
            input.close();
            return rtn;
        } catch (MalformedURLException e) {
            System.out.println("MUE" + e.getMessage());

        } catch (IOException e) {
            System.out.println("IOE" + e.getMessage());
        }
        return null; //return null if geturltext fails
    }

    /**
     * This method will traverse a JSON file which has been turned into a string
     * in order to pull out the value of a specific element. Although it is inefficient
     * I ran into several issues using gson.
     * @param param
     * @param json
     * @return
     */
    public static String getElement(String param, String json) {
        String removeFront = json.substring(json.indexOf(param) + param.length() + 4);
        //System.out.println(removeFront);
        return removeFront.substring(0,removeFront.indexOf(','));
    }

     /**
     * This method will traverse a JSON file which has been turned into a string
     * in order to pull out the value of a specific element. Although it is inefficient
     * I ran into several issues using gson. This one is specific for geo coords.
     * @param param
     * @param json
     * @return
     */
    public static String getCoorElement(String param, String json) {
        String removeFront = json.substring(json.indexOf(param) + param.length() + 2);
        //System.out.println(removeFront);
        return removeFront.substring(0,removeFront.indexOf(','));
    }


}
