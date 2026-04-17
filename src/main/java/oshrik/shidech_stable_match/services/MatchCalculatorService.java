package oshrik.shidech_stable_match.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.datamodels.User.Occupation;
import oshrik.shidech_stable_match.datamodels.User.ReligiousLevel;

@Service
public class MatchCalculatorService 
{

    // --- 1. משקולות ראשיות ---
    private final static double WEIGHT_PREFERENCES = 0.6;    
    private final static double WEIGHT_PERSONALITY = 0.4;

    // הגדרת קבועים לכל שאלה בתוך החלק הראשון - פרק 3 - התאמת נתונים והעדפות 
    private final static double WEIGHT_RELIGION = 0.25;    
    private final static double WEIGHT_AGE = 0.15;    
    private final static double WEIGHT_OCCUPATION = 0.15;    
    private final static double WEIGHT_DISTANCE = 0.10;    
    private final static double WEIGHT_HEIGHT = 0.10;    
    private final static double WEIGHT_EDUCATION = 0.10;    
    private final static double WEIGHT_SMOKING = 0.10;    
    private final static double WEIGHT_ETHNICITY = 0.05;    
    
    // הגדרת קבועים לכל שאלה בתוך החלק הראשון - פרק 4 - התאמת אישיות 
    private final static double WEIGHT_20 = 0.2;    
    private final static double WEIGHT_SCALE_EXTRAVERSION = WEIGHT_20;    
    private final static double WEIGHT_SCALE_ORDERNESS = WEIGHT_20;    
    private final static double WEIGHT_SCALE_EMOTION = WEIGHT_20;    
    private final static double WEIGHT_SCALE_STUBBORNESS = WEIGHT_20;    
    private final static double WEIGHT_SCALE_OPENNESS = WEIGHT_20;



    // --- מימוש הפונקציה לחישוב כל פרק 3 ---
    private double calcPreferencesScore(User me, User her) {


        // כאן נבדוק Deal Breakers שלא נותנים ניקוד אלא פוסלים ישר... (ילדים, חיות)
        if (isDealBreaker(me, her)) {
            return 0; // פסילה מיידית של כל הניקוד בפרק הזה (או אפילו בכלל)
        }

        // הציון הסופי של החישוב ציון התאמה ביניהם
        double score = 0;

        // שים לב: אנחנו כופלים את הציון (0-100) במשקל היחסי שלו
        score += calcReligionScore(me, her) * WEIGHT_RELIGION;
        score += calcAgeScore(me, her) * WEIGHT_AGE;
        score += calcOccupationScore(me, her) * WEIGHT_OCCUPATION;
        score += calcDistanceScore(me, her) * WEIGHT_DISTANCE;
        score += calcHeightScore(me, her) * WEIGHT_HEIGHT;
        score += calcEducationScore(me, her) * WEIGHT_EDUCATION;
        score += calcSmokingScore(me, her) * WEIGHT_SMOKING;
        score += calcEthnicityScore(me, her) * WEIGHT_ETHNICITY;
        
        return score;
    }

    // ---  פונקציות עזר לפרק 3 (Helpers) ---

    private double calcReligionScore(User me, User candidate) {
        // שלב 1: שלוף את מה שאני מוכן לקבל (הרשימה הלבנה שלי)
        List<ReligiousLevel> myWhitelist = me.getAllowedReligiousLevels();
        
        // שלב 2: שלוף את מה שהיא באמת (הנתון שלה)
        ReligiousLevel herReality = candidate.getReligiousLevel();
        
        // שלב 3: בדיקה - האם המציאות שלה קיימת ברשימה שלי?
        if (myWhitelist != null && myWhitelist.contains(herReality)) {
            return 100; // בינגו! היא מה שאני מחפש
        }
        
        return 0; // לא מתאימה
    }

     private double calcOccupationScore(User me, User candidate) {
        // לוגיקה: Whitelist
        
        List<Occupation> myWhitelist = me.getAllowedOccupations();
        Occupation herReality = candidate.getOccupation();
        
        if (myWhitelist != null && myWhitelist.contains(herReality)) {
            return 100; 
        }
        
        return 0;
     }

    private double calcAgeScore(User me, User candidate) {
        // לוגיקה: Range + Penalty
        if (me.getAgeRange() == null) return 100; // הגנה למקרה שאין טווח

        if(me.getAgeRange().getMin() <= candidate.getAge() && me.getAgeRange().getMax() >= candidate.getAge())
            return 100; // המועמד כן נמצא בטווח !  
        
        // כנראה שזה לא בטווח לכן , נעניש על כל שנת הבדל ב 1.5  
        double diff = 0;
        if(me.getAgeRange().getMax() < candidate.getAge()) {
            diff = candidate.getAge() - me.getAgeRange().getMax();
        } else {
            diff = me.getAgeRange().getMin() - candidate.getAge();
        }

        return Math.max(0, 100 - (diff * 1.5));
    }

