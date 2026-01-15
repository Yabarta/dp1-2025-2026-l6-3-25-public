package es.us.dp1.l6_3_24_25.Petris.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.l6_3_24_25.Petris.exceptions.ResourceNotFoundException;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;

@Epic("Utility module")
@Feature("Rest Preconditions")
@SpringBootTest
@DisplayName("RestPreconditions Tests")
class RestPreconditionsTest {

    @Test
    @DisplayName("Should throw exception when resource is null")
    @Description("Test that ResourceNotFoundException is thrown when checking null resource")
    @Story("Check null resource")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testCheckNotNull_ThrowsException() {
        assertThrows(ResourceNotFoundException.class, () -> {
            RestPreconditions.checkNotNull(null, "User", "id", 1);
        });
    }

    @Test
    @DisplayName("Should throw exception with correct details")
    @Description("Test that ResourceNotFoundException contains correct details about the resource")
    @Story("Exception contains details")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testCheckNotNull_ExceptionDetails() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            RestPreconditions.checkNotNull(null, "User", "username", "nonexistent");
        });

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("User") || exception.getMessage().contains("username"));
    }

    @Test
    @DisplayName("Should return resource when not null")
    @Description("Test that the resource is returned when it's not null")
    @Story("Return valid resource")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testCheckNotNull_ReturnsResource() {
        String resource = "ValidResource";
        String result = RestPreconditions.checkNotNull(resource, "Resource", "type", "String");

        assertNotNull(result);
        assertEquals("ValidResource", result);
    }

    @Test
    @DisplayName("Should return different types of resources")
    @Description("Test that checkNotNull works with different object types")
    @Story("Support different types")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testCheckNotNull_DifferentTypes() {
        Integer intResource = 42;
        Integer intResult = RestPreconditions.checkNotNull(intResource, "Integer", "value", 42);
        assertEquals(42, intResult);

        Object objResource = new Object();
        Object objResult = RestPreconditions.checkNotNull(objResource, "Object", "instance", null);
        assertSame(objResource, objResult);

        Boolean boolResource = true;
        Boolean boolResult = RestPreconditions.checkNotNull(boolResource, "Boolean", "value", true);
        assertEquals(true, boolResult);
    }

    @Test
    @DisplayName("Should work with various resource names")
    @Description("Test that various resource names are handled correctly")
    @Story("Handle different resource names")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testCheckNotNull_DifferentResourceNames() {
        String resource = "Test";

        String result1 = RestPreconditions.checkNotNull(resource, "User", "id", 1);
        assertEquals("Test", result1);

        String result2 = RestPreconditions.checkNotNull(resource, "Player", "nickname", "test");
        assertEquals("Test", result2);

        String result3 = RestPreconditions.checkNotNull(resource, "Match", "code", "ABC");
        assertEquals("Test", result3);
    }

    @Test
    @DisplayName("Should handle null fieldValue in message")
    @Description("Test that null fieldValue is handled properly")
    @Story("Handle null field value")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testCheckNotNull_NullFieldValue() {
        assertThrows(ResourceNotFoundException.class, () -> {
            RestPreconditions.checkNotNull(null, "Entity", "field", null);
        });
    }

    @Test
    @DisplayName("Should handle empty string resources")
    @Description("Test that empty strings are not treated as null")
    @Story("Handle empty strings")
    @Owner("DiegoVicenteCamara(RXW1249)")
    void testCheckNotNull_EmptyString() {
        String emptyResource = "";
        String result = RestPreconditions.checkNotNull(emptyResource, "String", "value", "");

        assertNotNull(result);
        assertEquals("", result);
    }

    
}