package oshrik.shidech_stable_match.datamodels;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * מחלקה המייצגת משתמש במערכת.
 * ממופה לאוסף "Users" במסד הנתונים MongoDB.
 * * @author Oshri Kataribas
 * 
 * @date 2026-02-24
 */
@Document(collection = "Users")
public class User2 {

    @Id
    private String id;

    private String username;
    private String password;

    /**
     * בנאי ריק (Default Constructor)
     * נדרש על ידי Spring Data MongoDB כדי לייצר אובייקטים בעת שליפה ממסד הנתונים.
     */
    public User2() {
    }

    /**
     * בנאי אתחול עם שם משתמש וסיסמה.
     * 
     * @param userName שם המשתמש
     * @param passWord סיסמת המשתמש
     */
    public User2(String userName, String passWord) {
        this.username = userName;
        this.password = passWord;
    }

    /**
     * Copy Constructor - בנאי העתקה
     * 
     * @param user אובייקט משתמש להעתקה
     */
    public User2(User2 user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
    }

    // --- Getters and Setters ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passWord) {
        this.password = passWord;
    }
}