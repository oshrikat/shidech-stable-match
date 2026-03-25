package oshrik.shidech_stable_match.datamodels;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * מחלקה המייצגת ציון התאמה בין שני משתמשים במערכת (גבר ואישה).
 */
@Document(collection = "MatchScores")
public class MatchScore {

    @Id
    private String id;
    
    private String manId;
    private String womanId;
    private double totalScore;
    private LocalDateTime calculatedAt;

    // בנאי ריק - חובה עבור Spring Data MongoDB
    public MatchScore() {
    }

    // בנאי עם נתונים
    public MatchScore(String manId, String womanId, double totalScore) {
        this.manId = manId;
        this.womanId = womanId;
        this.totalScore = totalScore;
        this.calculatedAt = LocalDateTime.now(); // מתעדכן אוטומטית בזמן החישוב
    }

    // --- Getters & Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getManId() {
        return manId;
    }

    public void setManId(String manId) {
        this.manId = manId;
    }

    public String getWomanId() {
        return womanId;
    }

    public void setWomanId(String womanId) {
        this.womanId = womanId;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }


    
}