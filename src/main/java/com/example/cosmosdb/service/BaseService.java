package com.example.cosmosdb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cosmosdb.dao.CustomDao;
import com.example.cosmosdb.model.BaseModel;

@Service
public class BaseService {
	
	@Autowired
	CustomDao tripPlanDao;
	
//	public <D, T extends BaseModel> List<D> getData(Class<D> dType, Class<T> type, String startTime, String endTime) {
//		List<T> dataFromDatabase = tripPlanDao.<T>getDataFromDatabase(type, null);
//		convertMinuteToHour(type, dataFromDatabase);
//		return null;
//	}
//	
//	private <T extends BaseModel> BaseModel convertMinuteToHour(Class<T> type, List<T> items) {
//		return ((BaseModel)type).convertMinuteToHour(items);
//	}
	
//	private <T extends BaseModel> BaseModel convertMinuteToHour(Class<T> type, List<T> items) {
//		return ((BaseModel)type).convertMinuteToHour(items);
//	}

}
