package oshrik.shidech_stable_match.utilities;

import oshrik.shidech_stable_match.datamodels.User;

/**
 * מחלקה השומרת צמד של מועמד + הציון שלו, ומאפשרת למיין אותם.
 */
public class ScorePair implements Comparable<ScorePair> {
    
    private User candidate; // שונה ל-User!
    private double score;

    public ScorePair(User candidate, double score) {
        this.candidate = candidate;
        this.score = score;
    }

    public User getCandidate() { return candidate; }
    public double getScore() { return score; }

    @Override
    public int compareTo(ScorePair other) {
        // אנחנו רוצים שכאשר הציון שלי גבוה יותר, הפונקציה תחזיר שלילי
        // כדי שהמחשב ישים אותי בהתחלה של התור (סדר יורד).
        return Double.compare(other.score, this.score); 
    }
    
    @Override
    public String toString() {
        return candidate.getFullName() + " (" + String.format("%.2f", score) + ")";
    }
}