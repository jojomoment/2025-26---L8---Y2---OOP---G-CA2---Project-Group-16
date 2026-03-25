package musichub.domain;

public class Studio

{
    private int studio_id;
    private String location_name ;
    private int room_capacity;
    private double hourly_rate;


    public Studio ( int studio_id, String  location_name, int room_capacity,  double hourly_rate)
    {

        // error handling

        if (studio_id <=0)
            throw new IllegalArgumentException("studio id required");


        if (location_name == null || location_name.isBlank())
            throw new IllegalArgumentException("location name required");

        if (room_capacity <=0)
            throw new IllegalArgumentException("room capacity required");

        if (hourly_rate <=0)
            throw new IllegalArgumentException("hourly rate required");


        // works but not needed for stage 1
//        if (audioFile == null || audioFile.length == 0)
//            throw new IllegalArgumentException("Audio file is required");


        //assign variables
        this.studio_id = studio_id;
        this.location_name = location_name;
        this.room_capacity = room_capacity;
        this.hourly_rate = hourly_rate;



    }

    //getters
    public int getStudio_id()
    {
        return studio_id;
    }

    public String getLocation_name()
    {
        return location_name;
    }

    public int getRoom_capacity()
    {
        return room_capacity;
    }

    public double getHourly_rate()
    {
        return hourly_rate;
    }



    //setters



    @Override
    public String toString() {
        return "Music Studio  {studio id =" + studio_id
                + ", location  name ='" + location_name + "'"
                + ", room capactiy =" + room_capacity
                + ", hourly rate  =" + hourly_rate
                ;
    }
}
