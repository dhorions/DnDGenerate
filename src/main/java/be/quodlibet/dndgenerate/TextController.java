package be.quodlibet.dndgenerate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class TextController {
@Autowired
private QueueService queueService;
   @PostMapping("/process_text")
    public ResponseEntity<String> processText(@RequestBody String text,@RequestParam(name = "title") String title) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDateTime = now.format(formatter);
    if (title.isBlank()) { title = "Untitled Campaign " + formattedDateTime;}
    RequestData requestData = new RequestData(text, title);
    queueService.addToQueue(requestData);
    return ResponseEntity.ok("Your campain '"+requestData.getTitle()+"' is added to the queue ( id " + requestData.getId() + "). There are " + queueService.getQueueSize() + " requests in the queue ") ;
       //return PdfGenerator.generatePdf(text,title) ;
    }

    
}
