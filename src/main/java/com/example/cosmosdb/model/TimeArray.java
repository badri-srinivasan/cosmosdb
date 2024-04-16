package com.example.cosmosdb.model;

public class TimeArray {
	private String time_key;
	private String actualShipRate;
	private String plannedShipRate;
	public String getTime_key() {
		return time_key;
	}
	public void setTime_key(String time_key) {
		this.time_key = time_key;
	}
	public String getActualShipRate() {
		return actualShipRate;
	}
	public void setActualShipRate(String actualShipRate) {
		this.actualShipRate = actualShipRate;
	}
	public String getPlannedShipRate() {
		return plannedShipRate;
	}
	public void setPlannedShipRate(String plannedShipRate) {
		this.plannedShipRate = plannedShipRate;
	}
	@Override
	public String toString() {
		return "TimeArray [time_key=" + time_key + ", actualShipRate=" + actualShipRate + ", plannedShipRate="
				+ plannedShipRate + "]";
	}
	
	

}
