package com.benromberg.cryptomato.web;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.benromberg.cryptomato.core.dao.DynamoDbRule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class GuiceTestModule extends AbstractModule {
    @Provides
    public Context getLambdaContext() {
        return new DummyContext();
    }

    @Provides
    public AmazonDynamoDB getDynamoDb() {
        return DynamoDbRule.getDB();
    }

    @Override
    protected void configure() {
    }

    private class DummyContext implements Context {
        @Override
        public String getAwsRequestId() {
            return null;
        }

        @Override
        public String getLogGroupName() {
            return null;
        }

        @Override
        public String getLogStreamName() {
            return null;
        }

        @Override
        public String getFunctionName() {
            return null;
        }

        @Override
        public String getFunctionVersion() {
            return null;
        }

        @Override
        public String getInvokedFunctionArn() {
            return null;
        }

        @Override
        public CognitoIdentity getIdentity() {
            return null;
        }

        @Override
        public ClientContext getClientContext() {
            return null;
        }

        @Override
        public int getRemainingTimeInMillis() {
            return 0;
        }

        @Override
        public int getMemoryLimitInMB() {
            return 0;
        }

        @Override
        public LambdaLogger getLogger() {
            return null;
        }
    }
}
