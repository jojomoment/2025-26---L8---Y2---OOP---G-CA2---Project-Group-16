package musichub.dao;

import musichub.domain.Studio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StudioDaoTest {

    private jdbcStudioDao dao;
    private int createdId;

    @BeforeEach
    void setUp() {
        dao = new jdbcStudioDao();
        createdId = -1;
    }

    @AfterEach
    void tearDown() throws Exception {
        if (createdId > 0) {
            dao.deleteById(createdId);
        }
    }

    @Test
    void insert_returnsGeneratedId_andRecordExists() throws Exception {
        int id = dao.insert("Test Studio", 10, 50.0);
        createdId = id;

        assertTrue(id > 0);

        Optional<Studio> stored = dao.getStudioById(id);
        assertTrue(stored.isPresent());
        assertEquals("Test Studio", stored.get().getLocationName());
        assertEquals(10, stored.get().getRoomCapacity());
        assertEquals(50.0, stored.get().getHourlyRate());
    }

    @Test
    void insert_throwsException_whenLocationNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.insert(null, 10, 50.0);
        });
    }

    @Test
    void insert_throwsException_whenLocationNameIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.insert("   ", 10, 50.0);
        });
    }

    @Test
    void insert_throwsException_whenRoomCapacityIsZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.insert("Test Studio", 0, 50.0);
        });
    }

    @Test
    void insert_throwsException_whenRoomCapacityIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.insert("Test Studio", -5, 50.0);
        });
    }

    @Test
    void insert_throwsException_whenHourlyRateIsZero() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.insert("Test Studio", 10, 0.0);
        });
    }

    @Test
    void insert_throwsException_whenHourlyRateIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            dao.insert("Test Studio", 10, -25.0);
        });
    }

    @Test
    void getAll_returnsStudiosCollection_nonNull() throws Exception {
        List<Studio> studios = dao.getAll();
        assertNotNull(studios);
        assertTrue(studios.size() >= 0);
    }

    @Test
    void getStudioById_returnsEmptyOptional_whenIdDoesNotExist() throws Exception {
        Optional<Studio> missing = dao.getStudioById(-9999);
        assertTrue(missing.isEmpty());
    }

    @Test
    void getStudioById_returnsEmptyOptional_whenIdIsZero() throws Exception {
        Optional<Studio> missing = dao.getStudioById(0);
        assertTrue(missing.isEmpty());
    }

    @Test
    void getStudioById_returnsEmptyOptional_whenIdIsNegative() throws Exception {
        Optional<Studio> missing = dao.getStudioById(-1);
        assertTrue(missing.isEmpty());
    }

    @Test
    void deleteById_returnsTrue_whenStudioExists() throws Exception {
        int id = dao.insert("Delete Test Studio", 8, 40.0);
        assertTrue(dao.deleteById(id));
        assertTrue(dao.getStudioById(id).isEmpty());
    }

    @Test
    void deleteById_returnsFalse_whenStudioDoesNotExist() throws Exception {
        boolean deleted = dao.deleteById(-9999);
        assertFalse(deleted);
    }

    @Test
    void updateStudio_updatesRow_andGetByIdReflectsChanges() throws Exception {
        createdId = dao.insert("Original Studio", 12, 60.0);

        Studio updated = dao.updateStudio(createdId, "Updated Studio", 15, 75.0);
        assertNotNull(updated);
        assertEquals(createdId, updated.getStudioId());
        assertEquals("Updated Studio", updated.getLocationName());
        assertEquals(15, updated.getRoomCapacity());
        assertEquals(75.0, updated.getHourlyRate());

        Optional<Studio> fetched = dao.getStudioById(createdId);
        assertTrue(fetched.isPresent());
        assertEquals("Updated Studio", fetched.get().getLocationName());
        assertEquals(15, fetched.get().getRoomCapacity());
        assertEquals(75.0, fetched.get().getHourlyRate());
    }

    @Test
    void updateStudio_returnsNull_whenStudioDoesNotExist() throws Exception {
        Studio result = dao.updateStudio(-9999, "NonExistent", 10, 50.0);
        assertNull(result);
    }

    @Test
    void studioRoundTrip_multipleInserts_canRetrieveAll() throws Exception {
        int id1 = dao.insert("Studio A", 10, 50.0);
        int id2 = dao.insert("Studio B", 15, 60.0);
        createdId = id1;

        try {
            List<Studio> all = dao.getAll();
            assertNotNull(all);
            assertTrue(all.stream().anyMatch(s -> s.getStudioId() == id1));
            assertTrue(all.stream().anyMatch(s -> s.getStudioId() == id2));

            Optional<Studio> byId1 = dao.getStudioById(id1);
            assertTrue(byId1.isPresent());
            assertEquals("Studio A", byId1.get().getLocationName());

            Optional<Studio> byId2 = dao.getStudioById(id2);
            assertTrue(byId2.isPresent());
            assertEquals("Studio B", byId2.get().getLocationName());
        } finally {
            dao.deleteById(id2);
        }
    }
}
