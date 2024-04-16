package com.example.cosmosdb.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cosmosdb.dao.CustomDao;
import com.example.cosmosdb.model.TimeArray;

@Service
public class CustomService {

	@Autowired
	private CustomDao tripPlanDao;
	
	public List<TimeArray> getBulkReadTimeArray(){
		return tripPlanDao.getBulkReadTimeArray();
	}
	
	public List<TimeArray> getBulkReadTimeArray(String aggregation, String date, Integer fromHour, Integer endHour){
		return tripPlanDao.getBulkReadTimeArray(aggregation, date, fromHour, endHour);
	}

	public void executeQuery(String query) {
		tripPlanDao.executeQuery(query);
	}
	
	public void insertData() {
		tripPlanDao.insertData();
	}
	
	public Object executeStoredProc() {
		return tripPlanDao.executeStoredProc();
	}
	
}
