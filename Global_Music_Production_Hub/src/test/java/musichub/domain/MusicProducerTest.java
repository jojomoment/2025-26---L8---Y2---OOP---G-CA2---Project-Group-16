package musichub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MusicProducerTest {

    @Test
    void constructor_initializesAllFields() {
        MusicProducer producer = new MusicProducer(1, "DJ Nova", 25, 4.5);
        
        assertEquals(1, producer.getfProducerId());
        assertEquals("DJ Nova", producer.getfStageName());
        assertEquals(25, producer.getfTracksUploaded());
        assertEquals(4.5, producer.getfAverageRating());
    }

    @Test
    void setfStageName_updatesField() {
        MusicProducer producer = new MusicProducer(1, "DJ Nova", 25, 4.5);
        
        producer.setfStageName("BeatMaster");
        assertEquals("BeatMaster", producer.getfStageName());
    }

    @Test
    void setfTracksUploaded_updatesField() {
        MusicProducer producer = new MusicProducer(1, "DJ Nova", 25, 4.5);
        
        producer.setfTracksUploaded(50);
        assertEquals(50, producer.getfTracksUploaded());
    }

    @Test
    void setfAverageRating_updatesField() {
        MusicProducer producer = new MusicProducer(1, "DJ Nova", 25, 4.5);
        
        producer.setfAverageRating(4.8);
        assertEquals(4.8, producer.getfAverageRating());
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
