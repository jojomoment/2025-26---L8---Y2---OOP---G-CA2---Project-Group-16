package  musichub.domain;

public class MusicTrack {

    private int songId;
    private String songTitle;
    private int BPM;
    private double durationInSeconds;

    private  byte[] audioFile; // BLOB field - excluded from JSON
    private String fileName;
    private String contentType;
    private int fileSize;


    // No-arg constructor required by Gson
    MusicTrack() {}

    // 4-arg constructor (backward compatibility)
    public MusicTrack( int songId, String  songTitle, int BPM,  double durationInSeconds)
    {
        this(songId, songTitle, BPM, durationInSeconds, null, null, null, 0);
    }

    // 5-arg constructor (backward compatibility with audioFile)
    public MusicTrack( int songId, String  songTitle, int BPM,  double durationInSeconds, byte[] audioFile)
    {
        this(songId, songTitle, BPM, durationInSeconds, audioFile, null, null, 0);
    }

    // Full 8-arg constructor
    public MusicTrack( int songId, String  songTitle, int BPM,  double durationInSeconds,
                       byte[] audioFile, String fileName, String contentType, int fileSize)
    {
        // error handling
        if (songId < 0)
            throw new IllegalArgumentException("song id cannot be negative");

        if (songTitle == null || songTitle.isBlank())
            throw new IllegalArgumentException("song title is required");

        if (BPM <=0)
            throw new IllegalArgumentException("BPM is required");

        if (durationInSeconds <=0)
            throw new IllegalArgumentException("duration in seconds  is required");

        //assign variables
        this.songId = songId;
        this.songTitle = songTitle;
        this.BPM = BPM;
        this.durationInSeconds = durationInSeconds;
        this.audioFile = audioFile;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
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

    public String getFileName()
    {
        return fileName;
    }

    public String getContentType()
    {
        return contentType;
    }

    public int getFileSize()
    {
        return fileSize;
    }

    //setters
    public void setAudioFile(byte[] audioFile)
    {
        this.audioFile = audioFile;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    public void setFileSize(int fileSize)
    {
        this.fileSize = fileSize;
    }

    public void setBPM(int newBPM) {
        if (newBPM <= 0)
            throw new IllegalArgumentException("BPM must be positive");
        this.BPM = newBPM;
    }

    public void setDurationInSeconds(double newDuration) {
        if (newDuration <= 0)
            throw new IllegalArgumentException("duration must be positive");
        this.durationInSeconds = newDuration;
    }

    public void setSongTitle(String newTitle) {
        if (newTitle == null || newTitle.isBlank())
            throw new IllegalArgumentException("song title is required");
        this.songTitle = newTitle;
    }

    @Override
    public String toString() {
        return "MusicTrack {songId=" + songId
                + ", songTitle='" + songTitle + "'"
                + ", BPM=" + BPM
                + ", durationInSeconds=" + durationInSeconds
                + ", audioFileLength=" + (audioFile != null ? audioFile.length : 0)
                + ", fileName='" + fileName + "'"
                + ", contentType='" + contentType + "'"
                + ", fileSize=" + fileSize
                + "}";
    }
}

