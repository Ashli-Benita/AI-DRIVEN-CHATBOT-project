package chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.Desktop;
import java.net.URI;

@SpringBootApplication
public class ChatbotApplication {

    public static void main(String[] args) {

        SpringApplication.run(ChatbotApplication.class, args);

        try {
            Thread.sleep(3000);

            String url = "http://localhost:8081";

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }

        } catch (Exception e) {
            System.out.println("Browser open failed: " + e.getMessage());
        }
    }
}