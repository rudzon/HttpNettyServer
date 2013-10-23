package http.server.structure;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author rudzon
 */
public class LastAccessReport {

    private static final int ENTRIES_QUANTITY = 16;
    private static LinkedBlockingQueue<LastAccessRecord> lastAccessQueue =
            new LinkedBlockingQueue<>(ENTRIES_QUANTITY);

    public static void push(LastAccessRecord lastAccessRecord) {
        synchronized (LastAccessReport.class) {
            while (!lastAccessQueue.offer(lastAccessRecord)) {
                lastAccessQueue.poll();
            }
        }
    }
        
    public static List<LastAccessRecord> getLastAccessInfo(){
        return new LinkedList<>(lastAccessQueue);
    }
}
