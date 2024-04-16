package com.example.cosmosdb;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.example.cosmosdb.controller.AccountSettings;

@Configuration
public class AppConfiguration {

//	@Autowired
//	private CosmosClient client;
	
//	@Autowired
//	private CosmosDatabase database;

    private final String databaseName = "WareHouse";
    private final String containerName = "WMgmt"; //"WasteMgmt";

	@Bean
	public CosmosClient getCosmosClient() {
        System.out.println("Using Azure Cosmos DB endpoint: " + AccountSettings.HOST);

        ArrayList<String> preferredRegions = new ArrayList<String>();
        preferredRegions.add("West US");
		//  Create sync client
		return new CosmosClientBuilder()
            .endpoint(AccountSettings.HOST)
            .key(AccountSettings.MASTER_KEY)
            .preferredRegions(preferredRegions)
            //.userAgentSuffix("CosmosDBJavaQuickstart")
            .consistencyLevel(ConsistencyLevel.EVENTUAL)
            .buildClient();
	}

	@Bean
	public CosmosDatabase getCosmosDatabase() {
        CosmosDatabase database = getCosmosClient().getDatabase(databaseName);
        System.out.println("Checking database " + database.getId() + " completed!\n");
        return database;
	}

	@Bean
	public CosmosContainer getCosmosContainer() {
        CosmosContainer container = getCosmosDatabase().getContainer(containerName);
        System.out.println("Checking container " + container.getId() + " completed!\n");
        return container;
	}

}
