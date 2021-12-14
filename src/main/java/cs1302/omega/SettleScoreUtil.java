package cs1302.omega;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import java.io.IOException;
import cs1302.api.Tools;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This is a utility class of SettleScore which does most of its main functions.
 * Main functions which are not related to javafx that is.
 **/
public class SettleScoreUtil {

    /**
     * The constructor of the utility class, I wont need an instance of it so
     * therefore it shoudl be empty.
     **/
    public SettleScoreUtil() {
    }

    /**
     * This method will return an image of the user specified location. It does this
     * by calling multiple APIs. Firstly, uses city and state to find geographical coordinates
     * , after which it uses the coordinates to find a map of the city and size appropriately.
     *
     * API URL: https://image.maps.ls.hereapi.com/mia/1.6/mapview?apiKey
     * =gy619M0V4cf7rDPwQhHxC93OFiDosU17jDgdLd89DWs&c=33.95,-83.375&u=5k&h=500&w=500
     * API URL INFO FROM ZIPCODE: http://ZiptasticAPI.com/30062
     * API  URL TURNING GETTING COORDINATES: https://api.geoapify.com/v1/geocode/
     * search?text=38%20Upper%20Montagu%20Street%2C%20Westminster%20W1H%201LJ%2
     * C%20United%20Kingdom&apiKey=7c56182fbbf1470faca613b0fe5262fe.
     *
     * @throws IOException this method will throw an IOE if the mapKey expires, each
     * generated map key has a limited number of uses
     *
     * @param state the state, in its INITIALs, to be searcched for
     * @param city the name of the city to be searched for
     * @return Image this is an image of a map of whatever city is specified
     */
    public static Image getMapImage(String state, String city) throws IOException {

        URL coorUrl = new URL("https://api.geoapify.com/v1/geocode/search?city="
            + city + "&state=" + state +
            "&country=US&format=json&apiKey=7c56182fbbf1470faca613b0fe5262fe");
        String jsonText = getUrlText(coorUrl);

        //get the longitude and latitude
        String lon = getCoorElement("lon",jsonText);
        String lat = getCoorElement("lat",jsonText);
        System.err.println("Long: " + lon + "   Lati: " + lat);

        //if an IO exception is thrown it is MOST LIKELY that this key exired
        //IF THE IO EXCEPTION IS STILL HAPPENING CHANGE THE STRING "mapAPIKey"
        // to "eA0zEAwF32u9QbF7IMhtUmXoXDmBGH91aML7qdRxZ-s" which is not being used
        String mapAPIKey = "SHvfBo0eh51rU8ScryoU9p_bTsqQLOJpXRNeqqkjmyM";
        String mapUrl = "https://image.maps.ls.hereapi.com/mia/1.6/mapview?" +
            "apiKey=" + mapAPIKey + "&c=" +
            lat + "," + lon + "&u=5k&h=500&w=500";

        System.err.println(mapUrl);
        return new Image(mapUrl);
    }

    /**
     * This method calculates the settle score. The settle score values crime and safety
     * over how much there is to see/do in a city.
     *
     * @param x crime score
     * @param y count of things to do
     * @return double the settle score itself
     **/
    public static double getSettleScore(double x, double y) throws IOException {
        return ((y / 10) - x * 3) * 20;
    }

    /**
     * This method will take a state initial and return a number representing the crime rate.
     * The higher the number, the more dangerous a state is. This method is an oversimplification
     * of crime in the US and searches by state using the US Census API.
     * Calculated from (total crimes/ population) / (us average) ) * 5.
     *
     * @throws IOException if the API KEY expires
     * @param state the state INITIALS which are being searched for crime data
     * @return double an over simplified  numerical representation of the crime rate
     */
    public static double getCrimeScore(String state) throws IOException {
        //String currentYear = Tools.get((Tools.getJson
        //("http://worldclockapi.com/api/json/est/now")),
        //"ordinalDate", 0).getAsString().substring(0,4);
        //for now assume previous year is 2020, in the
        //future you can make it variable to the current year

        //variables which we will need
        double crimeScore = 0;
        double totalCrimes = 0;
        int population = 0;
        double avgCrimeRate = .04743; //ballpark number from 2020

        URL censusUrl = new URL("https://api.usa.gov/crime/fbi/sapi/api/estimates/states/"
            + state + "/2020/2021?API_KEY=KuoKPSs0imhj6UEU8aSObTvUXj05KqZu67cltA76");
        String jsonText = getUrlText(censusUrl);

        //get all the elements to be added to the total crime count
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
        //calculate crime rate and scale up by 5

        System.err.println(crimeScore);
        crimeScore = Math.round(crimeScore);
        return crimeScore;
    }

