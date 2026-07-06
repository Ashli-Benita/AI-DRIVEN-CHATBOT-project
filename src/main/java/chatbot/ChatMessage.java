package chatbot;

import jakarta.persistence.*;

@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String userMessage;

    @Column(columnDefinition = "TEXT")
    private String botReply;

    public ChatMessage() {
    }

    public ChatMessage(String userMessage, String botReply) {
        this.userMessage = userMessage;
        this.botReply = botReply;
    }

    public Long getId() {
        return id;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getBotReply() {
        return botReply;
    }

    public void setBotReply(String botReply) {
        this.botReply = botReply;
    }
}