    private double calcHeightScore(User me, User candidate) {
        // לוגיקה: Range + Penalty
        if (me.getHeightRange() == null) return 100; 

        if( me.getHeightRange().getMin() <= candidate.getHeight() && me.getHeightRange().getMax() >= candidate.getHeight())
            return 100; // המועמד כן נמצא בטווח !  
        
         double diff = 0;
        if(me.getHeightRange().getMax() < candidate.getHeight()) {
            diff = candidate.getHeight() - me.getHeightRange().getMax();
        } else {
            diff = me.getHeightRange().getMin() - candidate.getHeight();
        }

        return Math.max(0, 100 - (diff * 1.5));
    }

    // חישוב המרחק האמיתי בק"מ
    private double calcDistanceScore(User me, User candidate) {
        // הגנה: אם אין לאחד מהם מיקום מוגדר, נניח שהמרחק סביר - ממוצע או נחזיר 0.

        if (me.getAddress() == null || candidate.getAddress() == null)
            return 70;

        double accuallDistance = me.getAddress().distanceTo(candidate.getAddress());
        int maxAllowed = me.getMaxDistanceKm();

        if (accuallDistance <= maxAllowed) {
            return 100;
        }

        double diff = accuallDistance - maxAllowed;
        return Math.max(0, 100 - (diff * 0.5));

    }

    private double calcEducationScore(User me, User candidate) {
        // לוגיקה: Binary Penalty
        if(me.isRequiresDegree() && !candidate.isHasDegree()) {
            return -20;
        }
        return 100;
    }
    
    private double calcEthnicityScore(User me, User candidate) {
        // לוגיקה: Blacklist
        if(me.getForbiddenEthnicities() != null && me.getForbiddenEthnicities().contains(candidate.getEthnicity())) {
                return 0;
        }
        return 100; // ניתן ניקוד מלא כי אין לו בעיה עם העדה של הצד השני
    }

    private double calcSmokingScore(User me, User candidate) {
        if(me.isSmokingDealBreaker() && candidate.isSmoker())
            return 0; // פסילה מיידית ... אסור שיקרה מצב כזה

        return 100; 
    }
    
    // פונקציה מיוחדת לבדיקת פסילות (ילדים, חיות)
    private boolean isDealBreaker(User me, User candidate) {

        // 1. ילדים
        if (me.isRejectsChildren() && candidate.getNumberOfChildren() > 0)
            return true;
        // 2. חיות
        if (me.isRejectsPets() && candidate.isHasPets())
            return true;
        // 3. עישון - אם זה קו אדום והצד השני מעשן
        if (me.isSmokingDealBreaker() && candidate.isSmoker())
            return true;
        // 4. תואר (אם הגדרתי כחובה ואין לצד השני)
        if (me.isRequiresDegree() && !candidate.isHasDegree())
            return true;

        // דרישות נוספות (טכנולוגיה, צבא, כשרות)

        // אם אני דורש טלפון מסונן והצד השני הצהיר שאין לו (isTechnological = false)
        if (me.isRequiresTechnological() && !candidate.isTechnological())
            return true;

        // אם אני דורש שירות צבאי/לאומי והצד השני לא עשה
        if (me.isRequiresMilitaryService() && !candidate.isMilitaryService())
            return true;

        // אם אני דורש כשרות מהדרין והצד השני לא מקפיד
        if (me.isRequiresStrictKashrut() && !candidate.isStrictKashrut())
            return true;

        return false; // אין פסילה - ניתן להמשיך לחישוב ציון WSM
    }

    /* פרק 4 - סקאלות של אופי */

    public double calcSingleScaleScore(int val1, int val2) {

        int diff = Math.abs(val1 - val2);

        // בסקאלה של 1-5, כל הפרש של נקודה שווה 25% ירידה בציון

        return Math.max(0, 100 - (diff * 25));
    }

    public double calcPersonalityScore(User me , User her) {
        // מוחצנות , סדר , רגש, נעימות ,פתיחות

        double score1 = calcSingleScaleScore(me.getScaleExtraversion(), her.getScaleExtraversion());
        double score2 = calcSingleScaleScore(me.getScaleOrderliness(), her.getScaleOrderliness());
        double score3 = calcSingleScaleScore(me.getScaleEmotional(), her.getScaleEmotional());
        double score4 = calcSingleScaleScore(me.getScaleAgreeableness(), her.getScaleAgreeableness());
        double score5 = calcSingleScaleScore(me.getScaleOpenness(), her.getScaleOpenness());

        return (score1 * WEIGHT_SCALE_EXTRAVERSION) + (score2 * WEIGHT_SCALE_ORDERNESS) + (score3 * WEIGHT_SCALE_EMOTION) 
        + (score4 * WEIGHT_SCALE_STUBBORNESS) + (score5 * WEIGHT_SCALE_OPENNESS);
    }

    /* (פונקציית חיבור של שני הפרקים האחרונים !  (אישיות + העדפות  */

    public double calculateTotalScore(User me , User her) {
        return (calcPersonalityScore(me, her) * WEIGHT_PERSONALITY) + (calcPreferencesScore(me, her) * WEIGHT_PREFERENCES);
    }

}