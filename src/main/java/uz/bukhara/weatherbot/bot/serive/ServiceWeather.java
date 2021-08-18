package uz.bukhara.weatherbot.bot.serive;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import uz.bukhara.weatherbot.bot.modelLocation.LocationsItem;
import uz.bukhara.weatherbot.bot.modelLocation.MapQuest;
import uz.bukhara.weatherbot.bot.modelWeather.ResponseWeather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServiceWeather {
    private static final String API_KEY="9a15ea5dddmshf61c0922c4bc1b5p1f37d8jsne5aab05a11a6";
    private static final String HOST="community-open-weather-map.p.rapidapi.com";

    private static final String url = "https://open.mapquestapi.com/geocoding/v1/reverse?key=iFaKV4Rp3rWRtGeJoQQYBEUnqJcxF8Cl&location=";

    public static String getCity(Float lat, Float lon) {
        Gson gson = new Gson();
        StringBuilder stringBuilder = new StringBuilder();
        String city = null;
        try {
            URL url1 = new URL(url + lat + "," + lon);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url1.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String row;
            while ((row = bufferedReader.readLine()) != null) {
                stringBuilder.append(row);
            }
            MapQuest mapQuest = gson.fromJson(stringBuilder.toString(), MapQuest.class);
            LocationsItem locationsItem = mapQuest.getResults().get(0).getLocations().get(0);
            city = locationsItem.getAdminArea5();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return city;
    }

    public static ResponseWeather getWeatherFromLocation(Float lat, Float lon) throws IOException {
        Gson gson=new Gson();
         String city = getCity(lat, lon);

        HttpGet httpGet=new HttpGet("https://"+HOST+"/find?q="+city);
        httpGet.setHeader("x-rapidapi-host",HOST);
        httpGet.setHeader("x-rapidapi-key",API_KEY);

        HttpClient client= HttpClients.createDefault();
        HttpResponse response=client.execute(httpGet);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        return gson.fromJson(reader,ResponseWeather.class);
    }
    public static ResponseWeather getWeatherFromCity(String city) throws IOException {
        Gson gson=new Gson();
        HttpGet httpGet=new HttpGet("https://"+HOST+"/find?q="+city);
        httpGet.setHeader("x-rapidapi-host",HOST);
        httpGet.setHeader("x-rapidapi-key",API_KEY);

        HttpClient client= HttpClients.createDefault();
        HttpResponse response=client.execute(httpGet);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        return gson.fromJson(reader,ResponseWeather.class);
    }
}
