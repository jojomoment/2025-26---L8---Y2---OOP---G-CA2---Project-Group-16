package musichub.server;
import java.util.Map;

public class ClientRequest 
{
    private String requestType;
    private Map<String, Object> payload;

    public ClientRequest()
    {
        this.requestType = " ";
        this.payload = Map.of();

    }

    public String getRequestType()
    {
        return requestType;
    }

    public void setRequestType(String requestType) 
    {
        this.requestType = requestType;
    }

    public Map<String, Object> getPayload()
    {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) 
    {
        this.payload = payload;
    }

    public String getString(String key) 
    {
        Object v = payload.get(key);
        return v == null ? null : v.toString();
    }

    public int getInt(String key) 
    {
        
        Object v = payload.get(key);
        if (v == null) return -1;
        try
         {
            return Integer.parseInt(v.toString());
        } 
        catch (NumberFormatException e) 
        {
            return -1;
        }
    }
}
