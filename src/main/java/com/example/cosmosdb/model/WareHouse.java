package com.example.cosmosdb.model;

import java.util.List;

public class WareHouse {
	private String id;
	private String warehouseid;
	private List<TimeArray> time_array;
	
	public WareHouse(String id, String warehouseid) {
		super();
		this.id = id;
		this.warehouseid = warehouseid;
	}
	public WareHouse() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWarehouseid() {
		return warehouseid;
	}
	public void setWarehouseid(String warehouseid) {
		this.warehouseid = warehouseid;
	}
	public List<TimeArray> getTime_array() {
		return time_array;
	}
	public void setTime_array(List<TimeArray> time_array) {
		this.time_array = time_array;
	}
	@Override
	public String toString() {
		return "SmartWareHouseTripPlan [id=" + id + ", warehouseid=" + warehouseid + ", time_array=" + time_array + "]";
	}

	
}
