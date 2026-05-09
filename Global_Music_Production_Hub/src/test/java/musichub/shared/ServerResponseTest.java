package musichub.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServerResponseTest {

    @Test
    void ok_createsSuccessResponse() {
        ServerResponse<String> response = ServerResponse.ok("Message", "data");
        
        assertTrue(response.isSuccess());
        assertEquals("Message", response.getMessage());
        assertEquals("data", response.getData());
    }

    @Test
    void ok_withNullData_createsSuccessResponse() {
        ServerResponse<Object> response = ServerResponse.ok("Success", null);
        
        assertTrue(response.isSuccess());
        assertEquals("Success", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void error_createsFailureResponse() {
        ServerResponse<Object> response = ServerResponse.error("Error message");
        
        assertFalse(response.isSuccess());
        assertEquals("Error message", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void getMessage_returnsMessage() {
        ServerResponse<String> response = ServerResponse.ok("Test message", "data");
        assertEquals("Test message", response.getMessage());
    }

    @Test
    void getData_returnsData() {
        String data = "test data";
        ServerResponse<String> response = ServerResponse.ok("Message", data);
        assertEquals(data, response.getData());
    }

    @Test
    void isSuccess_returnsTrueForOk() {
        ServerResponse<String> response = ServerResponse.ok("Message", "data");
        assertTrue(response.isSuccess());
    }

    @Test
    void isSuccess_returnsFalseForError() {
        ServerResponse<Object> response = ServerResponse.error("Error");
        assertFalse(response.isSuccess());
    }

    @Test
    void ok_withInteger_storesCorrectly() {
        ServerResponse<Integer> response = ServerResponse.ok("Count", 42);
        
        assertTrue(response.isSuccess());
        assertEquals(42, response.getData());
    }

    @Test
    void ok_withList_storesCorrectly() {
        java.util.List<String> list = java.util.List.of("a", "b", "c");
        ServerResponse<java.util.List<String>> response = ServerResponse.ok("Items", list);
        
        assertTrue(response.isSuccess());
        assertEquals(3, response.getData().size());
    }

    @Test
    void error_withNullMessage_storesNull() {
        ServerResponse<Object> response = ServerResponse.error(null);
        
        assertFalse(response.isSuccess());
        assertNull(response.getMessage());
    }
}
