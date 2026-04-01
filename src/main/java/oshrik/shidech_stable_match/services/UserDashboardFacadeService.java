package oshrik.shidech_stable_match.services;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.springframework.stereotype.Service;

import oshrik.shidech_stable_match.datamodels.Match;
import oshrik.shidech_stable_match.datamodels.MatchScore;
import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.datamodels.User.Gender;

@Service
public class UserDashboardFacadeService {
    
    // Services
    private final MatchScoreService matchScoreService;
    private final MatchService matchService;
    private final UserService userService;
    private final EmailService emailService;

    public UserDashboardFacadeService(MatchScoreService matchScoreService, MatchService matchService, UserService userService,EmailService emailService)
    {
        this.matchScoreService = matchScoreService;
        this.matchService = matchService;
        this.userService = userService;
        this.emailService = emailService;
    }

    public Match getCurrentActiveOrPendingMatch(String id, Gender gender) {
        return matchService.getCurrentActiveOrPendingMatch(id, gender);
    }

    public User findUserById(String userID) {
        return userService.findUserById(userID);
    }

    public List<MatchScore> getRankedWomenForMan(String id) {
        return matchScoreService.getRankedWomenForMan(id);
    }

    public List<MatchScore> getRankedMenForWoman(String id) {
        return matchScoreService.getRankedMenForWoman(id);
    }

    public String getBestScore(User currUser)
    {
         String maxScoreText = "?";
        if (currUser != null && currUser.getId() != null) {
            List<MatchScore> scores = (currUser.getGender() == User.Gender.MALE)
                    ? getRankedWomenForMan(currUser.getId())
                    : getRankedMenForWoman(currUser.getId());

            if (scores != null && !scores.isEmpty()) {
                double max = scores.get(0).getTotalScore();
                maxScoreText = String.format("%.1f%%", max);
            }
        }
        return maxScoreText;


    }

     public String calculateDaysInSystem(User currUser) 
     {
            return String.valueOf(Period.between(currUser.getRegistrationDate().toLocalDate(), LocalDate.now()).getDays());
     }

     public String getCountFirstMatches(User currUser) {
        return String.valueOf(currUser.getPreferencesScores().size());
    }

     public void sendTestEmail(String email_destination, String text_title, String text_body) {
        
        // פנייה לשירות של האימייל והפניה אליו עם כל הפקטים לשליחה של האימייל
        emailService.sendSimpleEmail(email_destination, text_title, text_body);


     }



}
