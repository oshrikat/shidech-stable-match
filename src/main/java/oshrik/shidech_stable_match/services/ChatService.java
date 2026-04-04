package oshrik.shidech_stable_match.services;

import org.springframework.stereotype.Service;
import oshrik.shidech_stable_match.datamodels.ChatMessage;
import oshrik.shidech_stable_match.repositories.ChatRepository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    // המרכזייה שלנו: מחזיקה איזה מזהה משתמש מחובר לאיזה מסך כרגע
    private final Map<String, ChatCallBack> activeUsers = new ConcurrentHashMap<>();

    // ממשק ההאזנה (Callback) שהמסך יממש
    public interface ChatCallBack {
        void onNewMessageArrived(ChatMessage message);
    }

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    // 1. שמירה ושליחה של הודעה חדשה
    public void sendMessage(String matchId, String senderId, String recipientId, String content) {
        
        // יצירת הודעה חדשה - בנייה שלה בעזרת הבנאי
        ChatMessage newMessage = new ChatMessage(matchId, senderId, recipientId, content);

        // שמירה במסד הנתונים
        chatRepository.save(newMessage);

        // אם המתשמש מחובר כעת , נקפיץ לו את ההודעה שנשלחה
        if(activeUsers.containsKey(recipientId))
            {
                activeUsers.get(recipientId).onNewMessageArrived(newMessage);
            }


    }

    // 2. שליפת היסטוריית שיחה למסך שעכשיו נטען
    public List<ChatMessage> getChatHistory(String matchId) {

        return chatRepository.findByMatchIdOrderByTimestampAsc(matchId);  
    }

    // 3. משתמש נכנס למסך הצ'אט (התחברות למרכזייה)
    public void register(String userId, ChatCallBack callBack) {
        activeUsers.put(userId, callBack);
    }

    // 4. משתמש עוזב את מסך הצ'אט (ניתוק מהמרכזייה)
    public void unregister(String userId) {
        activeUsers.remove(userId);
    }
    
}