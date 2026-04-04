package oshrik.shidech_stable_match.datamodels;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * מחלקה המייצגת שידוך סופי שנוצר על ידי אלגוריתם גייל-שפלי.
 * המחלקה שומרת את מצב השידוך, תאריך היצירה ואת תגובות הצדדים (אישורים במייל).
 */
@Document(collection = "Matches")
    public class Match {

        // ==========================================
        // ENUMS - טיפוסי נתונים מוגדרים מראש
        // ==========================================

        /**
         * Enum המייצג את הסטטוס הנוכחי של השידוך הספציפי הזה.
         */
        public enum MatchStatus {

            /** ממתין לתשובות מהצדדים (המיילים נשלחו) */
            PENDING_RESPONSES,

            /** הם יוצאים כרגע ! */
            ACTIVE_DATING,

            /** אחד הצדדים או שניהם סירבו (או שהקשר לא צלח) */
            REJECTED,

            /** מזל טוב! הקשר הסתיים בחתונה/אירוסין */
            ENGAGED,

            /** שני הצדדים אישרו - שלב לפני החלפת פרטים - שלב צ'אט היכרות */
            PRE_DATING_EVALUATION
        }

        // ==========================================
        // פרק 1: מזהים בסיסיים ונתוני אלגוריתם
        // ==========================================

        @Id
        private String id;
        
        private String manId; // מזהה הגבר
        private String womanId; // מזהה האישה
        
        private double matchScore; // הציון שבגללו האלגוריתם בחר לשדך ביניהם
        private LocalDateTime matchDate; // מתי האלגוריתם יצר את השידוך הזה

        // ==========================================
        // פרק 2: ניהול תהליך ומעקב (אימיילים)
        // ==========================================

        // משתמשים ב-Boolean (עטיפה) ולא boolean רגיל, כדי שיוכל להיות null כל עוד לא ענו
        private Boolean manAgreed; 
        private Boolean womanAgreed;
        
        private MatchStatus status; // הסטטוס הנוכחי של הקשר

        // ==========================================
        // בנאים (Constructors)
        // ==========================================

        /**
         * בנאי ריק חובה עבור Spring Data MongoDB.
         */
        public Match() {
        }

        /**
         * בנאי ליצירת שידוך חדש מיד לאחר סיום ריצת גייל-שפלי.
         */
        public Match(String manId, String womanId, double matchScore) {
            this.manId = manId;
            this.womanId = womanId;
            this.matchScore = matchScore;
            
            this.matchDate = LocalDateTime.now(); // חותמת זמן של הרגע הזה
            this.status = MatchStatus.PENDING_RESPONSES; // מתחילים תמיד בהמתנה לתשובה
            
            // אתחול לתשובות ריקות (עוד לא ענו)
            this.manAgreed = null;
            this.womanAgreed = null;
        }

        // ==========================================
        // Getters & Setters
        // ==========================================

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getManId() { return manId; }
        public void setManId(String manId) { this.manId = manId; }

        public String getWomanId() { return womanId; }
        public void setWomanId(String womanId) { this.womanId = womanId; }

        public double getMatchScore() { return matchScore; }
        public void setMatchScore(double matchScore) { this.matchScore = matchScore; }

        public LocalDateTime getMatchDate() { return matchDate; }
        public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }

        public Boolean getManAgreed() { return manAgreed; }
        public void setManAgreed(Boolean manAgreed) { this.manAgreed = manAgreed; }

        public Boolean getWomanAgreed() { return womanAgreed; }
        public void setWomanAgreed(Boolean womanAgreed) { this.womanAgreed = womanAgreed; }

        public MatchStatus getStatus() { return status; }
        public void setStatus(MatchStatus status) { this.status = status; }
        
    }