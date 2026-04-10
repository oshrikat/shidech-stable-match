package oshrik.shidech_stable_match.ui;

import java.io.IOException;
import java.util.ArrayList;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.UploadHandler;

import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.services.UserService;
import oshrik.shidech_stable_match.utilities.Location;
import oshrik.shidech_stable_match.utilities.Range;
import oshrik.shidech_stable_match.utilities.RouteHelper;
import oshrik.shidech_stable_match.utilities.SessionHelper;

@Route(value = "/wizard")
public class WizardView extends VerticalLayout implements BeforeEnterObserver {

    // פרק 1: User Data
    private TextField firstNameField;
    private TextField lastNameField;
    private DatePicker birthDateField;
    private ComboBox<User.Gender> genderComboBox;
    private ComboBox<User.MaritalStatus> maritalStatusComboBox;
    private TextField phoneField;
    private TextField cityField;
    private IntegerField numberOfChildrenField;

    // ---> השדות לשמירת התמונת פרופיל של המשתמש <---
    private Upload uploadProfileImage;
    private String base64ImageString = null; // ישמור את התמונה המקודדת

    // פרק 2: User Data
    private ComboBox<User.ReligiousLevel> religiousLevelBox;
    private ComboBox<User.Occupation> occupationBox;
    private ComboBox<User.Ethnicity> ethnicityBox;
    private Checkbox isSmokerBox;
    private IntegerField heightField;
    private Checkbox hasPetsBox;
    private Checkbox hasDegreeBox;

    // פרק 3:
    // Preferences :
    private MultiSelectComboBox<User.ReligiousLevel> allowedReligiousLevelsBox;
    private IntegerField minAgeField; // פיצלנו את ה-Range לשני שדות ב-UI
    private IntegerField maxAgeField;
    private MultiSelectComboBox<User.Occupation> allowedOccupationsBox;
    private IntegerField minHeightField;
    private IntegerField maxHeightField;
    private IntegerField maxDistanceField;
    private MultiSelectComboBox<User.Ethnicity> forbiddenEthnicitiesBox;

    // Checkboxes (Deal-Breakers)
    private Checkbox requiresDegreeBox;
    private Checkbox smokingDealBreakerBox;
    private Checkbox rejectsChildrenBox;
    private Checkbox rejectsPetsBox;


    // פרק 4: Personality
    private IntegerField scaleExtraversionField;
    private IntegerField scaleOrderlinessField;
    private IntegerField scaleEmotionalField;
    private IntegerField scaleAgreeablenessField;
    private IntegerField scaleOpennessField;

    private User currUser;

    private UserService userService; // הזרקת שירות משתמשים

