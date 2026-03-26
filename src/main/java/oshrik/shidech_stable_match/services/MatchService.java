package oshrik.shidech_stable_match.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import oshrik.shidech_stable_match.datamodels.Match;
import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.datamodels.Match.MatchStatus;
import oshrik.shidech_stable_match.datamodels.User.Gender;
import oshrik.shidech_stable_match.datamodels.User.UserStatus;
import oshrik.shidech_stable_match.repositories.MatchRepository;
import oshrik.shidech_stable_match.repositories.UserRepository;

@Service
public class MatchService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    // בהמשך נוסיף לכאן גם את שירות האימיילים: private final EmailService emailService;

    public MatchService(MatchRepository matchRepository, UserRepository userRepository, UserService userService) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * 1. שמירת תוצאות האלגוריתם והתראות
     * מקבלת את רשימת השידוכים החדשה שגייל-שפלי ייצר הרגע.
     */
    public void processAndSaveNewMatches(List<Match> newMatches) {
        //    נשמור את כל הרשימה של השידוכים במסד הנתונים 
        matchRepository.saveAll(newMatches);
        
        //   לולאה שעוברת על הזוגות ושולחת אימייל לכל אחד מהצדדים
        for(Match match : newMatches)
            {
                sendEmailToCouple(match);
            }


    }

    private void sendEmailToCouple(Match match) {
         
        // Calling EmailService & Sending Each One an Email With the details...

    }

    /**
     * 2. שליפת הנתונים לדאשבורד
     * מחזירה את השידוך הנוכחי של המשתמש, בתנאי שהוא עדיין פעיל או ממתין.
     */
    /**
     * 2. שליפת הנתונים לדאשבורד
     * מחזירה את השידוך הנוכחי של המשתמש, בתנאי שהוא עדיין פעיל או ממתין.
     */
    public Match getCurrentActiveOrPendingMatch(String userId, User.Gender gender) {
        
        // מגדירים את הסטטוסים שאנחנו מחפשים (ממתין לתשובה או כבר בדייטים)
        List<MatchStatus> activeStatuses = Arrays.asList(MatchStatus.PENDING_RESPONSES, MatchStatus.ACTIVE_DATING);

        // שולפים ישירות ממסד הנתונים בהתאם למגדר
        if (gender.equals(Gender.MALE)) {
            return matchRepository.findFirstByManIdAndStatusIn(userId, activeStatuses).orElse(null);
        } else {
            return matchRepository.findFirstByWomanIdAndStatusIn(userId, activeStatuses).orElse(null);
        }
    }
    
     /**
     * 3. עדכון החלטה (אישור/דחייה מהמסך הייעודי)
     */
    public boolean updateMatchResponse(String matchId, String respondingUserId, boolean isAccepted, User.Gender gender) {
        
        // לשלוף את השידוך לפי ה matchId
        Match curMatch = matchRepository.findOneMatchById(matchId);

        // לעדכן את השדה הנכון (manAgreed או womanAgreed) לפי ה-gender
        if(gender.equals(Gender.MALE))
            curMatch.setManAgreed(isAccepted);
        else
            curMatch.setWomanAgreed(isAccepted);
         
        //  : לוגיקת שינוי סטטוס:
        //       - אם isAccepted הוא false -> הסטטוס הופך ל-REJECTED.
        if(!isAccepted)
        {
            curMatch.setStatus(MatchStatus.REJECTED);

               // נשנה להם סטטוס של פנויים
            User man = userRepository.findById(curMatch.getManId()).orElse(null);
            if(man != null) man.setStatus(UserStatus.AVAILABLE);
            
            User woman = userRepository.findById(curMatch.getWomanId()).orElse(null);
            if(woman != null) woman.setStatus(UserStatus.AVAILABLE);

            userRepository.save(man);
            userRepository.save(woman);
            
            //  : לשמור את השידוך המעודכן בחזרה למונגו.
            matchRepository.save(curMatch);
            return false;
        }
        // מחשבה שלי , אני לא מבין את ההיגיון, הרי : לא כדאי לבדוק קודם כל אם המשתנה isAccepted הוא false , ולעצור שם אם באמת הוא false , כי כל השידוך בעצם יתבטל , ואפשר להחזיר ruturn , אני טועה ?


       // - אם שניהם (manAgreed ו-womanAgreed) הם true -> הסטטוס הופך ל-ACTIVE_DATING.
        if (Boolean.TRUE.equals(curMatch.getManAgreed()) && Boolean.TRUE.equals(curMatch.getWomanAgreed())) {
            curMatch.setStatus(MatchStatus.ACTIVE_DATING);
            
            // נשנה להם סטטוס של תפוסים
            User man = userRepository.findById(curMatch.getManId()).orElse(null);
            if(man != null) man.setStatus(UserStatus.IN_RELATIONSHIP);
            
            User woman = userRepository.findById(curMatch.getWomanId()).orElse(null);
            if(woman != null) woman.setStatus(UserStatus.IN_RELATIONSHIP);

            userRepository.save(man);
            userRepository.save(woman);

            // לשמור את השידוך המעודכן בחזרה למונגו.
            matchRepository.save(curMatch);
            return true;
        }
            
        
        //  : לשמור את השידוך המעודכן בחזרה למונגו.
        matchRepository.save(curMatch);

        return false;
    }
}