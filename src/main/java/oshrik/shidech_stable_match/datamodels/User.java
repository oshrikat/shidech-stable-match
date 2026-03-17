package oshrik.shidech_stable_match.datamodels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import oshrik.shidech_stable_match.utilities.Location;
import oshrik.shidech_stable_match.utilities.Range;
import oshrik.shidech_stable_match.utilities.ScorePair;

/**
 * מחלקה המייצגת משתמש במערכת.
 * משלבת את נתוני ההתחברות, הפרופיל, ההעדפות והמצב באלגוריתם השידוכים.
 */
@Document(collection = "Users")
public class User {

    // ==========================================
    // ENUMS - טיפוסי נתונים מוגדרים מראש
    // ==========================================

    /**
     * Enum המייצג את המגדר של המשתמש.
     * משמש לסינון בסיסי וקריטי בשידוכים.
     */
    public enum Gender {
        MALE, // זכר
        FEMALE // נקבה
    }

    /**
     * Enum המייצג את המצב המשפחתי.
     * משפיע על שאלות המשך כמו "האם יש ילדים".
     */
    public enum MaritalStatus {
        SINGLE, // רווק/ה
        DIVORCED, // גרוש/ה
        WIDOWED // אלמן/ה
    }

    /**
     * Enum המייצג את הרמה הדתית.
     * משמש לחישוב התאמה בשיטת Whitelist (בדיקה האם קיים ברשימה המותרת).
     */
    public enum ReligiousLevel {
        SECULAR, // חילוני
        TRADITIONAL, // מסורתי
        RELIGIOUS, // דתי
        HAREDI, // חרדי
        DOS // דוס (אופציונלי, לפי האפיון שלך)
    }

    /**
     * Enum המייצג את העיסוק הנוכחי.
     * משמש להתאמה לפי סגנון חיים (סטודנט עם סטודנטית וכו').
     */
    public enum Occupation {
        SOLDIER, // חייל/ת
        STUDENT, // סטודנט/ית
        EMPLOYEE, // שכיר/ה
        SELF_EMPLOYED, // עצמאי/ת
        UNEMPLOYED // לא עובד/ת
    }

    /**
     * Enum המייצג עדה/מוצא.
     * משמש בעיקר לסינון (Blacklist) אם המשתמש ביקש להימנע מעדות מסוימות.
     */
    public enum Ethnicity {
        ASHKENAZI, // אשכנזי
        SEPHARDI, // ספרדי
        MIZRAHI, // מזרחי
        ETHIOPIAN, // אתיופי
        OTHER // אחר
    }

    // ==========================================
    // פרק 0: נתוני מערכת והתחברות (DB & Auth)
    // ==========================================

    @Id
    private String id;
    private String username;
    private String password;

    // ==========================================
    // פרק 1: היכרות בסיסית וזהות (Identity)
    // ==========================================

    private String firstName;
    private String lastName;
    private Date birthDate;
    private Gender gender;

    private Location address; // יחזור לפעולה כשניצור את מחלקת Location
    private String photoUrl;
    private MaritalStatus maritalStatus;
    private boolean hasChildren;

    // ==========================================
    // פרק 2: אפיון המשתמש (User Data)
    // ==========================================

    private ReligiousLevel religiousLevel;
    private Occupation occupation;
    private Ethnicity ethnicity;
    private boolean isSmoker;
    private int height;
    private boolean hasPets;
    private boolean hasDegree;

    // ==========================================
    // פרק 3: העדפות חיפוש (Preferences)
    // ==========================================

    private ArrayList<ReligiousLevel> allowedReligiousLevels;
    private Range ageRange;
    private ArrayList<Occupation> allowedOccupations;
    private int maxDistanceKm;
    private Range heightRange;
    private boolean requiresDegree;
    private boolean smokingDealBreaker;
    private ArrayList<Ethnicity> forbiddenEthnicities;
    private boolean rejectsChildren;
    private boolean rejectsPets;