    /**
     * This method will take in a zip code, find relevant geo coordinates and return a score
     * of how many recreational facilities there are nearby. Recreational facilities include
     * everything from bars, to mueseums, to parks, to malls etc.

     * APIKey:"https://api.opentripmap.com/0.1/en/places/radius?radius=8000&lon="
     *   + lon + "&lat=" + lat + "&rate=1&format=count&limit=300&apikey="
     *   + "5ae2e3f221c38a28845f05b6ff66ab9c026d1daa06371b7280ad7fa9");.
     *
     * @param state
     * @param city
     * @return int a count of how many recreational points of interest are nearby
     */
    public static int getRecScore(String state, String city) throws IOException {
        URL coorUrl = new URL("https://api.geoapify.com/v1/geocode/search?city="
            + city + "&state=" + state +
            "&country=US&format=json&apiKey=7c56182fbbf1470faca613b0fe5262fe");
        String jsonText = getUrlText(coorUrl);

        //get longitude and latitude
        String lon = getCoorElement("lon",jsonText);
        String lat = getCoorElement("lat",jsonText);
        System.err.println("Long: " + lon + "   Lati: " + lat);

        URL recUrl = new URL("https://api.opentripmap.com/0.1/en/places/radius?radius=8000&lon="
            + lon + "&lat=" + lat + "&rate=1&format=count&limit=300&apikey="
            + "5ae2e3f221c38a28845f05b6ff66ab9c026d1daa06371b7280ad7fa9");
        String recText = getUrlText(recUrl);
        System.err.println(recText);
        //make a substring looking for the count element
        recText = recText.substring(recText.indexOf(":") + 1, recText.indexOf('}'));
        //System.err.println(recText);
        int recCount = Integer.parseInt(recText);
        System.err.println(recCount);
        return recCount;
    }

    /**
     * Given an exception, this helper method will automatically create a
     * properly formatted alert to be thrown.
     * Called like this in the catch block : Platform.runLater(() -> getAlert(e).showAndWait());.
     * @param e some exception encountered
     * @return Alert the formatted alert window
     */
    public static Alert getAlert(Exception e) {
        Alert rtn = new Alert(Alert.AlertType.WARNING);
        rtn.setTitle("An Exception Occurred");
        rtn.setContentText(e.getMessage() + ": see API for more information" +
            ", if the error message is a link then your API key is expired");
        //flexible way of dealing with any exception
        rtn.setResizable(true);

        return rtn;
    }


    /**
     * Given an IOB exception, this helper method will automatically create a
     * properly formatted alert to be thrown.
     * Called like this in the catch block : Platform.runLater(() -> getAlert(e).showAndWait());.
     * @param e some exception encountered
     * @return Alert the formatted alert window
     */
    public static Alert getInputAlert(Exception e) {
        Alert rtn = new Alert(Alert.AlertType.WARNING);
        rtn.setTitle("Invalid Input");
        rtn.setContentText("The Query was invalid, try: " +
            "cityName, stateInitials");
        rtn.setResizable(true);
        //specific to errors with the search bar

        return rtn;
    }

