package  musichub.domain;

public class MusicTrack {

    private int song_id;
    private String song_title;
    private int BPM;
    private double duration_in_seconds;
    private byte[] audio_file; // BLOB field

    public MusicTrack( int song_id, String  song_title, int BPM,  double duration_in_seconds, byte[] audio_file)
    {

        // error handling

        if (song_id <=0)
            throw new IllegalArgumentException("song id  is required");


        if (song_title == null || song_title.isBlank())
            throw new IllegalArgumentException("song title is required");

        if (BPM <=0)
            throw new IllegalArgumentException("BPM is required");

        if (duration_in_seconds <=0)
            throw new IllegalArgumentException("duration in seconds  is required");


        // works but not needed for stage 1
//        if (audioFile == null || audioFile.length == 0)
//            throw new IllegalArgumentException("Audio file is required");


        //assign variables
        this.song_id = song_id;
        this.song_title = song_title;
        this.BPM = BPM;
        this.duration_in_seconds = duration_in_seconds;
        this.audio_file = audio_file;


    }

    //getters
    public int getSongId()
    {
        return song_id;
    }

    public String getSongTitle()
    {
        return song_title;
    }

    public int getBPM()
    {
        return BPM;
    }

    public double getDurationInSeconds()
    {
        return duration_in_seconds;
    }

    public byte[] getAudioFile()
    {
        return audio_file;
    }

    //setters



    @Override
    public String toString() {
        return "MusicTrack {songId=" + song_id
                + ", songTitle='" + song_title + "'"
                + ", BPM=" + BPM
                + ", durationInSeconds=" + duration_in_seconds
                + ", audioFileLength=" + (audio_file != null ? audio_file.length : 0)
                + "}";
    }
}
