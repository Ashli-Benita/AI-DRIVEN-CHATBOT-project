package chatbot;

import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatRepository repository;
    private final OllamaService ollamaService;

    @Autowired
    public ChatController(ChatRepository repository, OllamaService ollamaService) {
        this.repository = repository;
        this.ollamaService = ollamaService;
    }

    // 💬 STREAMING CHAT (ChatGPT-like response)
    @GetMapping
    public void chat(@RequestParam String message,
                     HttpServletResponse response) throws Exception {

        response.setContentType("text/plain;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Get AI response from Ollama
        String reply = ollamaService.askAI(message);

        // Save chat to DB
        ChatMessage chat = new ChatMessage(message, reply);
        repository.save(chat);

        // 🔥 STREAM RESPONSE (typing effect)
        for (char c : reply.toCharArray()) {
            response.getWriter().write(c);
            response.getWriter().flush();
            Thread.sleep(10); // speed of typing
        }
    }

    // 📜 CHAT HISTORY
    @GetMapping("/history")
    public List<ChatMessage> history() {
        return repository.findAll();
    }

    // 🧹 CLEAR CHAT
    @DeleteMapping("/clear")
    public String clearChat() {
        repository.deleteAll();
        return "Chat history cleared!";
    }
}