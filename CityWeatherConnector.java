import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Handles the communication with the API:s
 */

public class CityWeatherConnector {

    private JSONObject jsonCurrent;
    private JSONObject jsonPollution;
    private String city;

    /**
     *
     * @param city the city to get information about
     * @throws IOException if the url is invalid (generally if city isn't a city)
     */
    public CityWeatherConnector(String city) throws IOException{
        JSONObject currentWeatherJson = openConnection("https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=e4445cd5da34d87c13dfa6073eb88e66");
        this.jsonCurrent = currentWeatherJson;
        JSONObject currentPollutionJson =openConnection("http://api.openweathermap.org/data/2.5/air_pollution?lat="+getLat()+"&lon="+getLon()+"&appid=e4445cd5da34d87c13dfa6073eb88e66");
        this.jsonPollution = currentPollutionJson;
        this.city = city;
    }

    /**
     * the following methods retrieve and return different pieces of information from the API.
     */
    public String getWeatherDescription() {
        JSONArray weatherArray = jsonCurrent.getJSONArray("weather");
        JSONObject weatherObject = (JSONObject) weatherArray.get(0);
        return weatherObject.getString("description");
    }

    public String getTempKelvin() {
        JSONObject mainObject = jsonCurrent.getJSONObject("main");
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        return numberFormat.format(mainObject.getDouble("temp"));
    }

    public String getTempCelsius() {
        JSONObject mainObject = jsonCurrent.getJSONObject("main");
        double kelvinDouble = mainObject.getDouble("temp");
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        return numberFormat.format(kelvinDouble-273.15);
    }

    public int getHumidity(){
        JSONObject mainObject = jsonCurrent.getJSONObject("main");
        return mainObject.getInt("humidity");
    }

    public String getFeelsLike(){
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        JSONObject mainObject = jsonCurrent.getJSONObject("main");
        double feelsK = mainObject.getDouble("feels_like");
        return numberFormat.format(feelsK-273.15);
    }

    public int getPressure(){
        JSONObject mainObject = jsonCurrent.getJSONObject("main");
        return mainObject.getInt("pressure");
    }

    public String getCountry(){
        Locale l = new Locale(Locale.UK.getLanguage(),jsonCurrent.getJSONObject("sys").getString("country"));
        return l.getDisplayCountry(Locale.UK);
    }

    public double getLat(){
        return jsonCurrent.getJSONObject("coord").getDouble("lat");
    }

    public double getLon(){
        return jsonCurrent.getJSONObject("coord").getDouble("lon");
    }

    public int getTimeZone(){
        return jsonCurrent.getInt("timezone")*1000;
    }

    public String getSunriseLocal(){
        long riseTimeInMs = jsonCurrent.getJSONObject("sys").getLong("sunrise")*1000;
        java.util.Date time = new java.util.Date(riseTimeInMs-7200*1000+getTimeZone());
        return time.toString().substring(11,19);
    }

    public String getSunsetLocal(){
        long riseTimeInMs = jsonCurrent.getJSONObject("sys").getLong("sunset")*1000;
        java.util.Date time = new java.util.Date(riseTimeInMs-7200*1000+getTimeZone());
        return time.toString().substring(11,19);
    }

    public int getAirQuality(){
        JSONObject listJson = (JSONObject) jsonPollution.getJSONArray("list").get(0);
        JSONObject main = listJson.getJSONObject("main");
        return main.getInt("aqi");
    }

    public String getAirQualityDescription(){
        int aq=getAirQuality();
        if (aq==1)
            return "good";
        if (aq==2)
            return "fair";
        if (aq==3)
            return "moderate";
        if (aq==4)
            return "poor";
        if (aq==5)
            return "very poor";
        return "";
    }

    public String getAirComponentAmount(String comp){
        try {
            JSONObject listJson = (JSONObject) jsonPollution.getJSONArray("list").get(0);
            return listJson.getJSONObject("components").getDouble(comp)+"";
        }
        catch(JSONException jsonException){
            return "";
        }
    }

    /**
     * makes sure the city is in the correct format.
     * @return the city in a proper format
     */
    public String getCity(){
        return firstUpperCase(city);
    }

    /**
     *
     * @param url the url to which a connection should be opened
     * @return the retrieved jsonObject
     * @throws IOException if the url is invalid
     */
    private JSONObject openConnection(String url)throws IOException{
        JSONObject json=new JSONObject();
        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }
            reader.close();
            json = new JSONObject(response.toString());
        }
        catch (MalformedURLException malformedURLException) {
            System.out.println("malformed URL");
        }
        return json;
    }

    /**
     *
     * @param city the word to format
     * @return the word with the first letter upper case and the rest lower case.
     */
    public static String firstUpperCase(String city){
        if (city.length()<1)
            return city;
        String first = city.charAt(0)+"";
        String rest = city.substring(1);
        return (first.toUpperCase()+rest.toLowerCase());
    }
}
