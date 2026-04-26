package musichub.domain;

public class UploadRequest {

    // === Fields ===
    private String fSongTitle;
    private int fBpm;
    private double fDurationInSeconds;

    private String fFileName;
    private String fContentType;
    private int fFileSize;
    private String fFileData; // Base64 encoded string

    // === Constructors ===

    public UploadRequest() {}

    public UploadRequest(String songTitle, int bpm, double durationInSeconds,
                         String fileName, String contentType,
                         int fileSize, String fileData) {

        if (songTitle == null || songTitle.isBlank()) {
            throw new IllegalArgumentException("Song title required");
        }

        if (bpm <= 0) {
            throw new IllegalArgumentException("BPM must be positive");
        }

        if (durationInSeconds <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }

        this.fSongTitle = songTitle;
        this.fBpm = bpm;
        this.fDurationInSeconds = durationInSeconds;
        this.fFileName = fileName;
        this.fContentType = contentType;
        this.fFileSize = fileSize;
        this.fFileData = fileData;
    }

    // === Public API ===

    public String getSongTitle() {
        return fSongTitle;
    }

    public int getBpm() {
        return fBpm;
    }

    public double getDurationInSeconds() {
        return fDurationInSeconds;
    }

    public String getFileName() {
        return fFileName;
    }

    public String getContentType() {
        return fContentType;
    }

    public int getFileSize() {
        return fFileSize;
    }

    public String getFileData() {
        return fFileData;
    }
}