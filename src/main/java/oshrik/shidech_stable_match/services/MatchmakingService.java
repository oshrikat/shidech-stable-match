package oshrik.shidech_stable_match.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.datamodels.User.Gender;
import oshrik.shidech_stable_match.datamodels.User.ROLE;
import oshrik.shidech_stable_match.datamodels.User.UserStatus;
import oshrik.shidech_stable_match.datamodels.MatchScore;
import oshrik.shidech_stable_match.repositories.UserRepository;
import oshrik.shidech_stable_match.utilities.ScorePair;

@Service
public class MatchmakingService {

    private final UserRepository userRepository;
    private final MatchCalculatorService matchCalculator;
    private final MatchScoreService matchScoreService;

    private List<User> currentMen;
    private List<User> currentWomen;

    // הזרקת כל התלויות דרך הבנאי
    public MatchmakingService(UserRepository userRepository,
            MatchCalculatorService matchCalculator,
            MatchScoreService matchScoreService) {
        this.userRepository = userRepository;
        this.matchCalculator = matchCalculator;
        this.matchScoreService = matchScoreService;
    }

    public void prepareAndFillPreferences() {

        // 1. שליפת המועמדים
        this.currentMen = userRepository.findByGenderAndRoleAndStatus(Gender.MALE, ROLE.USER, UserStatus.AVAILABLE);
        this.currentWomen = userRepository.findByGenderAndRoleAndStatus(Gender.FEMALE, ROLE.USER, UserStatus.AVAILABLE);

        matchScoreService.cleanScoreMatchTable();
        List<MatchScore> scoresToSaveToDb = new ArrayList<>();

        // 2. איפוס רשימות זמניות פעם אחת
        currentMen.forEach(m -> m.setPreferencesScores(new ArrayList<>()));
        currentWomen.forEach(w -> w.setPreferencesScores(new ArrayList<>()));

        // 3. חישוב המטריצה
        for (User man : currentMen) {
            for (User woman : currentWomen) {

                double scoreForMan = matchCalculator.calculateTotalScore(man, woman);
                double scoreForWoman = matchCalculator.calculateTotalScore(woman, man);

                double finalUnifiedScore = 0;

                if (scoreForMan > 0 && scoreForWoman > 0) {
                    // רק אם שניהם לא פסלו - מחשבים ממוצע אחיד
                    finalUnifiedScore = (scoreForMan + scoreForWoman) / 2.0;
                }

                // שניהם מקבלים את אותו הציון בדיוק
                man.getPreferencesScores().add(new ScorePair(woman, finalUnifiedScore));
                woman.getPreferencesScores().add(new ScorePair(man, finalUnifiedScore));

                // שמירה ל-DB לצורך אינדיקציה
                scoresToSaveToDb.add(new MatchScore(man.getId(), woman.getId(), finalUnifiedScore));
            }

            // מיון רשימת הגבר (מהגבוה לנמוך)
            Collections.sort(man.getPreferencesScores());
        }

        // 4. מיון רשימות הנשים (קריטי לאלגוריתם!)
        for (User woman : currentWomen) {
            Collections.sort(woman.getPreferencesScores());
        }

        // 5. שמירה מרוכזת ל-DB
        matchScoreService.saveAllScores(scoresToSaveToDb);
        System.out.println("Successfully calculated scores for " + scoresToSaveToDb.size() + " pairs.");
    }

    /**
     * מחזיר את רשימת כל הגברים במערכת (כאשר לכל אחד מהם כבר יש רשימת העדפות פנימית
     * ממוינת)
     * 
     * 
     * @return החזרת של הרשימה הממויינת
     */
    public List<User> getCurrentMen() { return currentMen; }

    /**
     * מחזיר את רשימת כל הנשים במערכת (כאשר לכל אחד מהם כבר יש רשימת העדפות
     * פנימית ממוינת)
     * 
     * @return החזרת של הרשימה הממויינת
     */
    public List<User> getCurrentWomen() { return currentWomen; }


}