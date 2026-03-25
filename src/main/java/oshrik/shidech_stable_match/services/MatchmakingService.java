package oshrik.shidech_stable_match.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.datamodels.User.Gender;
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
        this.currentMen = userRepository.findByGender(Gender.MALE);
        this.currentWomen = userRepository.findByGender(Gender.FEMALE);

        // שלב 1: מנקים את מסד הנתונים מהציונים הישנים לפני שמתחילים חישוב חדש
        matchScoreService.cleanScoreMatchTable();

        // רשימה זמנית שתאגור את כל הציונים כדי לשמור אותם יחד בסוף
        List<MatchScore> scoresToSaveToDb = new ArrayList<>();

        for (User man : currentMen) {
            man.setPreferencesScores(new ArrayList<>());
            for (User woman : currentWomen) {
                if (woman.getPreferencesScores() == null) {
                    woman.setPreferencesScores(new ArrayList<>());
                }

                // חישוב ציון ההתאמה בין הגבר לאישה על בסיס נתוני הפרופיל שלהם
                double score = matchCalculator.calculateTotalScore(man, woman);

                // --- שמירה בזיכרון (בשביל המהירות של אלגוריתם גייל-שפלי) ---
                man.getPreferencesScores().add(new ScorePair(woman, score)); // שומר אצל הגבר את האישה שאיתה קיבל ציון
                                                                             // ואת הציון
                woman.getPreferencesScores().add(new ScorePair(man, score));

                // --- הכנה לשמירה במסד הנתונים (בשביל היסטוריה, תצוגה במסך וכו') ---
                scoresToSaveToDb.add(new MatchScore(man.getId(), woman.getId(), score));

            }

            // מיון רשימת המועמדות של הגבר הנוכחי מהציון הגבוה לנמוך
            Collections.sort(man.getPreferencesScores());

        }

        // נעבור על כל הנשים ונמיין לכל אחת את הרשימת העדפות שכרגע קיימת אצלה
        for (User woman : currentWomen) {
            Collections.sort(woman.getPreferencesScores());
        }

        // שלב 2: שומרים את כל המטריצה למסד הנתונים בפעולה אחת יעילה
        matchScoreService.saveAllScores(scoresToSaveToDb);
        System.out
                .println("Successfully calculated and saved " + scoresToSaveToDb.size() + " match scores to MongoDB.");
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