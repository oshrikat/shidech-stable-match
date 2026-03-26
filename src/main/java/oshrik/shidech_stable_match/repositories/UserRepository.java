package oshrik.shidech_stable_match.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.datamodels.User.Gender;

/**
 * ממשק לניהול פעולות CRUD מול אוסף המשתמשים ב-MongoDB.
 * יורש מ-MongoRepository המספק פעולות בסיסיות מובנות.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * שליפת כל המשתמשים בעלי סיסמה זהה.
     * 
     * @param password הסיסמה לחיפוש
     * @return רשימת משתמשים מתאימים
     */
    public List<User> findAllByPassword(String password);

    /**
     * שליפת משתמשים לפי שם משתמש מדויק.
     * 
     * @param name שם המשתמש לחיפוש
     * @return רשימת משתמשים מתאימים
     */
    public List<User> findAllByUsername(String name);

    /**
     * שליפת משתמשים שהשם שלהם מכיל מחרוזת מסוימת (חיפוש גמיש).
     * 
     * @param name המחרוזת לחיפוש
     * @return רשימת משתמשים מתאימים
     */
    public List<User> findByUsernameLike(String name);

    /**
     * שליפת משתמש בודד על בסיס התאמה של שם משתמש וסיסמה (למשל עבור התחברות -
     * Login).
     * 
     * @param un שם המשתמש
     * @param pw הסיסמה
     * @return אובייקט המשתמש אם נמצא, אחרת null
     */
    public User findOneByUsernameAndPassword(String un, String pw);

    // האם השתמש משתמש כבר קיים ?
    public boolean existsByUsername(String userName);

    // פונקציה לשליפת כל המשתמשים שרלוונטיים לשידוך
    public List<User> findByGenderAndRoleAndStatus(User.Gender gender, User.ROLE role, User.UserStatus status);

    /**
     * פונקציה למציאת משתמש במסד הנתונים לפי מזהה האימייל
     * 
     * @param email - האימייל שאיתו נזהה ונשלוף את המשתמש הרצוי ממסד הנתונים
     * @return החזרת המשתמש אם קיים , במידה ולא יוחזר null
     */
    public User findOneByEmail(String email);

    /**
     * פונקציה אשר בודקת האם האימייל קיים בבסיס נתונים - האם קיים משתמש עם האימייל
     * אסור שיהיו 2 משתמשים עם אותו האימייל
     * 
     * @param email - האימייל שאותו מחפשים בתוך הבסיס נתונים
     * @return נחזיר אמת כאשר יש משתמש כזה אחרת , במקרה ובו האימייל לא קיים נחזיר
     *         שקר
     */
    public boolean existsByEmail(String email);

}