    // ==========================================
    // פרק 4: אישיות (Personality - Big 5)
    // ==========================================

    private int scaleExtraversion;
    private int scaleOrderliness;
    private int scaleEmotional;
    private int scaleAgreeableness;
    private int scaleOpenness;

    // ==========================================
    // פרק 5: מצב באלגוריתם (Algorithm State - לא נשמר ב-DB)
    // ==========================================

    /*
     * @Transient אומר ל-Spring Data: "זה משתנה לוגי בלבד, אל תשמור במונגו!"
     */

    @Transient
    private ArrayList<ScorePair> preferencesScores = new ArrayList<>();

    @Transient
    private User currentPartner;

    @Transient
    private int proposalIndex = 0;

    // ==========================================
    // בנאים (Constructors)
    // ==========================================

    /**
     * בנאי ריק (Default Constructor) - חובה עבור שליפה מ-MongoDB.
     */
    public User() {
    }

    /**
     * בנאי אתחול בסיסי למשתמש חדש (התחברות).
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // ==========================================
    // פעולות אלגוריתם (Algorithm Logic)
    // ==========================================

    /** מחשב את גיל המשתמש בשנים שלמות לפי תאריך הלידה */
    public int getAge() {
        if (birthDate == null)
            return 0;
        long diff = new Date().getTime() - birthDate.getTime();
        return (int) (diff / (1000L * 60 * 60 * 24 * 365.25));
    }

    /** מחזיר את השם המלא לתצוגה */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /** האם המשתמש רווק כרגע (בזמן ריצת האלגוריתם)? */
    public boolean isFree() {
        return currentPartner == null;
    }

    /** שולף את ההצעה הבאה בתור, ומקדם את האינדקס */

    public ScorePair getNextProposalCandidate() {
        int next = proposalIndex++;
        if (next < this.preferencesScores.size())
            return this.preferencesScores.get(next);
        else
            return null;
    }

    /** יוצר אירוסין עם משתמש אחר */
    public void engage(User currMatch) {
        this.currentPartner = currMatch;
    }

    /** מבטל את האירוסין (גירושין) */
    public void divorce() {
        this.currentPartner = null;
    }

    /**
     * * בודק האם שווה למשתמש להחליף את בן/בת הזוג הנוכחי במועמד הפוטנציאלי
     */
    public boolean isBetter(User potential) {
        int indexCurrent = findIndexAtMePrefList(this.currentPartner);
        int indexPotential = findIndexAtMePrefList(potential);

        // ככל שהאינדקס נמוך יותר, המועמד אהוב יותר (נמצא גבוה יותר ברשימה)
        return indexPotential < indexCurrent;
    }

    /** מוצא את מיקום המועמד ברשימת ההעדפות שלי */
    private int findIndexAtMePrefList(User candToCheck) {

        for (int i = 0; i < this.preferencesScores.size(); i++) {
            if (this.preferencesScores.get(i).getCandidate().equals(candToCheck)) {
                return i;
            }
        }

        return -1;
    }

