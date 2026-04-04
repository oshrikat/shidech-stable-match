package oshrik.shidech_stable_match.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import oshrik.shidech_stable_match.datamodels.ChatMessage;
import java.util.List;


public interface ChatRepository extends MongoRepository<ChatMessage,String> 
{

    // שולף את כל ההודעות של שידוך ספציפי, מסודרות מהישנה לחדשה
    List<ChatMessage> findByMatchIdOrderByTimestampAsc(String matchId);


} 
