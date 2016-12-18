package com.benromberg.cryptomato.core.dao;

import com.almworks.sqlite4java.SQLite;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.benromberg.cryptomato.model.Entity;
import org.junit.rules.ExternalResource;

import static java.util.Arrays.*;

public class DynamoDbRule extends ExternalResource {
    static {
        SQLite.setLibraryPath("target/sqlite-binaries/");
    }

    private static AmazonDynamoDB dynamoDb = DynamoDBEmbedded.create().amazonDynamoDB();

    @Override
    protected void after() {
        ListTablesResult tables = dynamoDb.listTables();
        tables.getTableNames().forEach(table -> {
            ScanResult ids = dynamoDb.scan(table, asList(Entity.ID_PROPERTY));
            ids.getItems().forEach(id -> dynamoDb.deleteItem(table, id));
        });
    }

    public static AmazonDynamoDB getDB() {
        return dynamoDb;
    }
}