    // הזרקת התלות לבנאי
    public WizardView(UserService userService) {
        this.userService = userService;
        setAlignItems(Alignment.CENTER);
        getStyle().set("direction", "rtl");

        H1 title = new H1("בואו נכיר קצת יותר לעומק!  ");

        add(title, create_Part_1_IdentitySection(), create_Part_2_UserDataSection(), create_Part_3_PreferencesSection(), create_Part_4_PersonalitySection());

        //   כפתור סיום ושמירה
        Button finishBtn = new Button("סיום ושמירה!", e -> {
            
            // 1. בדיקת חובה בסיסית
            if (!checkPart1() || !checkPart2() || !checkPart3() || !checkPart4()) {
                Notification.show("נא מלאו את כל הפרטים!", 4000, Notification.Position.MIDDLE);
                return; 
            }

            // 2. עדכון אובייקט המשתמש (משיכת הנתונים מה-Session)
            currUser = (User) SessionHelper.getAttribute("currentUser");
            
            if(currUser != null) {
                /* פרק 1 */

                // שם פרטי
                currUser.setFirstName(firstNameField.getValue());
                // שם משפחה
                currUser.setLastName(lastNameField.getValue());
                // תאריך לידה
                currUser.setBirthDate((birthDateField.getValue())); 
                // מגדר
                currUser.setGender(genderComboBox.getValue());
                // ססטוס משפחה
                currUser.setMaritalStatus(maritalStatusComboBox.getValue());
                // טלפון ליצירת קשר
                currUser.setPhone(phoneField.getValue());
                // עיר מגורים
                currUser.setCity(cityField.getValue());

                // כרגע נשמור אותו כטקסט - בהמשך נחשב מיקום בעזרת API
                // Address

                currUser.setAddress(new Location()); // חיבור ל-API של Nominatim
                /*
                 * מה אפשר לעשות בינתיים ? - אפשר לשלוף את המיקום שלו של המחשב ? ממנו הוא מתחבר
                 * בינתיים ? תמיד מבקשים ממני באתרים לאשר מיקום
                 */

                currUser.setNumberOfChildren(
                        numberOfChildrenField.getValue() != null ? numberOfChildrenField.getValue() : 0);

                // ---> שמירת התמונה! <---
                if (base64ImageString != null) {
                    currUser.setPhotoUrl(base64ImageString);
                }

                // =============
                /* פרק 2 */
                // =============

                // רמה דתית
                currUser.setReligiousLevel(religiousLevelBox.getValue());
                // עיסוק
                currUser.setOccupation(occupationBox.getValue());
                // מה המוצא
                currUser.setEthnicity(ethnicityBox.getValue());
                // מה הגובה
                currUser.setHeight(heightField.getValue() != null ? heightField.getValue() : 0);
                // האם מעשן
                currUser.setSmoker(isSmokerBox.getValue());
                // האם יש חיות
                currUser.setHasPets(hasPetsBox.getValue());
                // האם יש תואר
                currUser.setHasDegree(hasDegreeBox.getValue());

                // =============
                /* פרק 3 */
                // =============
                currUser.setAllowedReligiousLevels(new ArrayList<>(allowedReligiousLevelsBox.getValue()));
                currUser.setAllowedOccupations(new ArrayList<>(allowedOccupationsBox.getValue()));
                currUser.setForbiddenEthnicities(new ArrayList<>(forbiddenEthnicitiesBox.getValue()));

                // בניית אובייקטי Range
                if (minAgeField.getValue() != null && maxAgeField.getValue() != null) {
                    currUser.setAgeRange(new Range(minAgeField.getValue(), maxAgeField.getValue()));
                }

                if (minHeightField.getValue() != null && maxHeightField.getValue() != null) {
                    currUser.setHeightRange(new Range(minHeightField.getValue(), maxHeightField.getValue()));
                }

                // שדות פשוטים
                currUser.setMaxDistanceKm(maxDistanceField.getValue() != null ? maxDistanceField.getValue() : 100);
                currUser.setRequiresDegree(requiresDegreeBox.getValue());
                currUser.setSmokingDealBreaker(smokingDealBreakerBox.getValue());
                currUser.setRejectsChildren(rejectsChildrenBox.getValue());
                currUser.setRejectsPets(rejectsPetsBox.getValue());

                // =============
                /* פרק 4 */
                // =============

                currUser.setScaleExtraversion(scaleExtraversionField.getValue() != null ? scaleExtraversionField.getValue() : 5);
                currUser.setScaleOrderliness(scaleOrderlinessField.getValue() != null ? scaleOrderlinessField.getValue() : 5);
                currUser.setScaleEmotional(scaleEmotionalField.getValue() != null ? scaleEmotionalField.getValue() : 5);
                currUser.setScaleAgreeableness(scaleAgreeablenessField.getValue() != null ? scaleAgreeablenessField.getValue() : 5);
                currUser.setScaleOpenness(scaleOpennessField.getValue() != null ? scaleOpennessField.getValue() : 5);

                // 3. שינוי הסטטוס
                currUser.setProfileComplete(true);

                // 4. שמירה במסד הנתונים ועדכון הסשן
                userService.updateFullUser(currUser); 
                SessionHelper.setAttribute("currentUser", currUser);

                // 5. מעבר למסך הבית
                Notification.show("הפרופיל עודכן בהצלחה! ברוך הבא 🥳", 3000, Notification.Position.MIDDLE);
                RouteHelper.navigateTo(UserDashboardView.class);

            }
        });

        finishBtn.getStyle().set("background-color", "#7F77DD");
        finishBtn.getStyle().set("color", "white");
        finishBtn.getStyle().set("margin-top", "20px");
        
        add(finishBtn); 
    }

