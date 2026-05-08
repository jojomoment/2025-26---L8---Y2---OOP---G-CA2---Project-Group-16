package musichub.domain;

import java.util.Arrays;
import java.util.Objects;

/**
 * DTO representing a music track entity.
 *
 * @author Your Name (primary)
 */
public class MusicTrack
{
    // === Fields ===

    private int fSongId;

    private String fSongTitle;

    private int fBpm;

    private double fDurationInSeconds;

    private byte[] fAudioFile;

    private String fFileName;

    private String fContentType;

    private int fFileSize;

    // === Constructors ===

    // Creates: an empty music track object (required for JSON deserialization)
    public MusicTrack()
    {
        this.fSongId = 0;
        this.fSongTitle = "";
        this.fBpm = 1;
        this.fDurationInSeconds = 1.0;

        this.fAudioFile = null;
        this.fFileName = "";
        this.fContentType = "";
        this.fFileSize = 0;
    }

    // Creates: a music track object without binary data
    public MusicTrack(
            int songId,
            String songTitle,
            int bpm,
            double durationInSeconds)
    {
        setSongId(songId);
        setSongTitle(songTitle);
        setBpm(bpm);
        setDurationInSeconds(durationInSeconds);

        this.fAudioFile = null;
        this.fFileName = "";
        this.fContentType = "";
        this.fFileSize = 0;
    }

    // Creates: a full music track including binary file data
    public MusicTrack(
            int songId,
            String songTitle,
            int bpm,
            double durationInSeconds,
            byte[] audioFile,
            String fileName,
            String contentType,
            int fileSize)
    {
        setSongId(songId);
        setSongTitle(songTitle);
        setBpm(bpm);
        setDurationInSeconds(durationInSeconds);

        setAudioFile(audioFile);
        setFileName(fileName);
        setContentType(contentType);
        setFileSize(fileSize);
    }

    // === Public API ===

    // Gets: song ID
    public int getSongId()
    {
        return fSongId;
    }

    // Gets: song title
    public String getSongTitle()
    {
        return fSongTitle;
    }

    // Gets: BPM
    public int getBpm()
    {
        return fBpm;
    }

    // Gets: duration in seconds
    public double getDurationInSeconds()
    {
        return fDurationInSeconds;
    }

    // Gets: audio file bytes
    public byte[] getAudioFile()
    {
        return fAudioFile;
    }

    // Gets: file name
    public String getFileName()
    {
        return fFileName;
    }

    // Gets: content type
    public String getContentType()
    {
        return fContentType;
    }

    // Gets: file size
    public int getFileSize()
    {
        return fFileSize;
    }

    // Sets: song ID
    public void setSongId(int songId)
    {
        if (songId < 0)
        {
            throw new IllegalArgumentException("Song ID cannot be negative");
        }

        this.fSongId = songId;
    }

    // Sets: song title
    public void setSongTitle(String songTitle)
    {
        if (songTitle == null || songTitle.isBlank())
        {
            throw new IllegalArgumentException("Song title is required");
        }

        this.fSongTitle = songTitle.trim();
    }

    // Sets: BPM
    public void setBpm(int bpm)
    {
        if (bpm <= 0)
        {
            throw new IllegalArgumentException("BPM must be positive");
        }

        this.fBpm = bpm;
    }

    // Sets: duration
    public void setDurationInSeconds(double durationInSeconds)
    {
        if (durationInSeconds <= 0)
        {
            throw new IllegalArgumentException("Duration must be positive");
        }

        this.fDurationInSeconds = durationInSeconds;
    }

    // Sets: audio file
    public void setAudioFile(byte[] audioFile)
    {
        this.fAudioFile = audioFile;
    }

    // Sets: file name
    public void setFileName(String fileName)
    {
        this.fFileName = fileName;
    }

    // Sets: content type
    public void setContentType(String contentType)
    {
        this.fContentType = contentType;
    }

    // Sets: file size
    public void setFileSize(int fileSize)
    {
        if (fileSize < 0)
        {
            throw new IllegalArgumentException("File size cannot be negative");
        }

        this.fFileSize = fileSize;
    }

    // === Helpers ===

    // Checks: whether this track has binary data
    public boolean hasAudioFile()
    {
        return fAudioFile != null && fAudioFile.length > 0;
    }

    public boolean hasValidFileMetadata()
    {
        return fFileName != null && !fFileName.isBlank()
                && fContentType != null && !fContentType.isBlank()
                && fFileSize > 0;
    }

    public void clearAudioFile()
    {
        this.fAudioFile = null;
        this.fFileName = "";
        this.fContentType = "";
        this.fFileSize = 0;
    }


    // === Overrides ===

    @Override
    public int hashCode()
    {
        int result = Objects.hash(
                fSongId,
                fSongTitle,
                fBpm,
                fDurationInSeconds,
                fFileName,
                fContentType,
                fFileSize);

        result = 31 * result + Arrays.hashCode(fAudioFile);

        return result;
    }

    @Override
    public String toString()
    {
        return "MusicTrack{" +
                "songId=" + fSongId +
                ", songTitle='" + fSongTitle + '\'' +
                ", bpm=" + fBpm +
                ", durationInSeconds=" + fDurationInSeconds +
                ", fileName='" + fFileName + '\'' +
                ", contentType='" + fContentType + '\'' +
                ", fileSize=" + fFileSize +
                '}';
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof MusicTrack other))
        {
            return false;
        }

        return fSongId == other.fSongId;
    }


}