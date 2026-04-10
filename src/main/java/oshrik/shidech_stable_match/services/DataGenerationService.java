package oshrik.shidech_stable_match.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import org.springframework.stereotype.Service;

import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.datamodels.User.*;
import oshrik.shidech_stable_match.repositories.UserRepository;
import oshrik.shidech_stable_match.utilities.Location;
import oshrik.shidech_stable_match.utilities.Range;

@Service
public class DataGenerationService 
{

    private final UserRepository userRepository;
    private final Random rand = new Random();

    // מאגרי שמות
    private final String[] MALE_NAMES = {"Yossi", "David", "Moshe", "Dan", "Ronen", "Eli", "Avi", "Noam", "Itay", "Omer"};
    private final String[] FEMALE_NAMES = {"Sarah", "Rachel", "Leah", "Dana", "Noa", "Maya", "Tamar", "Adi", "Michal", "Yael"};
    private final String[] LAST_NAMES = {"Cohen", "Levi", "Mizrahi", "Peretz", "Biton", "Friedman", "Katz", "Azulai"};

    // הזרקת תלויות (Dependency Injection) - מחברים את ה-Repository לשירות
    public DataGenerationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * פונקציה ראשית שמייצרת ושומרת כמות מבוקשת של משתמשים מכל מגדר
     */
    public void generateAndSaveUsers(int amountPerGender) {
        // מחיקת משתמשים קודמים כדי להתחיל נקי (אופציונלי, אבל מומלץ בטסטים)
        userRepository.deleteAll();

        for (int i = 0; i < amountPerGender; i++) {
            User man = createRandomUser(Gender.MALE);
            User woman = createRandomUser(Gender.FEMALE);
            
            userRepository.save(man);
            userRepository.save(woman);
        }
        System.out.println("Successfully generated and saved " + (amountPerGender * 2) + " users to MongoDB.");
    }

    /**
     * פונקציית העזר שמייצרת משתמש יחיד (הותאמה לפורמט של User)
     */
    private User createRandomUser(Gender gender) {
        User u = new User();

        // נתוני מערכת והתחברות (חובה בשביל Vaadin ו-DB)
        String firstName = (gender == Gender.MALE) ? pickName(MALE_NAMES) : pickName(FEMALE_NAMES);
        String lastName = pickName(LAST_NAMES);
        
        u.setFirstName(firstName);
        u.setLastName(lastName);
        // יצירת שם משתמש ייחודי כדי שלא יהיו התנגשויות
        u.setUsername(firstName.toLowerCase() + "_" + rand.nextInt(10000)); 
        u.setPassword("1234"); // סיסמה אחידה לטסטים

        u.setGender(gender);
        
        // גיל ותאריך לידה (שנות ה-80 וה-90)
        int birthYear = 1985 + rand.nextInt(19); 
        u.setBirthDate(LocalDate.of(birthYear, rand.nextInt(12) + 1, 1));

        // מיקום גיאוגרפי
        u.setAddress(new Location(32.0 + rand.nextDouble(), 34.7 + rand.nextDouble())); 
        
        u.setHeight(150 + rand.nextInt(40)); 
        u.setSmoker(rand.nextBoolean());
        u.setHasDegree(rand.nextBoolean());
        
        // נתונים בסיסיים מתוך Enums
        u.setReligiousLevel(pickRandom(ReligiousLevel.class));
        u.setOccupation(pickRandom(Occupation.class));
        u.setEthnicity(pickRandom(Ethnicity.class));
        u.setMaritalStatus(pickRandom(MaritalStatus.class));
        
        boolean kids = (u.getMaritalStatus() != MaritalStatus.SINGLE) && rand.nextBoolean();
        // u.setHasChildren(kids);
        u.setHasPets(rand.nextBoolean());

        // --- העדפות ---
        int minAge = 20 + rand.nextInt(15);
        int maxAge = minAge + 5 + rand.nextInt(15);
        u.setAgeRange(new Range(minAge, maxAge));
        u.setHeightRange(new Range(150, 190)); 
        u.setMaxDistanceKm(10 + rand.nextInt(90)); 
        
        u.setAllowedReligiousLevels(createRandomList(ReligiousLevel.class));
        u.setAllowedOccupations(createRandomList(Occupation.class));
        u.setForbiddenEthnicities(new ArrayList<>()); 

        // Deal Breakers
        u.setRequiresDegree(rand.nextBoolean());
        u.setSmokingDealBreaker(rand.nextBoolean());
        u.setRejectsChildren(rand.nextBoolean());
        u.setRejectsPets(rand.nextBoolean());

        // --- אישיות (Big 5) ---
        u.setScaleExtraversion(1 + rand.nextInt(5));
        u.setScaleOrderliness(1 + rand.nextInt(5));
        u.setScaleEmotional(1 + rand.nextInt(5));
        u.setScaleAgreeableness(1 + rand.nextInt(5));
        u.setScaleOpenness(1 + rand.nextInt(5));

        return u;
    }

    // --- Helpers ---
    private String pickName(String[] names) {
        return names[rand.nextInt(names.length)];
    }

    private <T extends Enum<?>> T pickRandom(Class<T> clazz) {
        T[] values = clazz.getEnumConstants();
        return values[rand.nextInt(values.length)];
    }

    private <T extends Enum<?>> ArrayList<T> createRandomList(Class<T> clazz) {
        ArrayList<T> list = new ArrayList<>();
        T[] values = clazz.getEnumConstants();
        int numberOfItems = 1 + rand.nextInt(values.length); 
        for (int i = 0; i < numberOfItems; i++) {
            T randomItem = values[rand.nextInt(values.length)];
            if (!list.contains(randomItem)) {
                list.add(randomItem);
            }
        }
        return list;
    }
}