    // ==========================================
    // Getters & Setters
    // ==========================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public ReligiousLevel getReligiousLevel() {
        return religiousLevel;
    }

    public void setReligiousLevel(ReligiousLevel religiousLevel) {
        this.religiousLevel = religiousLevel;
    }

    public Occupation getOccupation() {
        return occupation;
    }

    public void setOccupation(Occupation occupation) {
        this.occupation = occupation;
    }

    public Ethnicity getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(Ethnicity ethnicity) {
        this.ethnicity = ethnicity;
    }

    public boolean isSmoker() {
        return isSmoker;
    }

    public void setSmoker(boolean smoker) {
        isSmoker = smoker;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isHasPets() {
        return hasPets;
    }

    public void setHasPets(boolean hasPets) {
        this.hasPets = hasPets;
    }

    public boolean isHasDegree() {
        return hasDegree;
    }

    public void setHasDegree(boolean hasDegree) {
        this.hasDegree = hasDegree;
    }

    public ArrayList<ReligiousLevel> getAllowedReligiousLevels() {
        return allowedReligiousLevels;
    }

    public void setAllowedReligiousLevels(ArrayList<ReligiousLevel> allowedReligiousLevels) {
        this.allowedReligiousLevels = allowedReligiousLevels;
    }

    public ArrayList<Occupation> getAllowedOccupations() {
        return allowedOccupations;
    }

    public void setAllowedOccupations(ArrayList<Occupation> allowedOccupations) {
        this.allowedOccupations = allowedOccupations;
    }

    public int getMaxDistanceKm() {
        return maxDistanceKm;
    }

    public void setMaxDistanceKm(int maxDistanceKm) {
        this.maxDistanceKm = maxDistanceKm;
    }

    public boolean isRequiresDegree() {
        return requiresDegree;
    }

    public void setRequiresDegree(boolean requiresDegree) {
        this.requiresDegree = requiresDegree;
    }

    public boolean isSmokingDealBreaker() {
        return smokingDealBreaker;
    }

    public void setSmokingDealBreaker(boolean smokingDealBreaker) {
        this.smokingDealBreaker = smokingDealBreaker;
    }

    public ArrayList<Ethnicity> getForbiddenEthnicities() {
        return forbiddenEthnicities;
    }

    public void setForbiddenEthnicities(ArrayList<Ethnicity> forbiddenEthnicities) {
        this.forbiddenEthnicities = forbiddenEthnicities;
    }

    public boolean isRejectsChildren() {
        return rejectsChildren;
    }

    public void setRejectsChildren(boolean rejectsChildren) {
        this.rejectsChildren = rejectsChildren;
    }

    public boolean isRejectsPets() {
        return rejectsPets;
    }

    public void setRejectsPets(boolean rejectsPets) {
        this.rejectsPets = rejectsPets;
    }

    public int getScaleExtraversion() {
        return scaleExtraversion;
    }

    public void setScaleExtraversion(int scaleExtraversion) {
        this.scaleExtraversion = scaleExtraversion;
    }

    public int getScaleOrderliness() {
        return scaleOrderliness;
    }

    public void setScaleOrderliness(int scaleOrderliness) {
        this.scaleOrderliness = scaleOrderliness;
    }

    public int getScaleEmotional() {
        return scaleEmotional;
    }

    public void setScaleEmotional(int scaleEmotional) {
        this.scaleEmotional = scaleEmotional;
    }

    public int getScaleAgreeableness() {
        return scaleAgreeableness;
    }

    public void setScaleAgreeableness(int scaleAgreeableness) {
        this.scaleAgreeableness = scaleAgreeableness;
    }

    public int getScaleOpenness() {
        return scaleOpenness;
    }

    public void setScaleOpenness(int scaleOpenness) {
        this.scaleOpenness = scaleOpenness;
    }

    public User getCurrentPartner() {
        return currentPartner;
    }

    public void setCurrentPartner(User currentPartner) {
        this.currentPartner = currentPartner;
    }

    public int getProposalIndex() {
        return proposalIndex;
    }

    public void setProposalIndex(int proposalIndex) {
        this.proposalIndex = proposalIndex;
    }

    // --- השלמות עבור פעולות האלגוריתם ---

    public Location getAddress() {
        return address;
    }

    public void setAddress(Location address) {
        this.address = address;
    }

    public Range getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(Range ageRange) {
        this.ageRange = ageRange;
    }

    public Range getHeightRange() {
        return heightRange;
    }

    public void setHeightRange(Range heightRange) {
        this.heightRange = heightRange;
    }

    public ArrayList<ScorePair> getPreferencesScores() {
        return preferencesScores;
    }

    public void setPreferencesScores(ArrayList<ScorePair> preferencesScores) {
        this.preferencesScores = preferencesScores;
    }

}