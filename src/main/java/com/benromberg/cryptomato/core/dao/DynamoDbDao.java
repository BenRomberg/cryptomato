package com.benromberg.cryptomato.core.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.model.*;
import com.benromberg.cryptomato.core.ExceptionUtil;
import com.benromberg.cryptomato.core.JsonMapper;
import com.benromberg.cryptomato.model.Entity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.StreamSupport;

import static com.benromberg.cryptomato.model.Entity.*;
import static java.util.stream.Collectors.*;

public class DynamoDbDao<E extends Entity<String>> {
    private static final ObjectMapper JSON_MAPPER = JsonMapper.wrapInstance(createDynamoDbMapper());
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbDao.class);

    private final Class<E> elementClass;
    private Table table;

    public DynamoDbDao(AmazonDynamoDB database, Class<E> elementClass, String collectionName) {
        this.elementClass = elementClass;
        CreateTableRequest createTableRequest = createTableRequest(collectionName);
        DynamoDB dynamoDb = new DynamoDB(database);
        try {
            table = dynamoDb.createTable(createTableRequest);
        } catch (ResourceInUseException exception) {
            table = dynamoDb.getTable(collectionName);
            LOGGER.info("Using preexisting table " + collectionName);
        }
    }

    private CreateTableRequest createTableRequest(String collectionName) {
        CreateTableRequest createTableRequest = new CreateTableRequest(collectionName,
                Arrays.asList(new KeySchemaElement(ID_PROPERTY, KeyType.HASH)));
        createTableRequest.withAttributeDefinitions(new AttributeDefinition(ID_PROPERTY, ScalarAttributeType.S));
        createTableRequest.withProvisionedThroughput(new ProvisionedThroughput(2L, 2L));
        return createTableRequest;
    }

    public Optional<E> findById(String id) {
        return toOptionalEntity(table.getItem(ID_PROPERTY, id));
    }

    private E fromJson(String json) {
        return ExceptionUtil.convertException(() -> JSON_MAPPER.readValue(json, elementClass));
    }

    public void insert(E element) {
        table.putItem(Item.fromJSON(toJson(element)));
    }

    public E insertOrUpdate(E element) {
        PutItemSpec spec = new PutItemSpec();
        spec.withItem(Item.fromJSON(toJson(element)));
        table.putItem(spec);
        return element;
    }

    protected Optional<E> update(String id, String key, Object value) {
        return update(id, "SET #key = :value", Collections.singletonMap("#key", key),
                Collections.singletonMap(":value", value));
    }

    protected Optional<E> update(String id, String updateExpression, Map<String, String> nameMap,
                                 Map<String, Object> valueMap) {
        UpdateItemSpec spec = new UpdateItemSpec();
        spec.withUpdateExpression(updateExpression);
        spec.withPrimaryKey(ID_PROPERTY, id);
        spec.withNameMap(extendNameMapWithId(nameMap));
        spec.withValueMap(valueMap);
        spec.withReturnValues(ReturnValue.ALL_NEW);
        spec.withConditionExpression("attribute_exists(#id)");
        try {
            return Optional.of(fromJson(table.updateItem(spec).getItem().toJSON()));
        } catch (ConditionalCheckFailedException exception) {
            return Optional.empty();
        }
    }

    private Map<String, String> extendNameMapWithId(Map<String, String> nameMap) {
        Map<String, String> extendedNameMap = new HashMap<>(nameMap);
        extendedNameMap.put("#id", ID_PROPERTY);
        return extendedNameMap;
    }

    protected String getJsonString(Object value) {
        return JSON_MAPPER.convertValue(value, String.class);
    }

    protected Map<String, Object> getJsonMap(Object value) {
        return JSON_MAPPER.convertValue(value, new TypeReference<Map<String, Object>>() {
        });
    }

    private Optional<E> toOptionalEntity(Item sourceItem) {
        return Optional.ofNullable(sourceItem).map(item -> fromJson(item.toJSON()));
    }

    private String toJson(E element) {
        return ExceptionUtil.convertException(() -> JSON_MAPPER.writeValueAsString(element));
    }

    private static ObjectMapper createDynamoDbMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule() {
            {
                addSerializer(new EmptyStringIsNullSerializer());
                addDeserializer(String.class, new NullIsEmptyStringDeserializer());
            }
        });
        return mapper;
    }

    public void remove(String id) {
        table.deleteItem(ID_PROPERTY, id);
    }

    public List<E> findAll() {
        return StreamSupport.stream(table.scan(new ScanSpec().withSelect(Select.ALL_ATTRIBUTES)).spliterator(), false)
                .map(item -> fromJson(item.toJSON())).collect(toList());
    }
}