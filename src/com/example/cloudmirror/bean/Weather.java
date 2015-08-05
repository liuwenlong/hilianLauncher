package com.example.cloudmirror.bean;

import java.io.Serializable;
import java.util.ArrayList;
 


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Weather implements Serializable {
 
	private static final long serialVersionUID = 1L;
	
	private String city;
	private String date;
	
	private String week;
	private String temperature;
	private String weather;
	private String weather_icon_id;  //weather_icon_id
	private String weather_icon_url;
	private String wind;
	private String dressing_index;
	private String dressing_advice;
	private String uv_index;
	private String wash_index;
	private String travel_index;
	private String exercise_index;
	private String humidity;
	private String time;
	private String pm25;
	
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	 
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public String getWeather_icon_id() {
		return weather_icon_id;
	}
	public void setWeather_icon_id(String weather_icon_id) {
		this.weather_icon_id = weather_icon_id;
	}
	public String getWeather_icon_url() {
		return weather_icon_url;
	}
	public void setWeather_icon_url(String weather_icon_url) {
		this.weather_icon_url = weather_icon_url;
	}
	public String getWind() {
		return wind;
	}
	public void setWind(String wind) {
		this.wind = wind;
	}
	public String getDressing_index() {
		return dressing_index;
	}
	public void setDressing_index(String dressing_index) {
		this.dressing_index = dressing_index;
	}
	public String getDressing_advice() {
		return dressing_advice;
	}
	public void setDressing_advice(String dressing_advice) {
		this.dressing_advice = dressing_advice;
	}
	public String getUv_index() {
		return uv_index;
	}
	public void setUv_index(String uv_index) {
		this.uv_index = uv_index;
	}
	public String getWash_index() {
		return wash_index;
	}
	public void setWash_index(String wash_index) {
		this.wash_index = wash_index;
	}
	public String getTravel_index() {
		return travel_index;
	}
	public void setTravel_index(String travel_index) {
		this.travel_index = travel_index;
	}
	public String getExercise_index() {
		return exercise_index;
	}
	public void setExercise_index(String exercise_index) {
		this.exercise_index = exercise_index;
	}
	public String getHumidity() {
		return humidity;
	}
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getPm25() {
		return pm25;
	}
	public void setPm25(String pm25) {
		this.pm25 = pm25;
	}
	
	
	
	
	
	
	
	
	
	
	/*

	private Today today;

	private ArrayList<Future> future;

	public Today getToday() {
		return today;
	}

	public void setToday(Today today) {
		this.today = today;
	}

	public ArrayList<Future> getFuture() {
		return future;
	}

	public void setFuture(ArrayList<Future> future) {
		this.future = future;
	}

	public class Today {
		private String city;
		private String date_y;
		private String week;
		private String temperature;
		private String weather;
		private String weather_icon_id;  //weather_icon_id
		private String weather_icon_url;
		private String wind;
		private String dressing_index;
		private String dressing_advice;
		private String uv_index;
		private String wash_index;
		private String travel_index;
		private String exercise_index;
		private String humidity;
		private String time;
		private String pm25;

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getDate_y() {
			return date_y;
		}

		public void setDate_y(String date_y) {
			this.date_y = date_y;
		}

		public String getWeek() {
			return week;
		}

		public void setWeek(String week) {
			this.week = week;
		}

		public String getTemperature() {
			return temperature;
		}

		public void setTemperature(String temperature) {
			this.temperature = temperature;
		}

		public String getWeather() {
			return weather;
		}

		public void setWeather(String weather) {
			this.weather = weather;
		}

		public String getWeather_icon_id() {
			return weather_icon_id;
		}

		public void setWeather_icon_id(String weather_id) {
			this.weather_icon_id = weather_id;
		}

		public String getWeather_icon_url() {
			return weather_icon_url;
		}

		public void setWeather_icon_url(String weather_url) {
			this.weather_icon_url = weather_url;
		}

		public String getWind() {
			return wind;
		}

		public void setWind(String wind) {
			this.wind = wind;
		}

		public String getDressing_index() {
			return dressing_index;
		}

		public void setDressing_index(String dressing_index) {
			this.dressing_index = dressing_index;
		}

		public String getDressing_advice() {
			return dressing_advice;
		}

		public void setDressing_advice(String dressing_advice) {
			this.dressing_advice = dressing_advice;
		}

		public String getUv_index() {
			return uv_index;
		}

		public void setUv_index(String uv_index) {
			this.uv_index = uv_index;
		}

		public String getWash_index() {
			return wash_index;
		}

		public void setWash_index(String wash_index) {
			this.wash_index = wash_index;
		}

		public String getTravel_index() {
			return travel_index;
		}

		public void setTravel_index(String travel_index) {
			this.travel_index = travel_index;
		}

		public String getExercise_index() {
			return exercise_index;
		}

		public void setExercise_index(String exercise_index) {
			this.exercise_index = exercise_index;
		}

		public String getHumidity() {
			return humidity;
		}

		public void setHumidity(String humidity) {
			this.humidity = humidity;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getPm25() {
			return pm25;
		}

		public void setPm25(String pm25) {
			this.pm25 = pm25;
		}

	}

	public class Future {
		private String date_y;
		private String week;
		private String temperature;
		private String weather;
		private String weather_id;//weather_icon_id
		private String weather_url;
		private String wind;

		public String getDate_y() {
			return date_y;
		}

		public void setDate_y(String date_y) {
			this.date_y = date_y;
		}

		public String getWeek() {
			return week;
		}

		public void setWeek(String week) {
			this.week = week;
		}

		public String getTemperature() {
			return temperature;
		}

		public void setTemperature(String temperature) {
			this.temperature = temperature;
		}

		public String getWeather() {
			return weather;
		}

		public void setWeather(String weather) {
			this.weather = weather;
		}

		public String getWeather_id() {
			return weather_id;
		}

		public void setWeather_id(String weather_id) {
			this.weather_id = weather_id;
		}

		public String getWeather_url() {
			return weather_url;
		}

		public void setWeather_url(String weather_url) {
			this.weather_url = weather_url;
		}

		public String getWind() {
			return wind;
		}

		public void setWind(String wind) {
			this.wind = wind;
		}
	}

	public static Bundle pearse(String jsonStr) throws Exception {

		Bundle bundle = new Bundle();

		if (jsonStr != null) {
			
			Log.d("jsonStr", jsonStr);
			
			try {
				JSONObject object = (JSONObject) new JSONTokener(jsonStr).nextValue();

//				int resultcode = object.getInt(RESULT_CODE_NODE);
//
//				bundle.putInt("resultcode", resultcode);
//
//				if (resultcode == 200) {
					Gson gson = new GsonBuilder().serializeNulls().enableComplexMapKeySerialization().create();
					java.lang.reflect.Type WeatherType = new TypeToken<Weather>() {}.getType();
					Weather weather = gson.fromJson(object.getJSONArray("result").get(0).toString(), WeatherType);
					bundle.putSerializable("entity", weather);

//				} else {
//					bundle.putString("reason", object.getString(REASON_NODE));
//				}
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}

		return bundle;
	}

*/}
