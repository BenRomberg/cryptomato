package com.benromberg.cryptomato.core.dao;

import com.benromberg.cryptomato.model.Entity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.Rule;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class DynamoDbDaoTest {
    private static final String DUMMY_ID_2 = "xdummy id 2";
    private static final String EMPTY_STRING_VALUE = "";
    private static final String DUMMY_VALUE = "dummy value";
    private static final String DUMMY_ID = "dummy-id";
    private static final String NEW_VALUE = "new-value";
    public static final String COLLECTION_NAME = "dummy";

    @Rule
    public DynamoDbRule dynamoDbRule = new DynamoDbRule();

    private final DynamoDbDao<DummyEntity> dao = new DynamoDbDao<>(DynamoDbRule.getDB(), DummyEntity.class, COLLECTION_NAME);

    @Test
    public void initialize_WithPreexistingTable_Succeeds() throws Exception {
        new DynamoDbDao<>(DynamoDbRule.getDB(), DummyEntity.class, COLLECTION_NAME);
    }

    @Test
    public void nonExistingEntity_CanNotBeFound() throws Exception {
        Optional<DummyEntity> entity = dao.findById(DUMMY_ID);
        assertThat(entity).isEmpty();
    }

    @Test
    public void insertedEntity_CanBeFound() throws Exception {
        dao.insert(new DummyEntity(DUMMY_ID, DUMMY_VALUE));
        DummyEntity client = dao.findById(DUMMY_ID).get();
        assertThat(client.getId()).isEqualTo(DUMMY_ID);
        assertThat(client.getValue()).isEqualTo(DUMMY_VALUE);
    }

    @Test
    public void entityWithEmptyStringValue_CanBeFound() throws Exception {
        dao.insert(new DummyEntity(DUMMY_ID, EMPTY_STRING_VALUE));
        DummyEntity client = dao.findById(DUMMY_ID).get();
        assertThat(client.getValue()).isEqualTo(EMPTY_STRING_VALUE);
    }

    @Test
    public void insertOrUpdate_WithoutExistingEntity_ReturnsCreatedElement() throws Exception {
        DummyEntity element = dao.insertOrUpdate(new DummyEntity(DUMMY_ID, DUMMY_VALUE));
        assertThat(element.getId()).isEqualTo(DUMMY_ID);
        assertThat(element.getValue()).isEqualTo(DUMMY_VALUE);
    }

    @Test
    public void insertOrUpdate_WithoutExistingEntity_CanBeFound() throws Exception {
        dao.insertOrUpdate(new DummyEntity(DUMMY_ID, DUMMY_VALUE));
        DummyEntity element = dao.findById(DUMMY_ID).get();
        assertThat(element.getId()).isEqualTo(DUMMY_ID);
        assertThat(element.getValue()).isEqualTo(DUMMY_VALUE);
    }

    @Test
    public void insertOrUpdate_WithExistingEntity_ReturnsUpdatedElement() throws Exception {
        dao.insert(new DummyEntity(DUMMY_ID, DUMMY_VALUE));
        DummyEntity element = dao.insertOrUpdate(new DummyEntity(DUMMY_ID, NEW_VALUE));
        assertThat(element.getId()).isEqualTo(DUMMY_ID);
        assertThat(element.getValue()).isEqualTo(NEW_VALUE);
    }

    @Test
    public void insertOrUpdate_WithExistingEntity_CanBeFound() throws Exception {
        dao.insert(new DummyEntity(DUMMY_ID, DUMMY_VALUE));
        dao.insertOrUpdate(new DummyEntity(DUMMY_ID, NEW_VALUE));
        DummyEntity element = dao.findById(DUMMY_ID).get();
        assertThat(element.getId()).isEqualTo(DUMMY_ID);
        assertThat(element.getValue()).isEqualTo(NEW_VALUE);
    }

    @Test
    public void remove_DeletesExistingEntity() throws Exception {
        dao.insert(new DummyEntity(DUMMY_ID, DUMMY_VALUE));
        dao.remove(DUMMY_ID);
        assertThat(dao.findById(DUMMY_ID)).isEmpty();
    }

    @Test
    public void remove_WithoutMatchingEntity_DoesNothing() throws Exception {
        dao.remove(DUMMY_ID);
        assertThat(dao.findById(DUMMY_ID)).isEmpty();
    }

    @Test
    public void findAll_ReturnsAllEntities_IncludingAllFields() throws Exception {
        dao.insert(new DummyEntity(DUMMY_ID, DUMMY_VALUE));
        dao.insert(new DummyEntity(DUMMY_ID_2, DUMMY_VALUE));
        assertThat(dao.findAll()).extracting(DummyEntity::getId, DummyEntity::getValue).containsOnly(
                tuple(DUMMY_ID, DUMMY_VALUE), tuple(DUMMY_ID_2, DUMMY_VALUE));
    }

    private static class DummyEntity extends Entity<String> {
        @JsonCreator
        public DummyEntity(String id, String value) {
            super(id);
            this.value = value;
        }

        @JsonProperty
        private final String value;

        public String getValue() {
            return value;
        }
    }
}
