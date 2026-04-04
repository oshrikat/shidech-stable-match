package oshrik.shidech_stable_match.datamodels;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "Chat_Messages")
public class ChatMessage {

    @Id
    private String id; // מזהה ייחודי של ההודעה שמונגו מייצר אוטומטית
    
    private String matchId; // מזהה השידוך (כדי לאגד את כל השיחה יחד)
    private String senderId; // מי שלח
    private String recipientId; // מי מקבל
    private String content; // תוכן ההודעה
    private LocalDateTime timestamp; // מתי נשלחה

    // קונסטרקטור ריק למונגו
    public ChatMessage() {}

    // קונסטרקטור ליצירת הודעה חדשה
    public ChatMessage(String matchId, String senderId, String recipientId, String content) {
        this.matchId = matchId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
        this.timestamp = LocalDateTime.now(); // מקבע את שעת השליחה לרגע יצירת האובייקט
        
    }


    // Getters and Setters ...

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

   
    
    


}