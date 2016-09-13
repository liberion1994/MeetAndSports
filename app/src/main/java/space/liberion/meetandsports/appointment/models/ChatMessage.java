package space.liberion.meetandsports.appointment.models;

/**
 * @author greg
 * @since 6/21/13
 */
public class ChatMessage {

    private String message;
    private String author;

    // Required default constructor for Firebase object mapping
    public ChatMessage() {
    }

    public ChatMessage(String message, String author) {
        this.message = message;
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }
}
