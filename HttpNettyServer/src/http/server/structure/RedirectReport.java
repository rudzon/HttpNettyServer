package http.server.structure;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author rudzon
 */
public class RedirectReport {
    private static ConcurrentHashMap<String, AtomicLong> redirectMap =
            new ConcurrentHashMap<>();

    private static AtomicLong overallRedirectCount = new AtomicLong(0);
  
    public static void incrementRedirectCounter(String url) {
        overallRedirectCount.incrementAndGet();
        
        AtomicLong redirectCount = redirectMap.get(url);
        if(redirectCount != null){
            redirectCount.incrementAndGet();
        }else{
            redirectMap.put(url, new AtomicLong(1));
        }
    }
    
    public static LinkedList<Map.Entry<String, AtomicLong>> getRedirectInfo() {
        return new LinkedList(redirectMap.entrySet());
    }
}
