package musichub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StudioTest {

    @Test
    void constructor_initializesAllFields() {
        Studio studio = new Studio(1, "Test Studio", 10, 50.0);
        
        assertEquals(1, studio.getStudioId());
        assertEquals("Test Studio", studio.getLocationName());
        assertEquals(10, studio.getRoomCapacity());
        assertEquals(50.0, studio.getHourlyRate());
    }

    @Test
    void setters_updateFields() {
        Studio studio = new Studio(1, "Initial Studio", 5, 25.0);
        
        studio.setStudioId(5);
        studio.setLocationName("Updated Studio");
        studio.setRoomCapacity(20);
        studio.setHourlyRate(75.0);
        
        assertEquals(5, studio.getStudioId());
        assertEquals("Updated Studio", studio.getLocationName());
        assertEquals(20, studio.getRoomCapacity());
        assertEquals(75.0, studio.getHourlyRate());
    }

    @Test
    void setLocationName_throwsException_whenNull() {
        Studio studio = new Studio(1, "Test", 10, 50.0);
        assertThrows(IllegalArgumentException.class, () -> studio.setLocationName(null));
    }

    @Test
    void setLocationName_throwsException_whenBlank() {
        Studio studio = new Studio(1, "Test", 10, 50.0);
        assertThrows(IllegalArgumentException.class, () -> studio.setLocationName("   "));
    }

    @Test
    void setRoomCapacity_throwsException_whenZero() {
        Studio studio = new Studio(1, "Test", 10, 50.0);
        assertThrows(IllegalArgumentException.class, () -> studio.setRoomCapacity(0));
    }

    @Test
    void setRoomCapacity_throwsException_whenNegative() {
        Studio studio = new Studio(1, "Test", 10, 50.0);
        assertThrows(IllegalArgumentException.class, () -> studio.setRoomCapacity(-5));
    }

    @Test
    void setHourlyRate_throwsException_whenZero() {
        Studio studio = new Studio(1, "Test", 10, 50.0);
        assertThrows(IllegalArgumentException.class, () -> studio.setHourlyRate(0.0));
    }

    @Test
    void setHourlyRate_throwsException_whenNegative() {
        Studio studio = new Studio(1, "Test", 10, 50.0);
        assertThrows(IllegalArgumentException.class, () -> studio.setHourlyRate(-10.0));
    }

    @Test
    void equals_returnsTrueForSameId() {
        Studio studio1 = new Studio(1, "Studio A", 10, 50.0);
        Studio studio2 = new Studio(1, "Studio B", 20, 60.0);
        
        assertEquals(studio1, studio2);
    }

    @Test
    void equals_returnsFalseForDifferentIds() {
        Studio studio1 = new Studio(1, "Studio A", 10, 50.0);
        Studio studio2 = new Studio(2, "Studio A", 10, 50.0);
        
        assertNotEquals(studio1, studio2);
    }

    @Test
    void equals_returnsFalseForNonStudioObject() {
        Studio studio = new Studio(1, "Studio A", 10, 50.0);
        assertNotEquals(studio, "not a studio");
    }

    @Test
    void toString_containsStudioInfo() {
        Studio studio = new Studio(1, "Test Studio", 10, 50.0);
        String str = studio.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("1") || str.contains("Test Studio"));
    }

    @Test
    void hashCode_sameForSameId() {
        Studio studio1 = new Studio(1, "Studio A", 10, 50.0);
        Studio studio2 = new Studio(1, "Studio B", 20, 60.0);
        
        assertEquals(studio1.hashCode(), studio2.hashCode());
    }
}
