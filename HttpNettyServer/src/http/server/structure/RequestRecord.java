package http.server.structure;

import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author rudzon
 */
public class RequestRecord {
    
    private String ip;
    private AtomicLong requestCount;
    private AtomicLong lastRequestTime;
    
    
    public RequestRecord(String ip, long requestCount, long lastRequestTime){
        this.ip = ip;
        this.requestCount = new AtomicLong(requestCount);
        this.lastRequestTime = new AtomicLong(lastRequestTime);
    }
    
    public RequestRecord(String ip){
        this(ip, 0, System.currentTimeMillis());
    }

    public String getIp() {
        return ip;
    } 
        
    public long getRequestCount() {
        return requestCount.get();
    }

    public void setRequestCount(long requestCount) {
        this.requestCount.set(requestCount);
    }
    
    public void incrementRequestCount(){
        this.requestCount.incrementAndGet();
    }

    public long getLastRequestTime() {
        return lastRequestTime.get();
    }

    public void setLastRequestTime(long lastRequestTime) {
        this.lastRequestTime.set(lastRequestTime);
    }
    
}
