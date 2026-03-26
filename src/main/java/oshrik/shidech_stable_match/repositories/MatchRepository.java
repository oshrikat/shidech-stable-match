package oshrik.shidech_stable_match.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import oshrik.shidech_stable_match.datamodels.Match;
import oshrik.shidech_stable_match.datamodels.Match.MatchStatus;


public interface MatchRepository extends MongoRepository<Match, String> {
    
    // שליפת כל השידוכים שמשתמש מסוים (גבר) מעורב בהם
    List<Match> findByManId(String manId);
    
    // שליפת כל השידוכים שמשתמשת מסוימת (אישה) מעורבת בהם
    List<Match> findByWomanId(String womanId);
    
    // שליפת שידוכים לפי סטטוס (למשל: תביא לי את כל מי שממתין כרגע לתשובה)
    List<Match> findByStatus(Match.MatchStatus status);


    // מציאת match לפי המזהה שלו
    Match findOneMatchById(String id);

    // אמור לחפש למשתמש את השידוך שלו
    Optional<Match> findFirstByManIdAndStatusIn(String manId, List<MatchStatus> statuses);

    // שליפת השידוך הנוכחי לאישה מתוך רשימת סטטוסים אפשריים
    Optional<Match> findFirstByWomanIdAndStatusIn(String womanId, List<MatchStatus> statuses);
    
}