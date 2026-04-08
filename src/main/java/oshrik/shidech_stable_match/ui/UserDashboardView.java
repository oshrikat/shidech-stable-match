package oshrik.shidech_stable_match.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import oshrik.shidech_stable_match.datamodels.Match;
import oshrik.shidech_stable_match.datamodels.MatchScore;
import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.datamodels.User.Gender;
import oshrik.shidech_stable_match.datamodels.User.UserStatus;
import oshrik.shidech_stable_match.services.MatchScoreService;
import oshrik.shidech_stable_match.services.MatchService;
import oshrik.shidech_stable_match.services.UserDashboardFacadeService;
import oshrik.shidech_stable_match.services.UserService;
import oshrik.shidech_stable_match.ui.components.StatusCardData;
import oshrik.shidech_stable_match.utilities.RouteHelper;
import oshrik.shidech_stable_match.utilities.SessionHelper;

@Route(value = "/userDashboard", layout = UserAppLayout.class)
public class UserDashboardView extends VerticalLayout implements BeforeEnterObserver {

    // Services
    UserDashboardFacadeService userDashboardFacadeService;

    // Data models
    private Match currMatch;
    private User currUser;
    private User currPartner;

    // UI
    private UI ui;

    public UserDashboardView(UserDashboardFacadeService userDashboardFacadeService) {
        this.userDashboardFacadeService = userDashboardFacadeService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        getStyle().set("direction", "rtl"); 
        getStyle().set("background-color", "#f4f4f9"); 
        getStyle().set("padding", "40px");

        ui = UI.getCurrent();
    }

    private void buildDashboard() {
        removeAll(); 

        add(
            create_Part1_Header(),
            create_Part2_StatusZone(),
            create_Part3_Stats(),
            create_Part4_SplitZone(),
            create_Part5_MatchHistory()
        );
    }

    // --- חלק 1: הדר עליון ---
    private HorizontalLayout create_Part1_Header() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidth("80%");
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        H2 greeting = new H2("שלום, " + currUser.getUsername() + " 👋");
        greeting.getStyle().set("margin", "0");

        Span activeBadge = new Span("🟢 פרופיל פעיל");

        activeBadge.getStyle().set("background-color", "#e6f4ea");
        activeBadge.getStyle().set("color", "#1e8e3e");
        activeBadge.getStyle().set("padding", "5px 15px");
        activeBadge.getStyle().set("border-radius", "20px");
        activeBadge.getStyle().set("font-weight", "bold");


