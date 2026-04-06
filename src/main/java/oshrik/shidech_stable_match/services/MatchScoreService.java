package oshrik.shidech_stable_match.services;

import java.util.List;
import org.springframework.stereotype.Service;
import oshrik.shidech_stable_match.datamodels.MatchScore;
import oshrik.shidech_stable_match.repositories.MatchScoreRepository;

@Service
public class MatchScoreService {
    
    private final MatchScoreRepository repo_score_match;

    public MatchScoreService(MatchScoreRepository repo_score_match) {
        this.repo_score_match = repo_score_match;
    }

    /**
     * מנקה את כל טבלת הציונים. שימושי לפני הרצה חדשה של האלגוריתם.
     */
    public boolean cleanScoreMatchTable() {
        try {
            repo_score_match.deleteAll();
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * שומר ציון התאמה בודד במסד הנתונים.
     */
    public MatchScore saveScore(MatchScore score) {
        return repo_score_match.save(score);
    }

    /**
     * שומר רשימה של ציוני התאמה בבת אחת (יעיל יותר לשמירה המונית).
     */
    public List<MatchScore> saveAllScores(List<MatchScore> scores) {
        return repo_score_match.saveAll(scores);
    }

    /**
     * שולף את כל הנשים עבור גבר מסוים, מסודרות מהציון הגבוה לנמוך (דירוג העדפות).
     */
    public List<MatchScore> getRankedWomenForMan(String manId) {
        return repo_score_match.findByManIdOrderByTotalScoreDesc(manId);
    }

    /**
     * שולף את כל הגברים עבור אישה מסוימת, מסודרים מהציון הגבוה לנמוך (דירוג העדפות).
     */
    public List<MatchScore> getRankedMenForWoman(String womanId) {
        return repo_score_match.findByWomanIdOrderByTotalScoreDesc(womanId);
    }

}