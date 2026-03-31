package oshrik.shidech_stable_match.services;

import org.springframework.stereotype.Service;
import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.utilities.ScorePair;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Service
public class GaleShapleyAlgoService {

    /**
     * אלגוריתם הליבה - גייל שייפלי
     * מקבל את רשימות הגברים והנשים (לאחר שחושבו ומוינו להם ההעדפות) ומבצע את השידוך.
     * * @param mens - רשימת הגברים
     * @param womens - רשימת הנשים
     */
    public void runGaleShapley(List<User> mens, List<User> womens) 
    {
        System.out.println("Algorithm Gale-Shapley Started...");

        // 1. יצירת התור: מעבירים את כל הגברים מהרשימה לתור של "פנויים"
        Queue<User> freeMens = new LinkedList<>(mens);
       
        // 2. הלולאה הראשית: כל עוד יש מישהו בתור
        while (!freeMens.isEmpty()) {
            // שליפת הגבר הראשון בתור  
            User currMan = freeMens.poll(); 
                        
            // שולפים את ההצעה הבאה (האובייקט הזוגי - כולל ציון ההתאמה)
            ScorePair nextProposal = currMan.getNextProposalCandidate();

            // בדיקה: האם נגמרו לו הנשים להציע להן?
            if (nextProposal == null) {
                // הגבר הזה סיים את כל האפשרויות שלו ונשאר רווק.
                // לא מחזירים אותו לתור (freeMens) וממשיכים לגבר הבא.
                continue; 
            }

            // אם יש הצעה, מושכים את האישה מתוכה
            User currWoman = nextProposal.getCandidate();

            if (currWoman != null) {
                if (currWoman.isFree()) {
                    // האישה רווקה, לכן נשדך אותם
                    currWoman.engage(currMan);
                    currMan.engage(currWoman);
                } else {
                    // האישה לא רווקה לכן נבדוק מי טוב יותר:
                    User manCurrentPartner = currWoman.getCurrentPartner();

                    // האם השידוך המוצע טוב יותר ממה שקיים כרגע?
                    if (currWoman.isBetter(currMan)) {
                        // השידוך המוצע טוב יותר - נבצע החלפה
                        currWoman.engage(currMan);
                        currMan.engage(currWoman); // הגבר שודך למישהי, נסמן שהוא כבר לא רווק
                        
                        // הגבר שהיה משודך כעת רווק כי שברנו את השידוך שלו, נבטל לו את השידוך הנוכחי
                        manCurrentPartner.divorce();

                        // נחזיר את הגבר שהוחלף לשוק הרווקים
                        freeMens.add(manCurrentPartner);
                    } else {
                        // השידוך המוצע לא טוב יותר
                        // נחזיר את הגבר שהציע לשוק הרווקים כדי שינסה את האישה הבאה בתור
                        freeMens.add(currMan);
                    }
                }
            }
        }
        
        System.out.println("Algorithm Gale-Shapley Finished!");
    }
}