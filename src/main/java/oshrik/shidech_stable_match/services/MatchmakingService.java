package oshrik.shidech_stable_match.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.datamodels.User.Gender;
import oshrik.shidech_stable_match.repositories.UserRepository;
import oshrik.shidech_stable_match.utilities.ScorePair;

@Service
public class MatchmakingService {

    private final UserRepository userRepository;
    private final MatchCalculatorService matchCalculator;

    // --- הוספנו משתנים לשמירת המצב (State) בזיכרון הריצה ---
    private List<User> currentMen;
    private List<User> currentWomen;

    public MatchmakingService(UserRepository userRepository, MatchCalculatorService matchCalculator) {
        this.userRepository = userRepository;
        this.matchCalculator = matchCalculator;
    }

    public void prepareAndFillPreferences() {
        // שומרים את הרשימות במשתני המחלקה במקום במשתנים מקומיים
        this.currentMen = userRepository.findByGender(Gender.MALE);
        this.currentWomen = userRepository.findByGender(Gender.FEMALE);

        for (User man : currentMen) {
            man.setPreferencesScores(new ArrayList<>());
            for (User woman : currentWomen) {
                if (woman.getPreferencesScores() == null) {
                    woman.setPreferencesScores(new ArrayList<>());
                }
                double score = matchCalculator.calculateTotalScore(man, woman);
                man.getPreferencesScores().add(new ScorePair(woman, score));
                woman.getPreferencesScores().add(new ScorePair(man, score));
            }
            Collections.sort(man.getPreferencesScores());
        }

        for (User woman : currentWomen) {
            Collections.sort(woman.getPreferencesScores());
        }
    }

    // --- הוספנו Getters כדי שהמסך יוכל למשוך את הרשימות המלאות ---
    public List<User> getCurrentMen() { return currentMen; }
    public List<User> getCurrentWomen() { return currentWomen; }

}