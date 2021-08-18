package uz.bukhara.weatherbot.bot.modelWeather;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ListItem{

	@SerializedName("dt")
	private int dt;

	@SerializedName("rain")
	private Object rain;

	@SerializedName("coord")
	private Coord coord;

	@SerializedName("snow")
	private Object snow;

	@SerializedName("name")
	private String name;

	@SerializedName("weather")
	private List<WeatherItem> weather;

	@SerializedName("main")
	private Main main;

	@SerializedName("id")
	private int id;

	@SerializedName("clouds")
	private Clouds clouds;

	@SerializedName("sys")
	private Sys sys;

	@SerializedName("wind")
	private Wind wind;

	public int getDt(){
		return dt;
	}

	public Object getRain(){
		return rain;
	}

	public Coord getCoord(){
		return coord;
	}

	public Object getSnow(){
		return snow;
	}

	public String getName(){
		return name;
	}

	public List<WeatherItem> getWeather(){
		return weather;
	}

	public Main getMain(){
		return main;
	}

	public int getId(){
		return id;
	}

	public Clouds getClouds(){
		return clouds;
	}

	public Sys getSys(){
		return sys;
	}

	public Wind getWind(){
		return wind;
	}
}