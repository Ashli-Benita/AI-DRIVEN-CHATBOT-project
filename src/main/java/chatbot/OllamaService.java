package chatbot;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OllamaService {

    private final WebClient webClient = WebClient.create("http://localhost:11434");
    private final ObjectMapper mapper = new ObjectMapper();

    public String askAI(String prompt) {

        try {
            String body = """
            {
              "model": "llama3.2",
              "prompt": "%s",
              "stream": false
            }
            """.formatted(prompt.replace("\"", "\\\""));

            String response = webClient.post()
                    .uri("/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("RAW OLLAMA RESPONSE: " + response);

            JsonNode json = mapper.readTree(response);

            return json.has("response")
                    ? json.get("response").asText()
                    : "No response from Ollama";

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}