package be.quodlibet.dndgenerate;

import java.io.BufferedReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class PromptController {
    private static final String DNDGENERATE_PROMPT_STORY = System.getenv("DNDGENERATE_PROMPT_STORY") != null && !System.getenv("DNDGENERATE_PROMPT_STORY").isEmpty() 
    ? System.getenv("DNDGENERATE_PROMPT_STORY") 
    : "static/prompts/promptv9.txt";
    private static final String DNDGENERATE_PROMPT_IMAGE = System.getenv("DNDGENERATE_PROMPT_IMAGE") != null && !System.getenv("DNDGENERATE_PROMPT_IMAGE").isEmpty() 
    ? System.getenv("DNDGENERATE_PROMPT_IMAGE") 
    : "static/prompts/image.txt";
    
    @GetMapping("/story/{type}")
    public ResponseEntity<String> getPrompt(@PathVariable("type") String type) {
        
        
        return ResponseEntity.ok(getPromptValue(type));
        
    }
    public static String getPromptValue(String type)
    {
        switch(type)
        {
            case "story":  return readFileAsString(DNDGENERATE_PROMPT_STORY);
            case "image":  return readFileAsString(DNDGENERATE_PROMPT_IMAGE);
        }
        return "ERROR:prompt type " + type + " not supported";
    }
    public static String readFileAsString(String filePath) {
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
}
