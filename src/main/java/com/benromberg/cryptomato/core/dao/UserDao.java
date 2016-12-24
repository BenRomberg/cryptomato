package com.benromberg.cryptomato.core.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.benromberg.cryptomato.model.Configuration;
import com.benromberg.cryptomato.model.User;

import javax.inject.Inject;

public class UserDao extends DynamoDbDao<User> {
    @Inject
    public UserDao(AmazonDynamoDB database, Configuration configuration) {
        super(database, User.class, configuration.getDynamoDbTableName("user"));
    }
}
