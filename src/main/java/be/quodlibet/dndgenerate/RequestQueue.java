
package be.quodlibet.dndgenerate;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

public class RequestQueue {
    private ConcurrentLinkedQueue<RequestData> queue = new ConcurrentLinkedQueue<>();

    public void add(RequestData data) {
        queue.add(data);
    }

    public RequestData poll() {
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }

    public Queue<RequestData> getQueue() {
        return new ConcurrentLinkedQueue<>(queue);
    }

    // Other necessary methods...
}
