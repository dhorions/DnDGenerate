package be.quodlibet.dndgenerate;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class TextController {

   @PostMapping("/process_text")
    public ResponseEntity<String> processText(@RequestBody String text,@RequestParam(name = "title") String title) {
       return PdfGenerator.generatePdf(text,title) ;
    }

    
}
