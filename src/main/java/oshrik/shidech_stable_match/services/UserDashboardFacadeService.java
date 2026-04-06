package oshrik.shidech_stable_match.services;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import oshrik.shidech_stable_match.datamodels.Match;
import oshrik.shidech_stable_match.datamodels.MatchScore;
import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.datamodels.Match.MatchStatus;
import oshrik.shidech_stable_match.datamodels.User.Gender;
import oshrik.shidech_stable_match.datamodels.User.UserStatus;
import oshrik.shidech_stable_match.ui.MyMatchView;
import oshrik.shidech_stable_match.ui.components.StatusCardData;
import oshrik.shidech_stable_match.utilities.RouteHelper;

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

     /**
      * מפיק אובייקט נתונים להצגת כרטיסיית הסטטוס בדאשבורד, בהתאם למצבו הנוכחי של
      * המשתמש.
      * הפונקציה מאתרת שידוך פעיל (אם קיים) ובונה את הטקסטים והכפתורים הנדרשים
      * לתצוגה.
      *
      * @param currUser המשתמש הנוכחי שעבורו נבנית התצוגה.
      * @return StatusCardData אובייקט המכיל כותרת, תת-כותרת, וטקסט לכפתור
      */
     public StatusCardData getStatusCardToShow(User currUser) {

         Match currMatch = getCurrentActiveOrPendingMatch(currUser.getId(), currUser.getGender());

         // 1. אם אין שידוך בכלל (ממתין להתאמה)
         if (currMatch == null) {
             return new StatusCardData(
                     "⏳ ממתין להתאמה...",
                     "האלגוריתם שלנו פעיל וסורק את המערכת. ההתאמה המושלמת תגיע בקרוב.",
                     null // אין כפתור במצב הזה!
             );
         }

         // 2. יש שידוך - נשלוף את בן/בת הזוג האמיתי
         User partner;
         if (currUser.getGender().equals(Gender.MALE)) {
             partner = findUserById(currMatch.getWomanId());
         } else {
             partner = findUserById(currMatch.getManId());
         }

         // 3. *הגדרת הטקסטים לפי הסטטוס של ה*שידוך
         MatchStatus matchStatus = currMatch.getStatus();

         // שלב א': ממתינים לתשובות מהצדדים
         if (matchStatus.equals(MatchStatus.PENDING_RESPONSES)) {

             // אם המשתמש שלנו כבר לחץ "אני מעוניין" והוא ממתין לצד השני
             if (currUser.getStatus().equals(UserStatus.AGREEE_WATING)) {
                 return new StatusCardData(
                         "הסכמת להצעה! 👍",
                         "אנו ממתינים כעת להחלטה של " + partner.getFirstName() + "...",
                         "צפה בסטטוס ההצעה");
             }

             // אם המשתמש שלנו עדיין לא קיבל החלטה
             else {
                 String message = String.format("הכר את %s (גיל: %d). איזה יופי!", partner.getFullName(),
                         partner.getAge());
                 return new StatusCardData(
                         "יש לך התאמה! 🎉",
                         message,
                         "כנס והחלט כאן !");
             }
         }

         // שלב ב': שניהם מעוניינים - נכנסים לצ'אט!
         else if (matchStatus.equals(MatchStatus.PRE_DATING_EVALUATION)) {
             String message = String.format("בוא נכיר סופית ונחליט לגבי השידוך עם: %s (גיל: %d).",
                     partner.getFullName(), partner.getAge());
             return new StatusCardData(
                     "מזל טוב! שניכם מעוניינים אחד בשני!",
                     message,
                     "צ'אט עם " + partner.getFirstName());
         }

         // שלב ג': החליפו פרטים ויוצאים בעולם האמיתי
         else if (matchStatus.equals(MatchStatus.ACTIVE_DATING)) {
             String message = ("מה קורה ? יש עדכון ?");
             return new StatusCardData(
                     "תעדכנו אותנו בבקשה מה מצבכם !",
                     message,
                     "עדכון סטטוס ");
         }

         // מצב חירום (Fallback)
         else {
             String message = ("מצב לא ידוע... תקלה");
             return new StatusCardData(
                     "מצב תקלה. הם יוצאים אבל מצב לא מזוהה",
                     message,
                     null);
         }

     }


}
