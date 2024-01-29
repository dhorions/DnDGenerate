/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package be.quodlibet.dndgenerate;

import be.quodlibet.dndgenerate.openai.ChatResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 *
 * @author dries
 */
@Service
public class PdfGenerator {
     private static final String OPENAI_URL = System.getenv("OPENAI_URL") != null && !System.getenv("OPENAI_URL").isEmpty() 
                                         ? System.getenv("OPENAI_URL") 
                                         : "https://api.openai.com/v1/chat/completions";

    private static final String OPENAI_KEY = System.getenv("OPENAI_API_KEY");
    private static final String PDF_PATH = System.getenv("DNDGENERATE_PDF_FOLDER") != null && !System.getenv("DNDGENERATE_PDF_FOLDER").isEmpty() 
    ? System.getenv("DNDGENERATE_PDF_FOLDER") 
    : System.getProperty("java.io.tmpdir");
    private final DalleImageGenerator dalleImageGenerator;
    private  final ResourceLoader resourceLoader;
    @Autowired
    public PdfGenerator(DalleImageGenerator dalleImageGenerator,ResourceLoader resourceLoader) {
        this.dalleImageGenerator = dalleImageGenerator;
        this.resourceLoader = resourceLoader;
    }
    
    public  ResponseEntity<String> generatePdf(String text, String title)
    {
        
        try {
            
            if (!isValidInput(text)) {
                return ResponseEntity.badRequest().body("Invalid input");
            }
            //Init resources
            copyFileToTemp("pdf/logo_trans.png");
            copyFileToTemp("pdf/style.css");
            //text = text.replaceAll("\"", "'").replaceAll("\n","\\n").replaceAll("\r","");
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost request = new HttpPost(OPENAI_URL);
                
                request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer "+OPENAI_KEY);
                request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                
                //String jsonBody = "{\"prompt\": \"" + text + "\", \"max_tokens\": 150}";
                // Using Jackson to create JSON
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> jsonMap = new HashMap<>();
                List<Map<String, Object>> messages = new ArrayList();
                Map <String, Object> msg = new HashMap();
                msg.put("role", "system");
                msg.put("content", "You are a creative writer with good knowledge of dnd5e");
                messages.add(msg);
                Map <String, Object> msgPrompt = new HashMap();
                msgPrompt.put("role", "user");
                msgPrompt.put("content", text);
                messages.add(msgPrompt);
                jsonMap.put("messages", messages);
                jsonMap.put("max_tokens", 3000);
                jsonMap.put("model", "gpt-3.5-turbo");
                /*List<String> formats = new ArrayList();
                formats.add("markdown");
                formats.add("csv");*/
                jsonMap.put("format", "markdown");
                
                
                Gson gson = new Gson();
                String jsonBody = gson.toJson(jsonMap);
                // jsonBody = "";
                System.out.println("Sent to gpt\t" +jsonBody);
                //return ResponseEntity.ok(jsonBody);
                request.setEntity(new StringEntity(jsonBody));
                
                String responseString = EntityUtils.toString(httpClient.execute(request).getEntity());
                System.out.println("gpt response received : \t" +responseString);
                
                ChatResponse chatResponse = gson.fromJson(responseString, ChatResponse.class);
                
                String content = chatResponse.getChoices().get(0).getMessage().getContent();
                String html = MarkdownToHtmlConverter.convertMarkdownToHtml(content);
                //create json list of the characters
                String NPCInfo = extractNPCsSection(content);
                if(NPCInfo.length()<100) NPCInfo = getLastWords(content,700);
                jsonMap.remove("format");
                jsonMap.put("format","csv");
                jsonMap.remove("messages");
                msgPrompt.put("content", "Extract npc characters from the text with their name and description. Create a csv of result in the format character_name,character_description.  \n Markdown text to extract from : " + NPCInfo);
                messages.clear();
                messages.add(msgPrompt);
                jsonMap.put("messages", messages);
                jsonBody = gson.toJson(jsonMap);
                System.out.println("Sent to gpt\t" +jsonBody);
                //return ResponseEntity.ok(jsonBody);
                request.setEntity(new StringEntity(jsonBody));
                
                responseString = EntityUtils.toString(httpClient.execute(request).getEntity());
                System.out.println("gpt response received : \t" +responseString);
                
                chatResponse = gson.fromJson(responseString, ChatResponse.class);
                String jsonNPC = chatResponse.getChoices().get(0).getMessage().getContent();
                
                System.out.println("Json NPC\t " +jsonNPC );
                Map<String, String> charactersMap = convertCSVToHashMap(jsonNPC);
                Map<String, String> imageMap = dalleImageGenerator.generateImages(charactersMap, title);
                String charactersHtml = generateHtmlDiv((HashMap<String, String>) imageMap);
                Document doc = Jsoup.parse(html, "UTF-8");
                String logoUrl = "file:///"+PDF_PATH +  "\\pdf\\logo_trans.png";
                logoUrl = logoUrl.replace("\\", "/");
                String coverPageHtml =
                        "<div class=\"cover-page\" style=\"page-break-after: always; text-align: center; padding: 100px 0;\">" +
                        "<img src=\""+logoUrl +"\" alt=\"Cover Image\" style=\"max-width: 100%; max-height:90%;height: auto;\">" +
                        "<h1 style=\"margin-top: 50px; font-size: 36px;\">This campaign was generated by DNDGenerate</h1>" +
                        "<a href=\"http://dndgenerate.com\" style=\"font-size: 24px; color: blue; text-decoration: none;\">http://dndgenerate.com</a>" +
                        "</div>";
                Element body = doc.body();
                /*  Element link = doc.createElement("link");
                link.attr("rel", "stylesheet");
                link.attr("type", "text/css");
                link.attr("href", "file://"+PDF_PATH + "\\resources\\style.css");
                */
                String cssUrl = "file:///"+PDF_PATH + "\\pdf\\style.css";
                cssUrl = cssUrl.replace("\\", "/");
                String linkTag = String.format("<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\" />",cssUrl );
                // Parse the tag with XML parser to keep it self-closing
                Element link = Jsoup.parse(linkTag, "", Parser.xmlParser()).selectFirst("link");
                
                doc.head().appendChild(link);
                
                body.prepend(coverPageHtml);
                body.append(charactersHtml);
                Document doc2 = Jsoup.parse(doc.toString(), "", Parser.xmlParser());
                
                
                
                
                html =doc2.toString();
                System.out.println("Html :\t " + doc.outerHtml());
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                String formattedDateTime = now.format(formatter);
                if (title.isBlank()) { title = "Untitled Campaign " + formattedDateTime;}
                String path = MarkdownToHtmlConverter.convertHtmlToPdf(html, PDF_PATH, "DnDGenerate Campaign - " +title);
                //String responseString = jsonBody;
                return ResponseEntity.ok("/pdf/"+Paths.get(path).getFileName().toString());
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body("Error processing request");
            }
        } catch (IOException ex) {
             Logger.getLogger(PdfGenerator.class.getName()).log(Level.SEVERE, null, ex);
             return ResponseEntity.internalServerError().body("Error processing request");
        }
    }
    private static boolean isValidInput(String input) {
        // Implement your input validation logic here
        return input != null && !input.trim().isEmpty();
    }
    private static String extractNPCsSection(String markdownText) {
         String npcSection = "";
        Pattern pattern = Pattern.compile("(?:#|##|###) NPCs|\\*\\*NPCs\\*\\*(.+)$", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(markdownText);

        if (matcher.find()) {
            npcSection = matcher.group(1);
        }

        return npcSection == null ? "" : npcSection.trim();
    }
    public HashMap<String, String> extractCharacters(String html) {
         HashMap<String, String> npcInfo = new HashMap<>();
        Document document = Jsoup.parse(html);

       // Extract NPC information based on various HTML structures
        Elements npcSections = document.select("h1:contains(NPC) + ul, h2:contains(NPC) + h3, p:contains(NPC:) + ol, h3:contains(NPC) + ol");
       for (Element section : npcSections) {
            Elements npcs = section.select("li");

            for (Element npc : npcs) {
                String npcName = "";
                String npcDescription = "";

                if (section.tagName().equals("ol")) {
                    // Handling <ol> structure (new format)
                    Element nameElement = npc.selectFirst("p");
                    Element descElement = npc.selectFirst("ul > li:contains(Detailed description)");
                    if (nameElement != null) {
                        npcName = nameElement.text().replace("Name: ", "");
                    }
                    if (descElement != null) {
                        npcDescription = descElement.text().replace("Detailed description: ", "");
                    }
                } else {
                    // Handling <ul> structure (original format)
                    if (npc.childNodeSize() > 0) {
                        npcName = npc.child(0).ownText();
                    }
                    if (npc.childNodeSize() > 1) {
                        npcDescription = npc.child(1).ownText();
                    }
                }

                if (!npcName.isEmpty() && !npcDescription.isEmpty()) {
                    npcInfo.put(npcName, npcDescription);
                }
            }
        }
        return npcInfo;
        
    }
    private static Map<String, String> convertCSVToHashMap(String csvContent)  {
        Map<String, String> map = new HashMap<>();
        try {
            
            CSVReader reader = new CSVReader(new StringReader(csvContent));
            String[] nextLine;
            
            // Skip the header row
            reader.readNext();
            
            while ((nextLine = reader.readNext()) != null) {
                String key = nextLine[0];
                String value = nextLine[1];
                map.put(key, value);
            }
            
           
        } catch (IOException ex) {
            Logger.getLogger(TextController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CsvValidationException ex) {
            Logger.getLogger(TextController.class.getName()).log(Level.SEVERE, null, ex);
        }
         return map;
    }
    public static String generateHtmlDiv(HashMap<String, String> characters) {
        StringBuilder htmlBuilder = new StringBuilder();

        // Start of the div and the main title
        htmlBuilder.append("<div>\n<h1>NPC Characters</h1>\n<ul>\n");

        // List each character and their image
        for (Map.Entry<String, String> entry : characters.entrySet()) {
            htmlBuilder.append("<li>\n")
                       .append("<h2>").append(entry.getKey()).append("</h2>\n")
                       .append("<img width=\"75%\" align=\"center\" src=\"").append(entry.getValue()).append("\" alt=\"").append(entry.getKey()).append("\">\n")
                       .append("</li>\n");
        }

        // End of the list and the div
        htmlBuilder.append("</ul>\n</div>");

        return htmlBuilder.toString();
    }
    public static String getLastWords(String text, int numwords) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // Split the text into words
        String[] words = text.split("\\s+");

        // Calculate the start index
        int startIndex = Math.max(words.length - numwords, 0);

        // Extract the last 500 words
        StringBuilder lastWords = new StringBuilder();
        for (int i = startIndex; i < words.length; i++) {
            lastWords.append(words[i]).append(" ");
        }

        return lastWords.toString().trim();
    }
    public File copyFileToTemp(String classpathLocation) throws IOException {
        var resource = resourceLoader.getResource("classpath:" + classpathLocation);
        //Path tempFilePath = Files.createTempFile("temp_", "_file");
        File tempFile = new File(PDF_PATH + System.getProperty("file.separator") + classpathLocation);
        tempFile.getParentFile().mkdirs();

        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }

        return tempFile;
    }
}
