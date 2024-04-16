package com.example.cosmosdb.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.cosmosdb.model.TimeArray;
import com.example.cosmosdb.service.CustomService;

@RestController
public class CustomController {

	@Autowired
	private CustomService tripPlanService;
	
	
	@GetMapping("/KPIBulkRead")
	public List<TimeArray> getTriplanKPIBulkRead() {
		return tripPlanService.getBulkReadTimeArray();
	}
	
	@GetMapping("/KPIBulkReadQuery")
	public List<TimeArray> getTriplanKPIBulkReadQuery(@RequestParam String aggregation, @RequestParam String date, @RequestParam Integer fromHour, @RequestParam Integer endHour) {
		return tripPlanService.getBulkReadTimeArray(aggregation, date, fromHour, endHour);
	}
	
	@GetMapping("/executeQuery")
	public void executeQuery(@RequestParam String query) {
		tripPlanService.executeQuery(query);
	}
	
	@GetMapping("/insertData")
	public void insertData() {
		tripPlanService.insertData();
	}
	
	@GetMapping("/executeStoredProc")
	public Object executeStoredProc() {
		return tripPlanService.executeStoredProc();
	}
}