    /**
     * I AM NOT THE ORIGINAL AUTHOR OF THIS CODE, it was heavily influenced from another user
     * online. I modified it slightly, but all credit goes to the OP.
     * Link:https://examples.javacodegeeks.com/core-java/net/url/read-text-from-url/
     *
     * @param url a url pointing to the address of the JSON array
     * @return String representation of the JSON array
     */
    public static String getUrlText(URL url) {
        try {
            String rtn = "";
            BufferedReader input = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = input.readLine()) != null) {
                rtn += line;
            }
            input.close();
            return rtn;
        } catch (MalformedURLException e) {
            System.out.println("MUE: " + e.getMessage());

        } catch (IOException e) {
            System.out.println("IOE: " + e.getMessage());
        }
        return null; //return null if geturltext fails
    }

    /**
     * This method will traverse a JSON file which has been turned into a string
     * in order to pull out the value of a specific element. Although it is inefficient
     * I ran into several issues using gson.
     *
     * @param param the parameter being searched for
     * @param json a text representation of a JSON Array
     * @return String a text representation of a JSON element
     */
    public static String getElement(String param, String json) {
        String removeFront = json.substring(json.indexOf(param) + param.length() + 4);
        //System.out.println(removeFront);
        return removeFront.substring(0,removeFront.indexOf(','));
    }

     /**
     * This method will traverse a JSON file which has been turned into a string
     * in order to pull out the value of a specific element. Although it is inefficient,
     * I ran into several issues using gson. This one is specific for geo coords.
     *
     * @param param the parameter being searched for (longitude or latitude)
     * @param json a text representation of Json array
     * @return String a text representation of Json element
     */
    public static String getCoorElement(String param, String json) {
        String removeFront = json.substring(json.indexOf(param) + param.length() + 2);
        //System.out.println(removeFront);
        return removeFront.substring(0,removeFront.indexOf(','));
    }


    /**
     * This method returns a string formatted report of some demographical information.
     * @param crimeScore the crimeScore
     * @param recCount the reccount
     * @param counts other information to include
     * @param x the search query run
     * @return String string report of the locaiton
     **/
    public static String getReport(double crimeScore, int recCount, int[] counts, String x)
        throws IOException {
        double settleScore = getSettleScore(crimeScore, recCount);
        String rtn = "This is your SettleScore! report for " + x + ".\n" + "\n";
        rtn += "The overall crime score for this state was " + crimeScore + ".\n" +
            "Here is a breakdown of some important numbers" + ".\n";

        rtn += "The population of the state in 2020 was approximately " + counts[0] + ".\n";
        rtn += "The violent crime count was approximately " + counts[1] + ".\n";
        rtn += "The robbery count was approximately " + counts[2] + ".\n";
        rtn += "The burglary count was approximately " + counts[3] + ".\n";
        rtn += "The property theft count was approximately " + counts[4] + ".\n";

        rtn += "There are " + recCount + " must see locations close by in this city." + "\n" + "\n";
        rtn += "The final SettleScore! taking all this and more into account is: "  + settleScore;

        return rtn;
    }

    /**
     * This helper method will fill an array with whatever information I want to be in
     * my report which gets returned.
     *
     * @param state the state INITIAls to be searched
     * @return int[] int array filled wiht the counts i need
     **/
    public static int[] getCounts(String state) throws MalformedURLException {

        URL censusUrl = new URL("https://api.usa.gov/crime/fbi/sapi/api/estimates/states/"
            + state + "/2020/2021?API_KEY=KuoKPSs0imhj6UEU8aSObTvUXj05KqZu67cltA76");
        String jsonText = getUrlText(censusUrl);

        int[] rtn = new int[5];
        rtn[0] = Integer.parseInt(getElement("population",jsonText));

        rtn[1] = Integer.parseInt(getElement("violent_crime",jsonText));
        //totalCrimes += Integer.parseInt(getElement("homicide",jsonText));
        rtn[2] = Integer.parseInt(getElement("robbery",jsonText));
        //totalCrimes += Integer.parseInt(getElement("aggravated_assault",jsonText));
        rtn[4] = Integer.parseInt(getElement("property_crime",jsonText));
        rtn[3] = Integer.parseInt(getElement("burglary",jsonText));
        //totalCrimes += Integer.parseInt(getElement("larceny",jsonText));
        //totalCrimes += Integer.parseInt(getElement("motor_vehicle_theft",jsonText));

        return rtn;

    }

}
