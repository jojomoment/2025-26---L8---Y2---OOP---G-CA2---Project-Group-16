package musichub.domain;

/**
 * Represents a recording studio in the system.
 * Stores location, capacity, and pricing details.
 *
 * @author Your Name
 */
public class Studio
{
    // === Fields ===

    private int fStudioId;

    private String fLocationName;

    private int fRoomCapacity;

    private double fHourlyRate;

    // === Constructors ===

    // Creates: Studio entity with validated fields
    public Studio(
            int studioId,
            String locationName,
            int roomCapacity,
            double hourlyRate)
    {
        setStudioId(studioId);
        setLocationName(locationName);
        setRoomCapacity(roomCapacity);
        setHourlyRate(hourlyRate);
    }

    // === Public API ===

    // Gets: studio ID
    public int getStudioId()
    {
        return fStudioId;
    }

    // Gets: location name
    public String getLocationName()
    {
        return fLocationName;
    }

    // Gets: room capacity
    public int getRoomCapacity()
    {
        return fRoomCapacity;
    }

    // Gets: hourly rate
    public double getHourlyRate()
    {
        return fHourlyRate;
    }

    // Sets: studio ID
    public void setStudioId(int studioId)
    {
        if (studioId < 0)
        {
            throw new IllegalArgumentException(
                    "Studio ID cannot be negative");
        }

        this.fStudioId = studioId;
    }

    // Sets: location name
    public void setLocationName(String locationName)
    {
        if (locationName == null || locationName.isBlank())
        {
            throw new IllegalArgumentException(
                    "Location name is required");
        }

        this.fLocationName = locationName.trim();
    }

    // Sets: room capacity
    public void setRoomCapacity(int roomCapacity)
    {
        if (roomCapacity < 1)
        {
            throw new IllegalArgumentException(
                    "Room capacity must be at least 1");
        }

        this.fRoomCapacity = roomCapacity;
    }

    // Sets: hourly rate
    public void setHourlyRate(double hourlyRate)
    {
        if (hourlyRate <= 0)
        {
            throw new IllegalArgumentException(
                    "Hourly rate must be positive");
        }

        this.fHourlyRate = hourlyRate;
    }

    // === Overrides ===

    @Override
    public String toString()
    {
        return "Studio{" +
                "studioId=" + fStudioId +
                ", locationName='" + fLocationName + '\'' +
                ", roomCapacity=" + fRoomCapacity +
                ", hourlyRate=" + fHourlyRate +
                '}';
    }

    @Override
    public int hashCode()
    {
        return fStudioId;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof Studio other))
        {
            return false;
        }

        return fStudioId == other.fStudioId;
    }
}