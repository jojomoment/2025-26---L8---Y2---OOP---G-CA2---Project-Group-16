package  musichub.domain;

public class MusicProducer {


    private int producer_id;
    private String stage_name ;
    private int tracks_uploaded;
    private double average_rating;


    public MusicProducer( int producer_id, String  stage_name, int tracks_uploaded,  double average_rating)
    {

        // error handling

        if (producer_id <=0)
            throw new IllegalArgumentException("producer id   is required");


        if (stage_name == null || stage_name.isBlank())
            throw new IllegalArgumentException("stage name  is required");

        if (tracks_uploaded <=0)
            throw new IllegalArgumentException("uplodaded tracks   required");

        if (average_rating <=0)
            throw new IllegalArgumentException("average rating  required");


        // works but not needed for stage 1
//        if (audioFile == null || audioFile.length == 0)
//            throw new IllegalArgumentException("Audio file is required");


        //assign variables
        this.producer_id = producer_id;
        this.stage_name = stage_name;
        this.tracks_uploaded = tracks_uploaded;
        this.average_rating = average_rating;



    }

    //getters
    public int getProducer_id()
    {
        return producer_id;
    }

    public String getStage_name()
    {
        return stage_name;
    }

    public int getTracks_uploaded()
    {
        return tracks_uploaded;
    }

    public double getAverage_rating()
    {
        return average_rating;
    }



    //setters



    @Override
    public String toString() {
        return "Music Producer {producerId =" + producer_id
                + ", stage name ='" + stage_name + "'"
                + ", Total uploaded tracks=" + tracks_uploaded
                + ", average rating =" + average_rating
                ;
    }
}