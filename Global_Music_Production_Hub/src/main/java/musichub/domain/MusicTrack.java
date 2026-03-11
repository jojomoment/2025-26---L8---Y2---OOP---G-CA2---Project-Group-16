package  musichub.domain;

public class MusicTrack {

    private int songId;
    private String songTitle;
    private int BPM;
    private double durationInSeconds;
    private byte[] audioFile; // BLOB field

    public MusicTrack( int songId, String  songTitle, int BPM,  double durationInSeconds) //, byte[] audioFile) need to add
    {

        // error handling

        if (songId <=0)
            throw new IllegalArgumentException("song id  is required");


        if (songTitle == null || songTitle.isBlank())
            throw new IllegalArgumentException("song title is required");

        if (BPM <=0)
            throw new IllegalArgumentException("BPM is required");

        if (durationInSeconds <=0)
            throw new IllegalArgumentException("duration in seconds  is required");


        // works but not needed for stage 1
//        if (audioFile == null || audioFile.length == 0)
//            throw new IllegalArgumentException("Audio file is required");


        //assign variables
        this.songId = songId;
        this.songTitle = songTitle;
        this.BPM = BPM;
        this.durationInSeconds = durationInSeconds;
        this.audioFile = audioFile;


    }

    //getters
    public int getSongId()
    {
        return songId;
    }

    public String getSongTitle()
    {
        return songTitle;
    }

    public int getBPM()
    {
        return BPM;
    }

    public double getDurationInSeconds()
    {
        return durationInSeconds;
    }

    public byte[] getAudioFile()
    {
        return audioFile;
    }

    //setters



    @Override
    public String toString() {
        return "MusicTrack {songId=" + songId
                + ", songTitle='" + songTitle + "'"
                + ", BPM=" + BPM
                + ", durationInSeconds=" + durationInSeconds
                + ", audioFileLength=" + (audioFile != null ? audioFile.length : 0)
                + "}";
    }
}