package  musichub.domain;


/**
 * Represents a music producer in the system.
 * Stores producer identity, uploaded tracks, and rating.
 *
 *
 */

public class MusicProducer {

//fields
    private int fProducerId;
    private String fStageName;
    private int fTracksUploaded;
    private double fAverageRating;


    //constructor
    public MusicProducer(int fProducerId, String  fStageName, int fTracksUploaded, double fAverageRating)
    {

        // error handling

        if (fProducerId < 0)
            throw new IllegalArgumentException("producer id   is required");


        if (fStageName == null || fStageName.isBlank())
            throw new IllegalArgumentException("stage name  is required");

        if (fTracksUploaded <=0)
            throw new IllegalArgumentException("uplodaded tracks   required");

        if (fAverageRating < 0.0 || fAverageRating > 5.0)
            throw new IllegalArgumentException("average rating  required");


        // works but not needed for stage 1
//        if (audioFile == null || audioFile.length == 0)
//            throw new IllegalArgumentException("Audio file is required");


        //assign variables
        this.fProducerId = fProducerId;
        this.fStageName = fStageName;
        this.fTracksUploaded = fTracksUploaded;
        this.fAverageRating = fAverageRating;



    }

    public MusicProducer()
    {
        this.fProducerId = 0;
        this.fStageName = "";
        this.fTracksUploaded = 0;
        this.fAverageRating = 0.0;
    }

    //getters
    public int getfProducerId()
    {
        return fProducerId;
    }

    public String getfStageName()
    {
        return fStageName;
    }

    public int getfTracksUploaded()
    {
        return fTracksUploaded;
    }

    public double getfAverageRating()
    {
        return fAverageRating;
    }



    //setters
    public void setfStageName(String fStageName)
    {
        if (fStageName == null || fStageName.isBlank())
            throw new IllegalArgumentException("stage name required");

        this.fStageName = fStageName.trim();
    }

    public void setfTracksUploaded(int fTracksUploaded)
    {
        if (fTracksUploaded < 0)
            throw new IllegalArgumentException("tracks uploaded required");

        this.fTracksUploaded = fTracksUploaded;
    }

    public void setfAverageRating(double fAverageRating)
    {
        if (fAverageRating < 0.0 || fAverageRating > 5.0)
            throw new IllegalArgumentException("invalid rating");

        this.fAverageRating = fAverageRating;
    }

    @Override
    public String toString()
    {
        return "Music Producer {producerId =" + fProducerId
                + ", stage name ='" + fStageName + "'"
                + ", Total uploaded tracks=" + fTracksUploaded
                + ", average rating =" + fAverageRating
                ;
    }

    @Override
    public int hashCode()
    {
        return fProducerId;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (!(obj instanceof MusicProducer))
            return false;

        MusicProducer other = (MusicProducer) obj;
        return this.fProducerId == other.fProducerId;
    }
}