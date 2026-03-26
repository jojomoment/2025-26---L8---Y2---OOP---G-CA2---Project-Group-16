package musichub.domain;

public class MusicProducer {

    private int producerId;
    private String stageName;
    private int tracksUploaded;
    private double averageRating;

    public MusicProducer(int producerId, String stageName, int tracksUploaded, double averageRating) {

        // error handling

        if (producerId <= 0)
            throw new IllegalArgumentException("producer id is required");

        if (stageName == null || stageName.isBlank())
            throw new IllegalArgumentException("stage name is required");

        if (tracksUploaded <= 0)
            throw new IllegalArgumentException("uploaded tracks required");

        if (averageRating <= 0)
            throw new IllegalArgumentException("average rating required");

        // assign variables
        this.producerId = producerId;
        this.stageName = stageName;
        this.tracksUploaded = tracksUploaded;
        this.averageRating = averageRating;
    }

    // getters
    public int getProducerId() {
        return producerId;
    }

    public String getStageName() {
        return stageName;
    }

    public int getTracksUploaded() {
        return tracksUploaded;
    }

    public double getAverageRating() {
        return averageRating;
    }

    // setters

    @Override
    public String toString() {
        return "Music Producer {producerId =" + producerId
                + ", stage name ='" + stageName + "'"
                + ", Total uploaded tracks=" + tracksUploaded
                + ", average rating =" + averageRating
                ;
    }
}