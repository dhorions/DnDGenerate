
package be.quodlibet.dndgenerate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class QueueService {
    private final RequestQueue requestQueue = new RequestQueue();
    private final PdfGenerator pdfGenerator;// = new PdfGenerator();
    private final AtomicInteger nextId = new AtomicInteger(1); // ID generator
    private RequestData currentlyProcessing;
    public void addToQueue(RequestData data) {
        data.setId(nextId.getAndIncrement()); // Set unique ID
        requestQueue.add(data);
    }
    @Autowired
    public QueueService(PdfGenerator pdfGenerator) {
        this.pdfGenerator = pdfGenerator;
    }

    @Scheduled(fixedDelay = 60000) // Schedule this method to run every 60,000 milliseconds (1 minute)
    public void processQueue() {
        RequestData data = requestQueue.poll();
        if (data != null) {
            System.out.println("Processing \t" + data.getId() + " " + data.getTitle() + "\t processQueue size : " + requestQueue.size());
            currentlyProcessing = data;
            pdfGenerator.generatePdf(data.getData(), data.getTitle());
            currentlyProcessing = null;
            // Process the data...
        }
        else{
            System.out.println("processQueue is empty");
        }
    }

    public int getQueueSize() {
        return requestQueue.size();
    }

    public List<RequestData> listQueue() {
        return new ArrayList<>(requestQueue.getQueue());
    }
    public RequestData getCurrentlyProcessing() {
        return currentlyProcessing;
    }

    // Other necessary methods...
}
