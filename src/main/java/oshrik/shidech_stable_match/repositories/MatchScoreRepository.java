package oshrik.shidech_stable_match.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import oshrik.shidech_stable_match.datamodels.MatchScore;
import java.util.List;


/**
 * ממשק שמנהל תקשורת בין שירות של האוסף של ציוני התאמות ממסד הנתונים לקוד ! שליפה ועדכון של האוסף
 */
@Repository
public interface MatchScoreRepository extends MongoRepository<MatchScore,String> 
{
    // פעולות עם האוסף של ScoreMatches במסד הנתונים


    /**
     * שליפת העדפות של גבר ממויין
     * @param manId
     * @return
     */
    public List<MatchScore> findByManIdOrderByTotalScoreDesc(String manId);   
    
    /**
     *  שליפת העדפות של אישה ממויין
     * @param womanId
     * @return
     */
    List<MatchScore> findByWomanIdOrderByTotalScoreDesc(String womanId);


    

}