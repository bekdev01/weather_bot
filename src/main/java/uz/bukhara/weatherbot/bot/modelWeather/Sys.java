package uz.bukhara.weatherbot.bot.modelWeather;

import com.google.gson.annotations.SerializedName;

public class Sys{

	@SerializedName("country")
	private String country;

	public String getCountry(){
		return country;
	}
}