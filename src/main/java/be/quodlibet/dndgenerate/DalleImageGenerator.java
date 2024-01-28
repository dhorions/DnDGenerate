package be.quodlibet.dndgenerate;

import be.quodlibet.dndgenerate.openai.DalleResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DalleImageGenerator {

     private static final String DALLE_API_URL = System.getenv("DALLE_API_URL") != null && !System.getenv("DALLE_API_URL").isEmpty() 
                                         ? System.getenv("OPENAI_URL") 
                                         : "https://api.openai.com/v1/images/generations";
    private static final String DALLE_API_MODEL = System.getenv("DALLE_API_MODEL") != null && !System.getenv("DALLE_API_MODEL").isEmpty() 
    ? System.getenv("DALLE_API_MODEL") 
    : "dall-e-3";
    private static final String API_KEY = System.getenv("OPENAI_API_KEY");
    public static Map<String,String>  generateImages(Map<String,String> imageDescriptions, String campaignTitle)
    {
       HashMap<String, String> resultMap = new HashMap<>();

        for (Map.Entry<String, String> entry : imageDescriptions.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            // Call the processValue method with the current value
            String processedValue = generateImage(key,value,campaignTitle);
            System.out.println(key + " -> " + processedValue);
            // Put the result in the new hashmap with the same key
            if(!processedValue.isBlank()) resultMap.put(key, processedValue);
           
            if(resultMap.size() > 4) break;
        }

        return resultMap;
    }
    public static String replacePlaceholders(String input, String name, String description,String campaignTitle) {
        if (input == null) {
            return null;
        }
        String replacedString = input.replace("{name}", name);
        replacedString = replacedString.replace("{description}", description);
        replacedString = replacedString.replace("{campaignTitle}", campaignTitle);
        return replacedString;
    }
    public static String generateImage(String name, String description,String campaignTitle)
    {
        Gson gson = new Gson();
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("prompt", replacePlaceholders(PromptController.getPromptValue("story"),name,description,campaignTitle));
       /* requestBody.addProperty("prompt", "Create a portrait of a dnd5e character called "+name + " described as  " + description + " for a campaign titled : " 
                + campaignTitle+".  The focus is solely on the character in a simple portrait style, without any background elements or hints about his quest");*/
        requestBody.addProperty("n", 1);
        requestBody.addProperty("size","1024x1024");
        requestBody.addProperty("model",DALLE_API_MODEL);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(DALLE_API_URL);
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + API_KEY);
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            request.setEntity(new StringEntity(gson.toJson(requestBody)));
            System.out.println("dalle request sent : " + gson.toJson(requestBody));
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                System.out.println("Response: " + jsonResponse);
                DalleResponse json = gson.fromJson(jsonResponse, DalleResponse.class);
                if(json.data != null)
                {
                    String url = json.data[0].getUrl();
                    return url;
                }
                else
                {
                    return "";
                }
                
                
                // Handle the JSON response here. Parse the response to retrieve image URLs or data.
            } catch (Exception ex) {
                Logger.getLogger(DalleImageGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (Exception ex) {
            Logger.getLogger(DalleImageGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
}
