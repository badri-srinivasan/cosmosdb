// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.cosmosdb.controller;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemIdentity;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.example.cosmosdb.model.WareHouse;
import com.example.cosmosdb.model.TimeArray;
import com.fasterxml.jackson.databind.JsonNode;

public class TripPlanReadTest {

    private CosmosClient client;
    private final String databaseName = "iotsensor";
    private final String containerName = "warehouse_raw";

    private CosmosDatabase database;
    private CosmosContainer container;

    public void close() {
        client.close();
    }

    public static void main(String[] args) {
    	TripPlanReadTest p = new TripPlanReadTest();

        try {
            p.getStartedDemo();
            System.out.println("Demo complete, please hold while resources are released");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(String.format("Cosmos getStarted failed with %s", e));
        } finally {
            System.out.println("Closing the client");
            p.close();
        }
        System.exit(0);
    }

    //  </Main>

    private void getStartedDemo() throws Exception {
        System.out.println("Using Azure Cosmos DB endpoint: " + AccountSettings.HOST);

        ArrayList<String> preferredRegions = new ArrayList<String>();
        preferredRegions.add("West US");

        //  Create sync client
        client = new CosmosClientBuilder()
            .endpoint(AccountSettings.HOST)
            .key(AccountSettings.MASTER_KEY)
            .preferredRegions(preferredRegions)
            //.userAgentSuffix("CosmosDBJavaQuickstart")
            .consistencyLevel(ConsistencyLevel.EVENTUAL)
            .buildClient();
        
        createDatabaseIfNotExists();
        createContainerIfNotExists();
        // scaleContainer();
        
        ArrayList<WareHouse> plans = new ArrayList<>();
        for(int i=0; i < 24 ; i++) {
        	plans.add(new WareHouse("1_2023_3_30_" + i + "_1", "1_2023_3_30_" + i + "_1"));	
        }

        long startTimeMillis = System.currentTimeMillis();
		System.out.println("Time - " + startTimeMillis);
//        readTripPlans(plans);
        long endTimeMillis = System.currentTimeMillis();
		System.out.println("Time - " + endTimeMillis);
		System.out.println("-------------------------");
		System.out.println("Total Time taken to read - " + (endTimeMillis - startTimeMillis));
		System.out.println("-------------------------");

		
        startTimeMillis = System.currentTimeMillis();
//		System.out.println("Time - " + startTimeMillis);
		queryItems();
        endTimeMillis = System.currentTimeMillis();
//		System.out.println("Time - " + endTimeMillis);
		System.out.println("-------------------------");
		System.out.println("Total Time taken to query - " + (endTimeMillis - startTimeMillis));
		System.out.println("-------------------------");

        startTimeMillis = System.currentTimeMillis();
//		System.out.println("Time - " + startTimeMillis);
//		List<SmartWareHouseTripPlan> readBulkTripPlans = readBulkTripPlans(plans);
		List<TimeArray> timeArray = new ArrayList<>();
//		readBulkTripPlans.forEach(plan ->	timeArray.addAll(plan.getTime_array()));
		System.out.println("timeArray.size() - " + timeArray.size());
		
        endTimeMillis = System.currentTimeMillis();
//		System.out.println("Time - " + endTimeMillis);
		System.out.println("-------------------------");
		System.out.println("Total Time taken to bulk read - " + (endTimeMillis - startTimeMillis));
		System.out.println("-------------------------");

    }
    private void createDatabaseIfNotExists() throws Exception {
        database = client.getDatabase(databaseName);
        System.out.println("Checking database " + database.getId() + " completed!\n");
    }


    private void createContainerIfNotExists() throws Exception {
        container = database.getContainer(containerName);
        System.out.println("Checking container " + container.getId() + " completed!\n");
    }

    private void readTripPlans(ArrayList<WareHouse> plans) {
        //  Using partition key for point read scenarios.
        //  This will help fast look up of items because of partition key
        plans.forEach(plan -> {
            try {
                CosmosItemResponse<WareHouse> planResponse = 
                		container.readItem(plan.getId(), 
                				new PartitionKey(plan.getWarehouseid()), WareHouse.class);
                double requestCharge = planResponse.getRequestCharge();
                Duration requestLatency = planResponse.getDuration();
                System.out.println(String.format("Item successfully read with id %s with a charge of %.2f and within duration %s",
                		planResponse.getItem().getId(), requestCharge, requestLatency.toMillis()));
                System.out.println(planResponse.getItem());
            } catch (CosmosException e) {
                e.printStackTrace();
                System.err.println(String.format("Read Item failed with %s", e));
            }
        });
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

    private void queryItems() {
        // Set some common query options
        int preferredPageSize = 30;
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        //  Set populate query metrics to get metrics around query executions
        queryOptions.setQueryMetricsEnabled(true);
        
        CosmosPagedIterable<JsonNode> plansIterable = container.queryItems(
        	  "SELECT * FROM c where c.id = '1_2023_10_10_1'",
//            "SELECT * FROM c where c.id in ('1_2023_3_30_0_1','1_2023_3_30_14_1','1_2023_3_30_15_1','1_2023_3_30_16_1','1_2023_3_30_17_1',"
//            + "'1_2023_3_30_10_1','1_2023_3_30_11_1','1_2023_3_30_12_1','1_2023_3_30_13_1','1_2023_3_30_18_1','1_2023_3_30_19_1',"
//            + "'1_2023_3_30_1_1','1_2023_3_30_20_1','1_2023_3_30_21_1','1_2023_3_30_22_1','1_2023_3_30_23_1','1_2023_3_30_2_1',"
//            + "'1_2023_3_30_3_1','1_2023_3_30_4_1','1_2023_3_30_5_1','1_2023_3_30_6_1','1_2023_3_30_7_1','1_2023_3_30_8_1','1_2023_3_30_9_1')", 
            queryOptions, JsonNode.class);

        plansIterable.iterableByPage(preferredPageSize).forEach(cosmosItemPropertiesFeedResponse -> {
//            System.out.println("Got a page of query result with " +
//                    cosmosItemPropertiesFeedResponse.getResults().size() + " items(s)"
//                    + " and request charge of " + cosmosItemPropertiesFeedResponse.getRequestCharge());
        	System.out.println("RU Usage - " + cosmosItemPropertiesFeedResponse.getRequestCharge());
        	List<JsonNode> results = cosmosItemPropertiesFeedResponse.getResults();
        	results.forEach(jsonNode1 ->
        	{
        	Iterator<String> iterator = jsonNode1.fieldNames();
        	iterator.forEachRemaining(e -> System.out.println("Keys - " + e));
        	});
        	
//            System.out.println("Item Ids " + cosmosItemPropertiesFeedResponse
//                    .getResults()
//                    .stream()
//                    .map(JSONPObject::getId)
//                    .collect(Collectors.toList()));
        });
    }
}
