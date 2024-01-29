package be.quodlibet.dndgenerate;

import java.io.BufferedReader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author dries
 */
@RestController
public class PromptController 
{
    private  final ResourceLoader resourceLoader;
    @Autowired
    public PromptController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private static final String DNDGENERATE_PROMPT_STORY = System.getenv("DNDGENERATE_PROMPT_STORY") != null && !System.getenv("DNDGENERATE_PROMPT_STORY").isEmpty()
            ? System.getenv("DNDGENERATE_PROMPT_STORY")
            : "defaultStory";
    private static final String DNDGENERATE_PROMPT_IMAGE = System.getenv("DNDGENERATE_PROMPT_IMAGE") != null && !System.getenv("DNDGENERATE_PROMPT_IMAGE").isEmpty()
            ? System.getenv("DNDGENERATE_PROMPT_IMAGE")
            : "defaultImage";

    private static String getDefault(String type) {
        String path = "";
        try {
            switch (type) {
                case "story": {
                    System.out.println(new ClassPathResource("static/prompts/promptv9.txt").exists());
                    path =  new ClassPathResource("static/prompts/promptv9.txt").getFile().getAbsolutePath();

                }

                case "image":
                    path =  new ClassPathResource("static/prompts/image.txt").getFile().getAbsolutePath();
            }
        } catch (IOException ex) {
            System.out.println("Unable to load default prompt path.");
        }
        System.out.println(type + " Prompt : " + path);
        return path;
    }

    @GetMapping("/prompt/{type}")
    public ResponseEntity<String> getPrompt(@PathVariable("type") String type) {

        return ResponseEntity.ok(getPromptValue(type));

    }

    public  String getPromptValue(String type) {
        switch (type) {
            case "story":
                return readFileAsString(DNDGENERATE_PROMPT_STORY);
            case "image":
                return readFileAsString(DNDGENERATE_PROMPT_IMAGE);
        }
        return "ERROR:prompt type " + type + " not supported";
    }

    public  String readFileAsString(String filePath) {
        //this.resourceLoader = resourceLoader;
        if(filePath.equals("defaultStory"))
        {
            try {
                return readFileFromClasspath("static/prompt/promptv9.txt");
            } catch (IOException ex) {
                Logger.getLogger(PromptController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        if(filePath.equals("defaultImage"))
        {
            try {
                return readFileFromClasspath("static/prompt/image.txt");
            } catch (IOException ex) {
                Logger.getLogger(PromptController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                contentBuilder.append(currentLine).append("\n");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found - " + filePath);
            return "";
        } catch (IOException e) {
            System.out.println("Error reading file - " + filePath);
            return "";
        }
        return contentBuilder.toString();
    }
    private  String readFileFromClasspath(String path) throws IOException {
        System.out.println("Classpath : " +System.getProperty("java.class.path"));
        Resource resource = resourceLoader.getResource("classpath:" + path);
        StringBuilder contentBuilder = new StringBuilder();

        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }
        return contentBuilder.toString();
    }
    

}
