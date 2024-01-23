package be.quodlibet.dndgenerate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class PdfFilesController {

    
    private static final String PDF_PATH = System.getenv("DNDGENERATE_PDF_FOLDER");
    @GetMapping("/pdf-files")
    public List<String> listPdfFiles() {
        File folder = new File(PDF_PATH);
        List<String> fileNames = new ArrayList<>();

        if (folder.exists() && folder.isDirectory()) {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".pdf")) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }
}