        header.add(greeting, activeBadge);
        return header;
    }

    // --- חלק 2: אזור סטטוס מרכזי (הלוגיקה!) ---
    /**
     * בונה ומחזיר את רכיב התצוגה של אזור הסטטוס המרכזי.
     * שואב את הנתונים משירות ה-Facade ומייצר את רכיבי ה-UI בהתאם
     *
     * @return VerticalLayout קונטיינר (מכל) המכיל את כל רכיבי הסטטוס המעוצבים.
     */
    private VerticalLayout create_Part2_StatusZone() {

        VerticalLayout statusCard = new VerticalLayout();
        statusCard.setWidth("80%");
        statusCard.setAlignItems(Alignment.CENTER);
        statusCard.getStyle().set("background-color", "white");
        statusCard.getStyle().set("border-radius", "15px");
        statusCard.getStyle().set("box-shadow", "0 4px 8px rgba(0,0,0,0.1)");
        statusCard.getStyle().set("padding", "30px");
        statusCard.getStyle().set("margin-top", "20px");

        // שליפת אובייקט הנתונים מהקבלן
        StatusCardData statusData = userDashboardFacadeService.getStatusCardToShow(currUser);

        H3 statusTitle = new H3(statusData.getTitle());
        statusTitle.getStyle().set("color", "#d93871");
        Span statusSub = new Span(statusData.getSubtitle());

        statusCard.add(statusTitle, statusSub);

        // הוספת הכפתור רק אם יש לו טקסט
        if (statusData.getButtonText() != null) {
            Button btnAction = new Button(statusData.getButtonText());
            btnAction.addClickListener(e -> RouteHelper.navigateTo(MyMatchView.class));
            statusCard.add(btnAction);
        }

        return statusCard;
    }


    // --- חלק 3: סטטיסטיקות מעודכן ודינמי ---
    private HorizontalLayout create_Part3_Stats() {
        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidth("80%");
        statsLayout.setJustifyContentMode(JustifyContentMode.AROUND);
        statsLayout.getStyle().set("margin-top", "20px");

        statsLayout.add(
                createStatBox("ימים במערכת", userDashboardFacadeService.calculateDaysInSystem(currUser)),
                createStatBox("התאמות שהתקבלו", userDashboardFacadeService.getCountFirstMatches(currUser)),
                createStatBox("ציון התאמה מקסימלי", userDashboardFacadeService.getBestScore(currUser))
        );

        return statsLayout;
    }



    private VerticalLayout createStatBox(String title, String value) {
        VerticalLayout box = new VerticalLayout();
        box.setAlignItems(Alignment.CENTER);
        box.getStyle().set("background-color", "white");
        box.getStyle().set("border-radius", "10px");
        box.getStyle().set("padding", "15px");
        box.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.05)");
        box.setWidth("30%");

        H2 valText = new H2(value);
        valText.getStyle().set("margin", "0");
        Span titleText = new Span(title);
        titleText.getStyle().set("color", "gray");

        box.add(valText, titleText);
        return box;
    }

    // --- חלק 4: אזור מפוצל ---
    private HorizontalLayout create_Part4_SplitZone() {

        HorizontalLayout splitLayout = new HorizontalLayout();
        splitLayout.setWidth("80%");
        splitLayout.getStyle().set("margin-top", "20px");

        VerticalLayout profileCard = new VerticalLayout();
        profileCard.getStyle().set("background-color", "white");
        profileCard.getStyle().set("border-radius", "15px");
        profileCard.getStyle().set("padding", "20px");
        profileCard.getStyle().set("box-shadow", "0 4px 8px rgba(0,0,0,0.1)");
        profileCard.setWidth("50%");

        // --- יצירת ההדר של כרטיסיית הפרופיל ---
        HorizontalLayout profileHeader = new HorizontalLayout();
        profileHeader.setWidthFull();
        // מרווח את האיברים לקצוות - הכותרת בימין, העיפרון בשמאל (בגלל RTL)
        profileHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        profileHeader.setAlignItems(FlexComponent.Alignment.CENTER);

        H4 profileTitle = new H4("הפרופיל שלי");
        profileTitle.getStyle().set("margin", "0"); // חשוב כדי שהיישור יהיה נקי

        Button editProfileBtn = new Button(VaadinIcon.EDIT.create());
        editProfileBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        editProfileBtn.getElement().setProperty("title", "ערוך פרטים");

        // כאן אנחנו קוראים לפונקציה שתפתח את המודאל
        editProfileBtn.addClickListener(e -> openEditProfileDialog());

        profileHeader.add(profileTitle, editProfileBtn);

        // מוסיפים את ההדר לכרטיסייה במקום הכותרת הרגילה
        profileCard.add(profileHeader);

        // ---> הלוגיקה של התמונה <---
        if (currUser.getPhotoUrl() != null && !currUser.getPhotoUrl().isEmpty()) {
            // מעבירים את ה-URL השמור (המחרוזת שלנו) וטקסט חלופי
            Image profileImg = new Image(currUser.getPhotoUrl(), "תמונת פרופיל");

            // עיצוב כדי שהתמונה תיראה טוב
            profileImg.setWidth("120px");
            profileImg.setHeight("120px");
            profileImg.getStyle().set("border-radius", "50%"); // עושה את התמונה עגולה
            profileImg.getStyle().set("object-fit", "cover"); // שומר על פרופורציות התמונה
            profileImg.getStyle().set("margin-bottom", "15px");

            profileCard.add(profileImg);
        } else {
            // אם אין תמונה, נציג טקסט או סמיילי
            profileCard.add(new Span("👤 אין תמונת פרופיל"));
        }

        profileCard.add(new Span("שם: " + currUser.getFullName()));
        profileCard.add(new Span("גיל: " + currUser.getAge()));
        profileCard.add(new Span("השכלה: " + (currUser.isHasDegree() ? "אקדמאית" : "ללא תואר")));
        profileCard.add(new Span("רקע דתי: " + (currUser.getReligiousLevel() != null ? currUser.getReligiousLevel().name() : "לא הוגדר")));
        profileCard.add(new Span("מקצוע: " + (currUser.getOccupation() != null ? currUser.getOccupation().name() : "לא הוגדר")));

        VerticalLayout personalityCard = new VerticalLayout();
        personalityCard.getStyle().set("background-color", "white");
        personalityCard.getStyle().set("border-radius", "15px");
        personalityCard.getStyle().set("padding", "20px");
        personalityCard.getStyle().set("box-shadow", "0 4px 8px rgba(0,0,0,0.1)");
        personalityCard.setWidth("50%");

        personalityCard.add(new H4("ניתוח אופי"));
        personalityCard.add(createProgressBar("מוחצנות", currUser.getScaleExtraversion())); 
        personalityCard.add(createProgressBar("יציבות", currUser.getScaleOrderliness()));
        personalityCard.add(createProgressBar("רגישות", currUser.getScaleEmotional()));
        personalityCard.add(createProgressBar("נועם הליכות", currUser.getScaleAgreeableness()));
        personalityCard.add(createProgressBar("פתיחות", currUser.getScaleOpenness()));

        splitLayout.add(profileCard, personalityCard);
        return splitLayout;
    }

    private void openEditProfileDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("עריכת פרטי פרופיל");
        dialog.setWidth("400px");

        // 1. יצירת השדות
        TextField usernameField = new TextField("שם משתמש");
        DatePicker dateBirth = new DatePicker("תאריך לידה");

        FormLayout formLayout = new FormLayout(usernameField, dateBirth);

        // 2. הגדרת ה-Binder
        Binder<User> binder = new Binder<>(User.class);

        // תיקון קריטי: Getter ו-Setter תואמים
        binder.forField(usernameField)
                .asRequired("שדה חובה")
                .bind(User::getUsername, User::setUsername);

        // הוספת ולידציית גיל (מעל 18) על DatePicker
        binder.forField(dateBirth)
                .asRequired("שדה חובה")
                .withValidator(date -> {
                    if (date == null)
                        return false;
                    // מחשב את השנים בין תאריך הלידה להיום
                    int age = java.time.Period.between(date, java.time.LocalDate.now()).getYears();
                    return age >= 17;
                }, "חובה להיות מעל גיל 17")
                .bind(User::getBirthDate, User::setBirthDate);

        // 3. טעינת הנתונים הנוכחיים לתוך הטופס
        binder.readBean(currUser);

        // 4. כפתורי פעולה
        Button saveBtn = new Button("שמור ועדכן", event -> {
            try {
                // מנסה לכתוב מהטופס למשתמש. אם הגיל קטן מ-17, זה יזרוק שגיאה
                // וייקפוץ ל-catch
                binder.writeBean(currUser);

                // שמירה ב-Facade (שמבצעת שמירה במונגו)
                userDashboardFacadeService.updateUser(currUser);

                dialog.close();
                Notification.show("הפרופיל עודכן בהצלחה!", 3000, Notification.Position.TOP_CENTER);

                // רענון העמוד - הדרך הבטוחה ב-Vaadin לעשות Reload
                ui.getPage().reload();

            } catch (ValidationException e) {
                Notification.show("יש לתקן את השגיאות בטופס", 3000, Notification.Position.MIDDLE);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("ביטול", e -> dialog.close());
        cancelBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        dialog.add(formLayout);
        dialog.getFooter().add(cancelBtn, saveBtn);

        dialog.open();
    }

    private VerticalLayout createProgressBar(String label, int outOfTen) {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(false);

        Span title = new Span(label + " (" + outOfTen + "/10)");
        ProgressBar pb = new ProgressBar();
        pb.setMin(0);
        pb.setMax(10);
        pb.setValue((double) outOfTen);
        
        layout.add(title, pb);
        return layout;
    }

    // --- חלק 5: היסטוריית התאמות ---
    private VerticalLayout create_Part5_MatchHistory() {
        VerticalLayout historyCard = new VerticalLayout();
        historyCard.setWidth("80%");
        historyCard.getStyle().set("background-color", "white");
        historyCard.getStyle().set("border-radius", "15px");
        historyCard.getStyle().set("padding", "20px");
        historyCard.getStyle().set("box-shadow", "0 4px 8px rgba(0,0,0,0.1)");
        historyCard.getStyle().set("margin-top", "20px");

        historyCard.add(new H4("היסטוריית התאמות (בקרוב)"));
        historyCard.add(new Span("כאן יופיעו ההתאמות הקודמות שלך לאחר שנפעיל את המערכת."));

        return historyCard;
    }

    private User getUser(String id) {
        return userDashboardFacadeService.findUserById(id);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        currUser = (User) SessionHelper.getAttribute("currentUser");


        if (currUser == null) {
            event.forwardTo(AuthView.class);
        } else if (!currUser.isProfileComplete()) {
            event.forwardTo(WizardView.class);
        } else {
            {
                // מעדכן את המשתמש הנוכחי , עם הנתונים הכי עדכניים ממסד הנתונים
                currUser = getUser(currUser.getId());

                // עדכון המערכת כולה בנתונים הכי מעודכנים של המשתמש - כל דף ישמשוט את המשתמש ,
                // יקבל אותו מעודכן תמיד
                SessionHelper.setAttribute("currentUser", currUser);

                /* יש משתמש קיים ומחובר */

                // אם יש שידוך -> נשלוף את השידוך + פרטנר

                currMatch = userDashboardFacadeService.getCurrentActiveOrPendingMatch(currUser.getId(),
                        currUser.getGender());
                if (currMatch != null) {

                    // יש שידוך זמין כשלהו למשתמש הנוכחי בדף

                    if (currUser.getGender().equals(Gender.MALE)) {
                        currPartner = userDashboardFacadeService.findUserById(currMatch.getWomanId());
                    } else {
                        currPartner = userDashboardFacadeService.findUserById(currMatch.getManId());
                    }

                }

                // נבנה את הדף - כי יש עם מה לעבוד
                buildDashboard();
            }

        }

    }
}