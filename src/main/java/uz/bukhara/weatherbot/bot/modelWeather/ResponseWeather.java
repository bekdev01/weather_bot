package uz.bukhara.weatherbot.bot.modelWeather;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseWeather{

	@SerializedName("count")
	private int count;

	@SerializedName("cod")
	private String cod;

	@SerializedName("message")
	private String message;

	@SerializedName("list")
	private List<ListItem> list;

	public int getCount(){
		return count;
	}

	public String getCod(){
		return cod;
	}

	public String getMessage(){
		return message;
	}

	public List<ListItem> getList(){
		return list;
	}
}