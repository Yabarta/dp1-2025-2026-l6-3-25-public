package es.us.dp1.l6_3_24_25.Petris.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectRetrievalFailureException;

import es.us.dp1.l6_3_24_25.Petris.model.BaseEntity;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;

@Epic("Utility module")
@Feature("Entity Utils")
@DisplayName("EntityUtils Tests")
class EntityUtilsTest {

    private Collection<TestEntity> entities;

    @BeforeEach
    void setUp() {
        entities = new ArrayList<>();
    }

    @Test
    @DisplayName("Should find entity by id")
    @Description("Test that an entity can be found by its id")
    @Story("Find entity by id")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGetById_Success() {
        TestEntity entity1 = new TestEntity(1, "Entity1");
        TestEntity entity2 = new TestEntity(2, "Entity2");
        TestEntity entity3 = new TestEntity(3, "Entity3");

        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);

        TestEntity found = EntityUtils.getById(entities, TestEntity.class, 2);

        assertNotNull(found);
        assertEquals(2, found.getId());
        assertEquals("Entity2", found.getName());
    }

    @Test
    @DisplayName("Should throw exception when entity not found")
    @Description("Test that ObjectRetrievalFailureException is thrown when entity not found")
    @Story("Entity not found")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGetById_NotFound() {
        TestEntity entity1 = new TestEntity(1, "Entity1");
        TestEntity entity2 = new TestEntity(2, "Entity2");

        entities.add(entity1);
        entities.add(entity2);

        assertThrows(ObjectRetrievalFailureException.class, () -> {
            EntityUtils.getById(entities, TestEntity.class, 999);
        });
    }

    @Test
    @DisplayName("Should throw exception for empty collection")
    @Description("Test that ObjectRetrievalFailureException is thrown for empty collection")
    @Story("Empty collection")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGetById_EmptyCollection() {
        assertThrows(ObjectRetrievalFailureException.class, () -> {
            EntityUtils.getById(entities, TestEntity.class, 1);
        });
    }

    @Test
    @DisplayName("Should find first matching entity in large collection")
    @Description("Test that correct entity is found even in large collections")
    @Story("Large collection")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGetById_LargeCollection() {
        for (int i = 1; i <= 1000; i++) {
            entities.add(new TestEntity(i, "Entity" + i));
        }

        TestEntity found = EntityUtils.getById(entities, TestEntity.class, 500);

        assertNotNull(found);
        assertEquals(500, found.getId());
    }

    @Test
    @DisplayName("Should work with HashSet collection")
    @Description("Test that EntityUtils works with different collection types")
    @Story("Different collection types")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGetById_DifferentCollectionTypes() {
        Collection<TestEntity> hashSet = new HashSet<>();
        hashSet.add(new TestEntity(1, "Entity1"));
        hashSet.add(new TestEntity(2, "Entity2"));

        TestEntity found = EntityUtils.getById(hashSet, TestEntity.class, 1);

        assertNotNull(found);
        assertEquals(1, found.getId());
    }

    @Test
    @DisplayName("Should check entity class type")
    @Description("Test that only entities of the specified class are returned")
    @Story("Class type checking")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGetById_ClassTypeValidation() {
        entities.add(new TestEntity(1, "Entity1"));
        entities.add(new TestEntity(2, "Entity2"));

        TestEntity found = EntityUtils.getById(entities, TestEntity.class, 1);

        assertTrue(TestEntity.class.isInstance(found));
    }

    @Test
    @DisplayName("Should return first entity with matching id")
    @Description("Test that first matching entity is returned")
    @Story("First match")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGetById_FirstMatch() {
        TestEntity entity1 = new TestEntity(1, "FirstEntity");
        TestEntity entity2 = new TestEntity(2, "SecondEntity");
        TestEntity entity3 = new TestEntity(3, "ThirdEntity");

        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);

        TestEntity found = EntityUtils.getById(entities, TestEntity.class, 1);

        assertEquals("FirstEntity", found.getName());
    }

    @Test
    @DisplayName("Should work with entity id = 0")
    @Description("Test that entity with id 0 can be found")
    @Story("Entity with id 0")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGetById_ZeroId() {
        TestEntity entity = new TestEntity(0, "ZeroEntity");
        entities.add(entity);

        TestEntity found = EntityUtils.getById(entities, TestEntity.class, 0);

        assertNotNull(found);
        assertEquals(0, found.getId());
    }

    @Test
    @DisplayName("Should throw exception with correct entity class")
    @Description("Test that exception contains correct entity class information")
    @Story("Exception details")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testGetById_ExceptionContainsClass() {
        ObjectRetrievalFailureException exception = assertThrows(ObjectRetrievalFailureException.class, () -> {
            EntityUtils.getById(entities, TestEntity.class, 1);
        });

        assertNotNull(exception);
        assertNotNull(exception.getMessage());
    }

    // Test Entity class that extends BaseEntity
    private static class TestEntity extends BaseEntity {
        private String name;

        public TestEntity(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}