package oshrik.shidech_stable_match.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
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

    public UserDashboardView(UserDashboardFacadeService userDashboardFacadeService) {
        this.userDashboardFacadeService = userDashboardFacadeService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        getStyle().set("direction", "rtl"); 
        getStyle().set("background-color", "#f4f4f9"); 
        getStyle().set("padding", "40px");
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

        H2 greeting = new H2("שלום, " + currUser.getFirstName() + " 👋");
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

        profileCard.add(new H4("הפרופיל שלי"));
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


    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        currUser = (User) SessionHelper.getAttribute("currentUser");


        if (currUser == null) {
            event.forwardTo(AuthView.class);
        } else if (!currUser.isProfileComplete()) {
            event.forwardTo(WizardView.class);
        } else {
            {
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