    private VerticalLayout create_Part_1_IdentitySection() {

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        H3 sectionTitle = new H3("1️⃣ פרטים אישיים");

        // --- שדות פרטים אישיים ---
        firstNameField = new TextField("שם פרטי");
        lastNameField = new TextField("שם משפחה");
        birthDateField = new DatePicker("תאריך לידה");
        genderComboBox = new ComboBox<>("מגדר");
        genderComboBox.setItems(User.Gender.values());

        phoneField = new TextField("מספר טלפון");
        phoneField.setPlaceholder("05X-XXXXXXX");

        cityField = new TextField("עיר מגורים");
        cityField.setHelperText("הזן עיר כדי שנחשב מרחק לשידוך");

        maritalStatusComboBox = new ComboBox<>("מצב משפחתי");
        maritalStatusComboBox.setItems(User.MaritalStatus.values());

        // שדה כמות ילדים
        numberOfChildrenField = new IntegerField("כמות ילדים");
        numberOfChildrenField.setValue(0);
        numberOfChildrenField.setMin(0);
        numberOfChildrenField.setVisible(false); // מוסתר בהתחלה

        // --- לוגיקה דינמית לילדים ---
        maritalStatusComboBox.addValueChangeListener(event -> {
            User.MaritalStatus status = event.getValue();
            // השדה יופיע רק אם המשתמש גרוש או אלמן
            boolean shouldShowChildren = status != User.MaritalStatus.SINGLE;
            numberOfChildrenField.setVisible(shouldShowChildren);
        });

        // ---> הלוגיקה של העלאת התמונה <---
        UploadHandler uploadHandler = (request) -> {
            try {
                // 1. קריאת כל המידע מהזרם (InputStream) לתוך מערך של בתים (byte array)
                byte[] fileBytes = request.getInputStream().readAllBytes();

                // 2. המרת מערך הבתים למחרוזת טקסט בפורמט Base64
                String base64Encoded = java.util.Base64.getEncoder().encodeToString(fileBytes);

                // 3. יצירת המחרוזת הסופית עם הקידומת שהדפדפן צריך כדי להציג תמונה
                base64ImageString = "data:" + request.getContentType() + ";base64,"
                        + base64Encoded;

                getUI().ifPresent(ui -> ui.access(
                        () -> Notification.show("התמונה הועלתה והומרה בהצלחה!", 3000, Notification.Position.MIDDLE)));

            } catch (IOException e) {
                throw new RuntimeException("שגיאה בקריאת הקובץ", e);
            }
        };

        uploadProfileImage = new Upload(uploadHandler);
        uploadProfileImage.setAcceptedFileTypes("image/jpeg", "image/png");
        uploadProfileImage.setMaxFileSize(5 * 1024 * 1024);
        uploadProfileImage.setDropLabel(new Span("גרור תמונת פרופיל לכאן (או לחץ)"));

        // --- סידור ב-FormLayout למראה מקצועי ---
        FormLayout formLayout = new FormLayout();
        formLayout.add(firstNameField, lastNameField, birthDateField, genderComboBox,
                phoneField, cityField, maritalStatusComboBox, numberOfChildrenField);

        // הגדרה שבמסך רחב יהיו 2 עמודות
        formLayout.setResponsiveSteps(
                new ResponsiveStep("0", 1),
                new ResponsiveStep("500px", 2));

        layout.add(sectionTitle, uploadProfileImage, formLayout);
        return layout;
    }


