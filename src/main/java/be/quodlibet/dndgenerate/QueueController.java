
package be.quodlibet.dndgenerate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/queue")
public class QueueController {

    @Autowired
    private QueueService queueService;

    // ... existing endpoints ...

    @GetMapping("/requests")
    public List<RequestData> getAllRequests() {
        return queueService.listQueue();
    }
}
