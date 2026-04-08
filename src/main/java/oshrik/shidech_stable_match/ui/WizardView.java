package oshrik.shidech_stable_match.ui;

import java.util.ArrayList;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.services.UserService;
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

    // פרק 3: Preferences
    private MultiSelectComboBox<User.ReligiousLevel> allowedReligiousLevelsBox;
    private MultiSelectComboBox<User.Occupation> allowedOccupationsBox;
    private IntegerField maxDistanceField;
    private Checkbox requiresDegreeBox;
    private Checkbox smokingDealBreakerBox;
    private MultiSelectComboBox<User.Ethnicity> forbiddenEthnicitiesBox;
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
                // פרק 1
                currUser.setFirstName(firstNameField.getValue());
                currUser.setLastName(lastNameField.getValue());
                currUser.setBirthDate((birthDateField.getValue())); 
                currUser.setGender(genderComboBox.getValue());
                currUser.setMaritalStatus(maritalStatusComboBox.getValue());

                // ---> שמירת התמונה! <---
                if (base64ImageString != null) {
                    currUser.setPhotoUrl(base64ImageString);
                }

                // פרק 2
                currUser.setReligiousLevel(religiousLevelBox.getValue());
                currUser.setOccupation(occupationBox.getValue());
                currUser.setEthnicity(ethnicityBox.getValue());
                currUser.setHeight(heightField.getValue() != null ? heightField.getValue() : 0);
                currUser.setSmoker(isSmokerBox.getValue());
                currUser.setHasPets(hasPetsBox.getValue());
                currUser.setHasDegree(hasDegreeBox.getValue());

                // פרק 3 (המרת ה-Set של ה-MultiSelect ל-ArrayList)
                currUser.setAllowedReligiousLevels(new ArrayList<>(allowedReligiousLevelsBox.getValue()));
                currUser.setAllowedOccupations(new ArrayList<>(allowedOccupationsBox.getValue()));
                currUser.setForbiddenEthnicities(new ArrayList<>(forbiddenEthnicitiesBox.getValue()));
                currUser.setMaxDistanceKm(maxDistanceField.getValue() != null ? maxDistanceField.getValue() : 0);
                currUser.setRequiresDegree(requiresDegreeBox.getValue());
                currUser.setSmokingDealBreaker(smokingDealBreakerBox.getValue());
                currUser.setRejectsChildren(rejectsChildrenBox.getValue());
                currUser.setRejectsPets(rejectsPetsBox.getValue());

                // פרק 4
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
        H3 sectionTitle = new H3("1️⃣ פרטים אישיים");

        firstNameField = new TextField("שם פרטי");
        lastNameField = new TextField("שם משפחה");
        birthDateField = new DatePicker("תאריך לידה");
        
        genderComboBox = new ComboBox<>("מגדר");
        maritalStatusComboBox = new ComboBox<>("מצב משפחתי");

        genderComboBox.setItems(User.Gender.values());
        maritalStatusComboBox.setItems(User.MaritalStatus.values());
        // ---> הלוגיקה של העלאת התמונה <---
        com.vaadin.flow.server.streams.UploadHandler uploadHandler = (request) -> {
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

            } catch (java.io.IOException e) {
                throw new RuntimeException("שגיאה בקריאת הקובץ", e);
            }
        };

        uploadProfileImage = new Upload(uploadHandler);
        uploadProfileImage.setAcceptedFileTypes("image/jpeg", "image/png"); // רק תמונות
        uploadProfileImage.setMaxFileSize(5 * 1024 * 1024); // הגבלה ל-5MB
        uploadProfileImage.setDropLabel(new com.vaadin.flow.component.html.Span("גרור תמונת פרופיל לכאן (או לחץ)"));

        // הוספנו את ה-uploadProfileImage ל-layout!
        layout.add(sectionTitle, uploadProfileImage, firstNameField, lastNameField, birthDateField, genderComboBox,
                maritalStatusComboBox);
        return layout;
    }


    private VerticalLayout create_Part_2_UserDataSection() {
        VerticalLayout layout = new VerticalLayout();
        H3 sectionTitle = new H3("2️⃣ קצת עליך");

        religiousLevelBox = new ComboBox<>("רמה דתית");
        religiousLevelBox.setItems(User.ReligiousLevel.values());

        occupationBox = new ComboBox<>("עיסוק נוכחי");
        occupationBox.setItems(User.Occupation.values());

        ethnicityBox = new ComboBox<>("עדה/מוצא");
        ethnicityBox.setItems(User.Ethnicity.values());

        heightField = new IntegerField("גובה (בס\"מ)");
        heightField.setMin(140);
        heightField.setMax(220);

        isSmokerBox = new Checkbox("מעשן/ת?");
        hasPetsBox = new Checkbox("יש חיות מחמד?");
        hasDegreeBox = new Checkbox("בעל/ת תואר אקדמי?");

        layout.add(sectionTitle, religiousLevelBox, occupationBox, ethnicityBox, heightField, isSmokerBox, hasPetsBox, hasDegreeBox);
        return layout;
    }

    private VerticalLayout create_Part_3_PreferencesSection() {
        VerticalLayout layout = new VerticalLayout();
        H3 sectionTitle = new H3("3️⃣ מה את/ה מחפש/ת?");

        // שימוש ב-MultiSelect כדי לאפשר בחירה של כמה אפשרויות
        allowedReligiousLevelsBox = new MultiSelectComboBox<>("רמות דתיות מועדפות");
        allowedReligiousLevelsBox.setItems(User.ReligiousLevel.values());

        allowedOccupationsBox = new MultiSelectComboBox<>("עיסוקים מועדפים");
        allowedOccupationsBox.setItems(User.Occupation.values());

        forbiddenEthnicitiesBox = new MultiSelectComboBox<>("עדות שאינן מתאימות (Blacklist)");
        forbiddenEthnicitiesBox.setItems(User.Ethnicity.values());

        maxDistanceField = new IntegerField("מרחק מקסימלי לשידוך (ק\"מ)");
        
        requiresDegreeBox = new Checkbox("חובה תואר אקדמי?");
        smokingDealBreakerBox = new Checkbox("עישון זה דיל-ברייקר?");
        rejectsChildrenBox = new Checkbox("מעדיף/ה ללא ילדים קודמים?");
        rejectsPetsBox = new Checkbox("מעדיף/ה ללא חיות מחמד?");

        layout.add(sectionTitle, allowedReligiousLevelsBox, allowedOccupationsBox, forbiddenEthnicitiesBox, 
                   maxDistanceField, requiresDegreeBox, smokingDealBreakerBox, rejectsChildrenBox, rejectsPetsBox);
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
        if (!validateMultiSelect(allowedReligiousLevelsBox))
            isValid = false;
        if (!validateMultiSelect(allowedOccupationsBox))
            isValid = false;
        if (!validateIntegerField(maxDistanceField))
            isValid = false;
        // עדות פסולות (forbiddenEthnicitiesBox) זה בסדר להשאיר ריק, לא חובה שיהיה
        // לאנשים Blacklist
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