    private VerticalLayout create_Part_2_UserDataSection() {

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        H3 sectionTitle = new H3("2️⃣ קצת עליך (נתונים אישיים)");

        // --- אתחול רכיבים (Instantiate) - חובה לעשות new להכל! ---
        religiousLevelBox = new ComboBox<>("רמה דתית");
        occupationBox = new ComboBox<>("עיסוק נוכחי");
        ethnicityBox = new ComboBox<>("עדה/מוצא");
        heightField = new IntegerField("גובה (בס\"מ)");

        isSmokerBox = new Checkbox("אני מעשן/ת");
        hasPetsBox = new Checkbox("יש לי בעלי חיים");
        hasDegreeBox = new Checkbox("אני בעל/ת תואר אקדמי");

        // --- הגדרת נתונים (Populate) ---
        religiousLevelBox.setItems(User.ReligiousLevel.values());

        occupationBox.setItems(User.Occupation.values());
        ethnicityBox.setItems(User.Ethnicity.values());

        // הגדרות גובה
        heightField.setMin(120);
        heightField.setMax(220);
        heightField.setPlaceholder("למשל: 175");
        heightField.setHelperText("הגובה קריטי להתאמה מדוייקת");

        // --- סידור ב-FormLayout ---
        FormLayout formLayout = new FormLayout();
        formLayout.add(religiousLevelBox, occupationBox, ethnicityBox, heightField,
                isSmokerBox, hasPetsBox, hasDegreeBox);

        // הגדרת עמודות
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        layout.add(sectionTitle, formLayout);
        return layout;
    }

    private VerticalLayout create_Part_3_PreferencesSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        H3 sectionTitle = new H3("3️⃣ מה את/ה מחפש/ת? (העדפות לאלגוריתם)");

        // --- אתחול רכיבים ---
        allowedReligiousLevelsBox = new MultiSelectComboBox<>("רמות דתיות שמתאימות לי");
        allowedReligiousLevelsBox.setItems(User.ReligiousLevel.values());

        allowedOccupationsBox = new MultiSelectComboBox<>("עיסוקים מועדפים");
        allowedOccupationsBox.setItems(User.Occupation.values());

        forbiddenEthnicitiesBox = new MultiSelectComboBox<>("עדות שלא באות בחשבון (Blacklist)");
        forbiddenEthnicitiesBox.setItems(User.Ethnicity.values());

        // טווחי גיל וגובה (פיצול ל-2 שדות לשיפור ה-UX)
        minAgeField = new IntegerField("גיל מינימום");
        maxAgeField = new IntegerField("גיל מקסימום");
        minHeightField = new IntegerField("גובה מינימום (ס\"מ)");
        maxHeightField = new IntegerField("גובה מקסימום (ס\"מ)");
        maxDistanceField = new IntegerField("מרחק מקסימלי (ק\"מ)");

        // Deal-Breakers
        requiresDegreeBox = new Checkbox("חובה תואר אקדמי");
        smokingDealBreakerBox = new Checkbox("לא מוכן/ה למעשן/ת");
        rejectsChildrenBox = new Checkbox("לא מוכן/ה למישהו עם ילדים");
        rejectsPetsBox = new Checkbox("לא מוכן/ה לבעלי חיים בבית");

        // --- סידור ---
        FormLayout formLayout = new FormLayout();
        formLayout.add(allowedReligiousLevelsBox, allowedOccupationsBox,
                minAgeField, maxAgeField,
                minHeightField, maxHeightField,
                maxDistanceField, forbiddenEthnicitiesBox);

        // הוספת הצ'קבוקסים בשורה נפרדת או מתחת
        VerticalLayout checkboxes = new VerticalLayout(
                new Span("קוים אדומים (Deal-Breakers):"),
                requiresDegreeBox, smokingDealBreakerBox, rejectsChildrenBox, rejectsPetsBox);
        checkboxes.setSpacing(false);
        checkboxes.setPadding(false);

