package oshrik.shidech_stable_match.services;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.repositories.UserRepository;

@Service
public class UserService 
{

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    // החזרת ArrayList כמו שביקשת (בצורה בטוחה)
    // R (Read)
    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(userRepository.findAll());
    }

    // returns the names that like the parameter name
    public ArrayList<User> getAllUsersLikeName(String name) {
        return new ArrayList<>(userRepository.findByUsernameLike(name));
    }


    // U (Update)
    public void updateUserPassword(String username, String newPassword) {
        User user = userRepository.findById(username).orElse(null);
        if (user != null) {
            user.setPassword(newPassword);
            userRepository.save(user);
        }
    }

    // עדכון פרופיל מלא של משתמש (למשל אחרי סיום השאלון)
    public void updateFullUser(User u) {
        // מונגו מזהה את המשתמש ופשוט שומר את כל האובייקט המעודכן עליו
        userRepository.save(u);
    }

    public ArrayList<User> findByUsernameLike(User user) {
        return (ArrayList<User>) userRepository.findByUsernameLike(user.getUsername());

    }

    // isUserExist(User) / (username,pass)
    public boolean isUserExist(User user)// String name, String password
    {
        return userRepository.findOneByUsernameAndPassword(user.getUsername(), user.getPassword()) != null;
    }

    public User getUserByNameAndPassword(String us, String pw) {
        return userRepository.findOneByUsernameAndPassword(us, pw);
    }


    // D (delete)
    // create a delete function
    public void deleteUser(User user) {
        // אין צורך לבדוק אם המשתמש קיים - כי בעצם לחצנו עליו
        userRepository.delete(user);

    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    /**
     * פונקציה שנועדה לבצע חיבור למשתמש שרשום למערכת
     * 
     * @param email    - האימייל של אותו משתמש שהוא פוטנציאל רשום בתוך הבסיס נתונים
     * @param password - הסיסמא של המשתמש שמנסה להתחבר
     * @return - אם המשתמש רשום , נחזיר אמת ונכניס אותו למשתמש , נבצע חיבור , אחרת
     *         נחזיר שגיאה , יוחזר שקר
     */
    public User loginUser(String email, String password) {
        User userCheck = userRepository.findOneByEmail(email);

        if (userCheck != null) {
            // נבצע התחברות

            // 1. בדיקת סיסמא
            if (userCheck.getPassword().equals(password)) {
                return userCheck;
            }
        }

        return null;
    }

    /**
     * פוקנציה אשר מבצעת רישום משתמש בסיסי למסד הנתונים לצורך כניסה ראשונית
     * 
     * @param newUser - המשתמש שרוצה להירשם
     * @return מחזיר אמת כאשר ההרשמה בוצעה בהצלחה , אחרת מחזיר שקר כי קיים משתמש כזה
     */
    public boolean registerNewUser(User newUser) {

        if (!(userRepository.existsByEmail(newUser.getEmail()))) {
            // אין משתמש כזה , לכן נוכל להכניסו למסד הנתונים בהצלחה
            userRepository.insert(newUser);
            return true;

        }

        // המשתמש קיים , לכן לא נוכל לבצע הרשמה

        return false;
    }

}
