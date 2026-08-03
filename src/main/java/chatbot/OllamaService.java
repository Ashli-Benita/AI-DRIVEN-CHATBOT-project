package chatbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OllamaService {

    private final WebClient webClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public OllamaService(@Value("${ollama.base.url:http://localhost:11434}") String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    public String askAI(String prompt) {

        try {
            String model = System.getenv().getOrDefault("OLLAMA_MODEL", "llama3.2");

            String body = """
            {
              "model": "%s",
              "prompt": "%s",
              "stream": false
            }
            """.formatted(model, prompt.replace("\"", "\\\""));

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