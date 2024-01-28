package be.quodlibet.dndgenerate;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class PdfController {
    private static final String PDF_PATH = System.getenv("DNDGENERATE_PDF_FOLDER") != null && !System.getenv("DNDGENERATE_PDF_FOLDER").isEmpty() 
    ? System.getenv("DNDGENERATE_PDF_FOLDER") 
    : System.getProperty("java.io.tmpdir");
    @GetMapping("/pdf/{filename}")
    public ResponseEntity<Resource> getPdf(@PathVariable("filename") String filename) {
        Path pdfPath = Paths.get(PDF_PATH, filename);

        if (!pdfPath.getFileName().toString().toLowerCase().endsWith(".pdf")) {
            return ResponseEntity.badRequest().body(null);
        }

        File file = pdfPath.toFile();

        if (file.exists()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new FileSystemResource(file));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
