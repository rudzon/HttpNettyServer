package http.server.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author rudzon
 */
public class RequestReport {

    private static ConcurrentHashMap<String, RequestRecord> requestMap =
            new ConcurrentHashMap<>();
    private static AtomicLong overallRequestCount = new AtomicLong(0);

    public static void incrementRequestCounter(String ip) {
        overallRequestCount.incrementAndGet();
        RequestRecord requestRecord = requestMap.get(ip);
        if(requestRecord != null){
            requestRecord.incrementRequestCount();
            requestRecord.setLastRequestTime(System.currentTimeMillis());
            requestMap.replace(ip, requestRecord);
        }else{
            requestMap.put(ip, new RequestRecord(ip, 1, System.currentTimeMillis()));
        }
        
    }

    public static int getHitCount() {
        return requestMap.size();
    }

    public static long getRequestCount() {
        return overallRequestCount.get();
    }

    public static LinkedList<Entry<String,RequestRecord>> getRequestInfo() {
        return new LinkedList(requestMap.entrySet());
    }
}
