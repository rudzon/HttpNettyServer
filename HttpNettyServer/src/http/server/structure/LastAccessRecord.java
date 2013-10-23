package http.server.structure;

import java.util.concurrent.atomic.AtomicLong;
/**
 * 
 * @author rudzon
 */
public class LastAccessRecord {
       
    private String sourceIP;
    private String uri;
    private long lastRequestTime;
    private long bytesSent;
    private AtomicLong bytesReceived;
    private long speed;

    public LastAccessRecord(){
        this.lastRequestTime = System.currentTimeMillis();
        this.bytesSent = 0;
        this.bytesReceived = new AtomicLong(0);
        this.speed = 0;
    }

    public LastAccessRecord(String sourceIP, String uri, long lastRequestTime, 
            long bytesSent, long bytesReceived, long speed) {
        this();
        this.sourceIP = sourceIP;
        this.uri = uri;
        this.lastRequestTime = lastRequestTime;
        this.bytesSent = bytesSent;
        this.bytesReceived.set(bytesReceived);
        this.speed = speed;
    }
       
    public String getSourceIP() {
        return sourceIP;
    }

    public void setSourceIP(String sourceIP) {
        this.sourceIP = sourceIP;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getTimestamp() {
        return lastRequestTime;
    }

    public void setTimestamp(long lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    public long getBytesReceived() {
        return bytesReceived.get();
    }

    public void setBytesReceived(long bytesReceived) {
        this.bytesReceived.set(bytesReceived);
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }    
}
