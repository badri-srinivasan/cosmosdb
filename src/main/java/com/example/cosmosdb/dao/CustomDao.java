package com.example.cosmosdb.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosStoredProcedure;
import com.azure.cosmos.models.CosmosItemIdentity;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.CosmosStoredProcedureRequestOptions;
import com.azure.cosmos.models.CosmosStoredProcedureResponse;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.example.cosmosdb.model.WareHouse;
import com.example.cosmosdb.model.TimeArray;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class CustomDao {
	
	@Autowired
	private CosmosContainer container;
	
	@Autowired
	private CosmosDatabase database;
	
	@Autowired
	private CosmosClient client;
	
	public List<TimeArray> getBulkReadTimeArray(){
		
        ArrayList<WareHouse> plans = new ArrayList<>();
        for(int i=0; i < 24 ; i++) {
        	plans.add(new WareHouse("1_2023_3_30_" + i + "_1", "1_2023_3_30_" + i + "_1"));	
        }
        return extractTimeArray(plans);
	}

	private List<TimeArray> extractTimeArray(ArrayList<WareHouse> plans) {
		Long startTimeMillis = System.currentTimeMillis();
		List<WareHouse> readBulkTripPlans = readBulkTripPlans(plans);
		List<TimeArray> timeArray = new ArrayList<>();
		readBulkTripPlans.forEach(plan ->
		timeArray.addAll(plan.getTime_array())
				);
		System.out.println("timeArray.size() - " + timeArray.size());
		
        Long endTimeMillis = System.currentTimeMillis();
		System.out.println("-------------------------");
		System.out.println("Total Time taken to bulk read - " + (endTimeMillis - startTimeMillis));
		System.out.println("-------------------------");
		return timeArray;
	}
	
	public List<TimeArray> getBulkReadTimeArray(String aggregation, String date, Integer fromHour, Integer endHour){
		
        ArrayList<WareHouse> plans = new ArrayList<>();
//        Integer startHourInt = Integer.valueOf(fromHour); //1
//        Integer endHourInt = Integer.valueOf(endHour); //24
        for(int i=fromHour; i < endHour ; i++) {
//        	String date = "2023_3_30";
			plans.add(new WareHouse("1_" + date + "_" + i + "_1", "1_" + date + "_" + i + "_1"));	
        }
        return extractTimeArray(plans);
	}
	
    private List<WareHouse> readBulkTripPlans(ArrayList<WareHouse> plans) {
        //  Using partition key for point read scenarios.
        //  This will help fast look up of items because of partition key
    	List<CosmosItemIdentity> planIdentities = 
    			plans.stream().map(plan -> new CosmosItemIdentity(new PartitionKey(plan.getWarehouseid()), plan.getId())).collect(Collectors.toList());
    	FeedResponse<WareHouse> cosmosItemPropertiesFeedResponse = container.readMany(planIdentities, WareHouse.class);
    	System.out.println("RU Usage - " + cosmosItemPropertiesFeedResponse.getRequestCharge());
    	cosmosItemPropertiesFeedResponse.getResults();
        System.out.println("Item Ids " + cosmosItemPropertiesFeedResponse
                .getResults()
                .stream()
                .map(WareHouse::getId)
                .collect(Collectors.toList()));
        return cosmosItemPropertiesFeedResponse.getResults();
    	
    }
    
    public <T> List<T> getDataFromDatabase(Class<T> type, List<String> ids){
        //  Using partition key for point read scenarios.
        //  This will help fast look up of items because of partition key
    	List<CosmosItemIdentity> planIdentities = ids.stream().map(id -> new CosmosItemIdentity(new PartitionKey(id), id)).collect(Collectors.toList());
    	FeedResponse<T> cosmosItemPropertiesFeedResponse = container.readMany(planIdentities, type);
        return cosmosItemPropertiesFeedResponse.getResults();
	}

	public void executeQuery(String query) {
		queryItems(query);
	}

//	private void queryItems(String query) {
//        // Set some common query options
//        int preferredPageSize = 30;
//        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
//        //  Set populate query metrics to get metrics around query executions
//        queryOptions.setQueryMetricsEnabled(true);
//        
//        CosmosPagedIterable<SmartWareHouseTripPlan> plansIterable = container.queryItems(
//        	  query,
////        	  "SELECT * FROM c where c.id = '1_2023_10_10_1'",
////            "SELECT * FROM c where c.id in ('1_2023_3_30_0_1','1_2023_3_30_14_1','1_2023_3_30_15_1','1_2023_3_30_16_1','1_2023_3_30_17_1',"
////            + "'1_2023_3_30_10_1','1_2023_3_30_11_1','1_2023_3_30_12_1','1_2023_3_30_13_1','1_2023_3_30_18_1','1_2023_3_30_19_1',"
////            + "'1_2023_3_30_1_1','1_2023_3_30_20_1','1_2023_3_30_21_1','1_2023_3_30_22_1','1_2023_3_30_23_1','1_2023_3_30_2_1',"
////            + "'1_2023_3_30_3_1','1_2023_3_30_4_1','1_2023_3_30_5_1','1_2023_3_30_6_1','1_2023_3_30_7_1','1_2023_3_30_8_1','1_2023_3_30_9_1')", 
//            queryOptions, SmartWareHouseTripPlan.class);
//
//        plansIterable.iterableByPage(preferredPageSize).forEach(cosmosItemPropertiesFeedResponse -> {
////            System.out.println("Got a page of query result with " +
////                    cosmosItemPropertiesFeedResponse.getResults().size() + " items(s)"
////                    + " and request charge of " + cosmosItemPropertiesFeedResponse.getRequestCharge());
//        	System.out.println("RU Usage - " + cosmosItemPropertiesFeedResponse.getRequestCharge());
//
//            System.out.println("Item Ids " + cosmosItemPropertiesFeedResponse
//                    .getResults()
//                    .stream()
//                    .map(SmartWareHouseTripPlan::getId)
//                    .collect(Collectors.toList()));
//        });
//    }

	private void queryItems(String query) {
        // Set some common query options
		Long startTimeMillis = System.currentTimeMillis();
		int preferredPageSize = 30;
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        //  Set populate query metrics to get metrics around query executions
        queryOptions.setQueryMetricsEnabled(true);
        
        CosmosPagedIterable<WareHouse> plansIterable = container.queryItems(
        	query, queryOptions, WareHouse.class);

        plansIterable.iterableByPage(preferredPageSize).forEach(cosmosItemPropertiesFeedResponse -> {
        	System.out.println("RU Usage - " + cosmosItemPropertiesFeedResponse.getRequestCharge());

            System.out.println("Item Ids " + cosmosItemPropertiesFeedResponse
                    .getResults()
                    .stream()
                    .map(WareHouse::getId)
                    .collect(Collectors.toList()));
        });
        Long endTimeMillis = System.currentTimeMillis();
		System.out.println("-------------------------");
		System.out.println("Total Time taken query - " + (endTimeMillis - startTimeMillis));
		System.out.println("-------------------------");
    }
	
	public void insertData() {
		Map<String, Object> map = new HashMap<>();
		Integer i = 1201;
		Integer siteId = 1;
		Integer deptId = 1;
		Integer shiftId = 1;
		Integer lineId = 1;
		Date dt = new Date();
		LocalDateTime localDateTime = LocalDateTime.now();
		while(i <= 1400) {
			map.put("id", i.toString());
			map.put("pKey", "1000");
			map.put("mttr", 100 + i);
			map.put("avg", 150 + i);
			map.put("siteId", siteId);
			map.put("deptId", deptId);
			map.put("shiftId", shiftId);
			map.put("lineId", lineId);
			map.put("fromDate", localDateTime.toString());
			
			
			container.createItem(map);
			System.out.println(i);
			if(i%2 ==0) {
				localDateTime = localDateTime.plusDays(1);
			}
			if(i%3 ==0) {
				deptId++;
			}
			if(i%4 ==0) {
				lineId++;
			}
			if(i%5 ==0) {
				shiftId++;
			}
			if(i%10 == 0) {
				siteId++;
			}
			i++;
		}
	}
	
	public Object executeStoredProc() {
		CosmosStoredProcedure sproc = container.getScripts().getStoredProcedure("mttr");
		CosmosStoredProcedureRequestOptions options = new CosmosStoredProcedureRequestOptions();
		options.setPartitionKey(new PartitionKey("1000"));
		List<Object> items = new ArrayList<>();
		CosmosStoredProcedureResponse response = sproc.execute(
		    items, 
		    options
		);
		ObjectMapper mapper = new ObjectMapper();
	    try {
			JsonNode readTree = mapper.readTree(response.getResponseAsString());
//			List<Map<String, Object>> result = mapper.convertValue(readTree, new TypeReference<List<Map<String, Object>>>(){});
			return readTree;
	    } catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null;
	}

}
