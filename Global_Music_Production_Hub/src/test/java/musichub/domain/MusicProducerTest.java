package musichub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MusicProducerTest {

    @Test
    void constructor_initializesAllFields() {
        MusicProducer producer = new MusicProducer(1, "DJ Nova", 25, 4.5);
        
        assertEquals(1, producer.getProducerId());
        assertEquals("DJ Nova", producer.getStageName());
        assertEquals(25, producer.getTracksUploaded());
        assertEquals(4.5, producer.getAverageRating());
    }

    @Test
    void setProducerId_updatesField() {
        MusicProducer producer = new MusicProducer(1, "DJ Nova", 25, 4.5);
        
        producer.setProducerId(5);
        assertEquals(5, producer.getProducerId());
    }

    @Test
    void setStageName_updatesField() {
        MusicProducer producer = new MusicProducer(1, "DJ Nova", 25, 4.5);
        
        producer.setStageName("BeatMaster");
        assertEquals("BeatMaster", producer.getStageName());
    }

    @Test
    void setTracksUploaded_updatesField() {
        MusicProducer producer = new MusicProducer(1, "DJ Nova", 25, 4.5);
        
        producer.setTracksUploaded(50);
        assertEquals(50, producer.getTracksUploaded());
    }

    @Test
    void setAverageRating_updatesField() {
        MusicProducer producer = new MusicProducer(1, "DJ Nova", 25, 4.5);
        
        producer.setAverageRating(4.8);
        assertEquals(4.8, producer.getAverageRating());
    }

    @Test
    void equals_returnsTrueForSameProducerId() {
        MusicProducer producer1 = new MusicProducer(1, "DJ Nova", 25, 4.5);
        MusicProducer producer2 = new MusicProducer(1, "BeatMaster", 40, 4.8);
        
        assertEquals(producer1, producer2);
    }

    @Test
    void equals_returnsFalseForDifferentProducerIds() {
        MusicProducer producer1 = new MusicProducer(1, "DJ Nova", 25, 4.5);
        MusicProducer producer2 = new MusicProducer(2, "DJ Nova", 25, 4.5);
        
        assertNotEquals(producer1, producer2);
    }

    @Test
    void equals_returnsTrueForSelfReference() {
        MusicProducer producer = new MusicProducer(1, "DJ Nova", 25, 4.5);
        assertEquals(producer, producer);
    }

    @Test
    void equals_returnsFalseForNonProducerObject() {
        MusicProducer producer = new MusicProducer(1, "DJ Nova", 25, 4.5);
        assertNotEquals(producer, "not a producer");
    }

    @Test
    void toString_containsProducerInfo() {
        MusicProducer producer = new MusicProducer(1, "DJ Nova", 25, 4.5);
        String str = producer.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("1") || str.contains("DJ Nova"));
    }

    @Test
    void hashCode_sameForSameProducerId() {
        MusicProducer producer1 = new MusicProducer(1, "DJ Nova", 25, 4.5);
        MusicProducer producer2 = new MusicProducer(1, "BeatMaster", 40, 4.8);
        
        assertEquals(producer1.hashCode(), producer2.hashCode());
    }
}
