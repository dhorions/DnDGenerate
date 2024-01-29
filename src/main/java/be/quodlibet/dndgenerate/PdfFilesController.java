package be.quodlibet.dndgenerate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RestController
public class PdfFilesController {

    private static final String PDF_PATH = System.getenv("DNDGENERATE_PDF_FOLDER") != null && !System.getenv("DNDGENERATE_PDF_FOLDER").isEmpty() 
    ? System.getenv("DNDGENERATE_PDF_FOLDER") 
    : System.getProperty("java.io.tmpdir");     

    @GetMapping("/pdf-files")
    public List<String> listPdfFiles() {
        System.out.println("Looking for pdf folder in : " + PDF_PATH);
        File folder = new File(PDF_PATH);
        List<File> pdfFiles = new ArrayList<>();

        if (folder.exists() && folder.isDirectory()) {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".pdf")) {
                    pdfFiles.add(file);
                }
            }

            // Sort the list by last modified time in descending order
            pdfFiles.sort(Comparator.comparingLong(File::lastModified).reversed());
        }

        // Convert the sorted list of Files to a list of file names
        List<String> fileNames = new ArrayList<>();
        for (File file : pdfFiles) {
            fileNames.add(file.getName());
        }

        return fileNames;
    }
}
