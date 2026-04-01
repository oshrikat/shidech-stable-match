package oshrik.shidech_stable_match.services;

import java.util.ArrayList;
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
import oshrik.shidech_stable_match.utilities.ScorePair;

@Service
public class MatchService {

    private final GaleShapleyAlgoService galeShapleyService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    // בהמשך נוסיף לכאן גם את שירות האימיילים: private final EmailService emailService;

    public MatchService(MatchRepository matchRepository, UserRepository userRepository, UserService userService,
            GaleShapleyAlgoService galeShapleyService) {
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.galeShapleyService = galeShapleyService;
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

        User man = userRepository.findById(curMatch.getManId()).orElse(null);
        User woman = userRepository.findById(curMatch.getWomanId()).orElse(null);

        if (curMatch == null)
            return false;

        // לעדכן את השדה הנכון (manAgreed או womanAgreed) לפי ה-gender
        if(gender.equals(Gender.MALE))
        {
            curMatch.setManAgreed(isAccepted);
            if (isAccepted) {
                man.setStatus(UserStatus.AGREEE_WATING);
            }

        }
        else
        {
            curMatch.setWomanAgreed(isAccepted);
            if (isAccepted) {
                woman.setStatus(UserStatus.AGREEE_WATING);
            }
        }
         
        //  : לוגיקת שינוי סטטוס:
        //       - אם isAccepted הוא false -> הסטטוס הופך ל-REJECTED.
        if(!isAccepted)
        {
            curMatch.setStatus(MatchStatus.REJECTED);

            // נשנה להם סטטוס של פנויים
            if (man != null) {
                man.setStatus(UserStatus.AVAILABLE);
                man.setCurrentPartner(null);

            }

            if (woman != null) {
                woman.setStatus(UserStatus.AVAILABLE);
                woman.setCurrentPartner(null);

            }

            userRepository.save(man);
            userRepository.save(woman);
            
            //  : לשמור את השידוך המעודכן בחזרה למונגו.
            matchRepository.save(curMatch);
            return false;
        }

       // - אם שניהם (manAgreed ו-womanAgreed) הם true -> הסטטוס הופך ל-ACTIVE_DATING.
        if (Boolean.TRUE.equals(curMatch.getManAgreed()) && Boolean.TRUE.equals(curMatch.getWomanAgreed())) {
            curMatch.setStatus(MatchStatus.ACTIVE_DATING);
            
            // נשנה להם סטטוס של תפוסים
            if (man != null) {
                man.setStatus(UserStatus.IN_RELATIONSHIP);
                man.setCurrentPartner(woman);
            }

            if (woman != null) {
                woman.setStatus(UserStatus.IN_RELATIONSHIP);
                woman.setCurrentPartner(man);
            }

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

    // תהליך מרכזי - אלגוריתם גייל שייפלי

    public List<Match> runAlgo_performFullMatchmaking(List<User> mens, List<User> womens) {
        // 1. הפעלת האלגוריתם (החישוב בזיכרון)
        galeShapleyService.runGaleShapley(mens, womens);

        List<Match> matchesToSave = new ArrayList<>();

        // 2. מעבר על התוצאות בזיכרון והפיכתן לנתונים במסד הנתונים
        for (User man : mens) {
            User woman = man.getCurrentPartner();

            if (woman != null) {
                // יצירת אובייקט Match חדש
                Match newMatch = new Match(man.getId(), woman.getId(), findScore(man, woman));
                newMatch.setStatus(MatchStatus.PENDING_RESPONSES); // סטטוס התחלתי
                matchesToSave.add(newMatch);

                // עדכון הסטטוס של המשתמשים עצמם
                man.setStatus(UserStatus.PENDING_APPROVAL);
                woman.setStatus(UserStatus.PENDING_APPROVAL);
            }
        }

        // אוסף של תוצאות שידוך סופי
        matchRepository.saveAll(matchesToSave);
        // אוסף של משתמשים
        userRepository.saveAll(mens);
        userRepository.saveAll(womens);

        System.out.println("Saved " + matchesToSave.size() + " new matches to DB.");

        return matchesToSave;
    }

    /** פונקציית עזר לשליפת ציון ההתאמה מתוך רשימת ההעדפות */
    private double findScore(User man, User partner) {
        if (man.getPreferencesScores() != null) {
            for (ScorePair sp : man.getPreferencesScores()) {
                if (sp.getCandidate().getId().equals(partner.getId())) {
                    return sp.getScore();
                }
            }
        }
        return 0.0;
    }

    public List<Match> findAllMatches() {
        return matchRepository.findAll();
    }

}