        layout.add(sectionTitle, formLayout, checkboxes);
        return layout;
    }

    private VerticalLayout create_Part_4_PersonalitySection() {
        VerticalLayout layout = new VerticalLayout();
        H3 sectionTitle = new H3("4️⃣ אישיות (1 עד 10)");

        scaleExtraversionField = new IntegerField("מוחצנות (Extraversion)");
        scaleOrderlinessField = new IntegerField("סדר וארגון (Orderliness)");
        scaleEmotionalField = new IntegerField("רגישות (Emotional)");
        scaleAgreeablenessField = new IntegerField("נועם הליכות (Agreeableness)");
        scaleOpennessField = new IntegerField("פתיחות לחוויות (Openness)");

        IntegerField[] personalityFields = {scaleExtraversionField, scaleOrderlinessField, scaleEmotionalField, scaleAgreeablenessField, scaleOpennessField};
        for (IntegerField field : personalityFields) {
            field.setMin(1);
            field.setMax(10);
            field.setStepButtonsVisible(true); // מוסיף כפתורי פלוס ומינוס קטנים
            layout.add(field);
        }

        layout.addComponentAsFirst(sectionTitle);
        return layout;
    }

    // ==========================================
    // פונקציות בדיקה (Validation) לכל פרק
    // ==========================================

    private boolean checkPart1() {
        boolean isValid = true;
        if (!validateTextField(firstNameField))
            isValid = false;
        if (!validateTextField(lastNameField))
            isValid = false;
        if (birthDateField.isEmpty()) {
            birthDateField.setInvalid(true);
            birthDateField.setErrorMessage("תאריך חובה!");
            isValid = false;
        } else {
            birthDateField.setInvalid(false);
        }
        if (!validateComboBox(genderComboBox))
            isValid = false;
        if (!validateComboBox(maritalStatusComboBox))
            isValid = false;
        if (!validateTextField(phoneField))
            isValid = false;
        if (validateTextField(cityField))
            isValid = false;

        return isValid;
    }

    private boolean checkPart2() {
        boolean isValid = true;
        if (!validateComboBox(religiousLevelBox))
            isValid = false;
        if (!validateComboBox(occupationBox))
            isValid = false;
        if (!validateComboBox(ethnicityBox))
            isValid = false;
        if (!validateIntegerField(heightField))
            isValid = false;

        // check box אין צורך לבדוק , לא חייב
        // אם משתמש סימן זה true אחרת false

        return isValid;
    }

    private boolean checkPart3() {
        boolean isValid = true;
        if (allowedReligiousLevelsBox.isEmpty()) {
            allowedReligiousLevelsBox.setInvalid(true);
            allowedReligiousLevelsBox.setErrorMessage("חובה לבחור לפחות רמה דתית אחת");
            isValid = false;
        }

        if (minAgeField.isEmpty() || maxAgeField.isEmpty()) {
            Notification.show("נא להזין טווח גילאים תקין");
            isValid = false;
        }

        // מרחק מקסימלי הוא חובה עבור חישוב גיאוגרפי
        if (maxDistanceField.isEmpty()) {
            maxDistanceField.setInvalid(true);
            isValid = false;
        }

        return isValid;
    }

    private boolean checkPart4() {
        boolean isValid = true;
        if (!validateIntegerField(scaleExtraversionField))
            isValid = false;
        if (!validateIntegerField(scaleOrderlinessField))
            isValid = false;
        if (!validateIntegerField(scaleEmotionalField))
            isValid = false;
        if (!validateIntegerField(scaleAgreeablenessField))
            isValid = false;
        if (!validateIntegerField(scaleOpennessField))
            isValid = false;
        return isValid;
    }

    // ==========================================
    // פונקציות עזר לבדיקת רכיבים שונים
    // ==========================================

    private boolean validateComboBox(ComboBox<?> comboBox) {
        if (comboBox.isEmpty()) {
            comboBox.setInvalid(true);
            comboBox.setErrorMessage("חובה לבחור!");
            return false;
        }
        comboBox.setInvalid(false);
        return true;
    }

    private boolean validateMultiSelect(MultiSelectComboBox<?> comboBox) {
        if (comboBox.isEmpty()) {
            comboBox.setInvalid(true);
            comboBox.setErrorMessage("נא לבחור לפחות אפשרות אחת!");
            return false;
        }
        comboBox.setInvalid(false);
        return true;
    }

    private boolean validateTextField(TextField textField) {
        if (textField.isEmpty()) {
            textField.setInvalid(true);
            textField.setErrorMessage("שדה חובה!");
            return false;
        }
        textField.setInvalid(false);
        return true;
    }

    private boolean validateIntegerField(IntegerField intField) {
        if (intField.isEmpty()) {
            intField.setInvalid(true);
            intField.setErrorMessage("חובה להזין מספר!");
            return false;
        } else if ((intField.isInvalid())) {

            intField.setInvalid(true);
            intField.setErrorMessage("חובה להזין מספר בין 1 ל 10 בלבד!");
            return false;
        }

        intField.setInvalid(false);
        return true;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        currUser = (User) SessionHelper.getAttribute("currentUser");
        if (currUser == null) {
            event.forwardTo(AuthView.class);
        }

    }
}