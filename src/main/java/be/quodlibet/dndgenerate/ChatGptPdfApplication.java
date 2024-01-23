package be.quodlibet.dndgenerate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling

@SpringBootApplication
public class ChatGptPdfApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatGptPdfApplication.class, args);
    }
}
