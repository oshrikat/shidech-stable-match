package oshrik.shidech_stable_match.ui;


import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.H3;


import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.datamodels.User.ROLE;
import oshrik.shidech_stable_match.repositories.UserRepository;
import oshrik.shidech_stable_match.services.AsyncManagerService;
import oshrik.shidech_stable_match.services.DataGenerationService;
import oshrik.shidech_stable_match.services.GaleShapleyService;
import oshrik.shidech_stable_match.services.MatchmakingService;
import oshrik.shidech_stable_match.services.UserService;
import oshrik.shidech_stable_match.ui.components.MatchCard;
import oshrik.shidech_stable_match.utilities.ScorePair;
import oshrik.shidech_stable_match.utilities.SessionHelper;

@CssImport("./themes/match-card.css")
// הכתובת אליה ניגש בדפדפן: localhost:8080/match-algo
@Route(value = "/match-algo", layout = AdminAppLayout.class)
@PageTitle("מעבדת אלגוריתם - טסט")
public class MatchAlgoView extends VerticalLayout implements BeforeEnterObserver {

    // הזרקת השירותים
    private final DataGenerationService dataGenService;
    private final MatchmakingService matchmakingService;
    private final UserRepository userRepository;
    private UserService userService;
    private final GaleShapleyService galeShapleyService;
    private final AsyncManagerService backroundCoreProccess;

    // רכיב תצוגה - טבלה
    private final Grid<User> userGrid = new Grid<>(User.class, false);
    
    // אזור תצוגה חדש עבור רשימות ההעדפות
    private final HorizontalLayout listsContainer = new HorizontalLayout();

    // אזור תצוגה חדש עבור התוצאות הסופיות (הכרטיסיות)
    private final HorizontalLayout cardsContainer = new HorizontalLayout();

    // גישה לתצוגה מהלהליכונים
    private UI ui;
    private Button btnRunAlgo;

    public MatchAlgoView(DataGenerationService dataGenService, MatchmakingService matchmakingService,
            UserRepository userRepository, UserService userService, GaleShapleyService galeShapleyService,
            AsyncManagerService backroundCoreProccess) 
    {
       // אתחול תלויות 
        this.dataGenService = dataGenService;
        this.matchmakingService = matchmakingService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.galeShapleyService = galeShapleyService;
        this.backroundCoreProccess = backroundCoreProccess;

        // הגדרות עיצוב כלליות למסך
        setSpacing(true);
        setPadding(true);
        setSizeFull();


        // 1. כותרת המסך
        H2 title = new H2("\t\t\t\t" + "\t\t מעבדת אלגוריתם שידוכים\t\t\t\t");
        Span subtitle = new Span("כאן נבדוק את השלבים השונים של האלגוריתם, צעד אחר צעד.");
        
        // 2. הגדרת כפתורי ההפעלה
        Button btnGenerateData = new Button("1. ייצור נתוני בדיקה (Data Generation)");
        btnGenerateData.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        

        Button btnCalculateScores = new Button("2. חישוב ציונים והעדפות (Scoring)");
        btnCalculateScores.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        btnRunAlgo = new Button("3. הפעל אלגוריתם שידוכים (Gale-Shapley)");
        btnRunAlgo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnRunAlgo.getStyle().set("background-color", "purple"); // נצבע אותו בסגול שייבלט

        // הוספת גלגל טעינה למסך - מאותחל להיות מוסתר בהתחלה
        ProgressBar progressBar = new ProgressBar(); // מה אני עושה עם זה ..? מתי אני הופך אותו לנראה ?
        progressBar.setVisible(false);

        btnRunAlgo.addClickListener(e -> {
            // 1. בדיקת תקינות - מוודאים שיש נתונים
            if (matchmakingService.getCurrentMen() == null || matchmakingService.getCurrentMen().isEmpty()) {

                showErrorNotification("חובה לחשב ציונים והעדפות (שלב 2) לפני הרצת האלגוריתם!");
                return;
            }

            showSuccessNotification("Dating on the way !");

            // אתחול ui
            ui = UI.getCurrent();

            // נעילת כפתור
            btnRunAlgo.setEnabled(false);

            // הצגת גלגל טעינה - נהפוך אותו לגלוי
            progressBar.setVisible(true);

            // ננקה את ההעדפות מהמסך
            listsContainer.removeAll();

            // התחלת תהליך שידוכים ברקע
            runCoreGaleShapleyAlgo(progressBar);




        });

        // 3. הגדרת עמודות הטבלה (כדי שנראה את האנשים שיצרנו)
        userGrid.addColumn(User::getFullName).setHeader("שם מלא").setAutoWidth(true);
        userGrid.addColumn(User::getGender).setHeader("מגדר").setAutoWidth(true);
        userGrid.addColumn(User::getAge).setHeader("גיל").setAutoWidth(true);
        userGrid.addColumn(User::getReligiousLevel).setHeader("רמה דתית").setAutoWidth(true);
        userGrid.addColumn(User::getOccupation).setHeader("עיסוק").setAutoWidth(true);

        // --- הוספת הלוגיקה לכפתורים ---

        // לחיצה על כפתור 1: ייצור נתונים
        btnGenerateData.addClickListener(e -> 
        {
            // נמחק מה שקיים
            userService.deleteAllUsers();

            // מייצרים 10 גברים ו-10 נשים (סה"כ 20)
            dataGenService.generateAndSaveUsers(20);
            
            // מרעננים את הטבלה שעל המסך
            refreshGrid();
            
            showSuccessNotification("נוצרו 40 משתמשים חדשים בהצלחה!");
        });

        // לחיצה על כפתור 2: בניית רשימות העדפות
        btnCalculateScores.addClickListener(e -> {
            try {
                matchmakingService.prepareAndFillPreferences();
                showSuccessNotification("הציונים חושבו ורשימות ההעדפות מוינו בהצלחה!");
                
                // קריאה לפונקציה החדשה שמציירת את הרשימות
                displayPreferenceLists();
                
                // אופציונלי: אפשר להעלים את טבלת האנשים המקורית כדי לפנות מקום
                userGrid.setVisible(false);

            } catch (Exception ex) {

                showErrorNotification(" שגיאה בחישוב הציונים ");

            }

        });

        // 4. סידור הרכיבים על המסך
        HorizontalLayout buttonsLayout = new HorizontalLayout(btnGenerateData, btnCalculateScores, btnRunAlgo);
        
        listsContainer.setWidthFull();

        cardsContainer.setWidthFull();
        cardsContainer.getStyle().set("flex-wrap", "wrap"); // מסדר אותם יפה בשורות
        cardsContainer.getStyle().set("justify-content", "center");

        add(title, subtitle, buttonsLayout, progressBar, userGrid, listsContainer, cardsContainer);

        // טעינה ראשונית של נתונים (אם כבר קיימים ב-DB)
        refreshGrid();

        // שמירת מצב (State Management)
        if (matchmakingService.getCurrentMen() != null && !matchmakingService.getCurrentMen().isEmpty()) {
            // אם לאחד מהם כבר יש שידוך, נציג מיד את הכרטיסיות
            if (matchmakingService.getCurrentMen().get(0).getCurrentPartner() != null) {
                showAlgoResults();
                userGrid.setVisible(false); // מעלים את טבלת הבסיס כדי לחסוך מקום
            }
        }

    }

    private void runCoreGaleShapleyAlgo(ProgressBar proggressBar) {
        backroundCoreProccess.executeWithProgress(new Runnable() {

            @Override
            public void run() {
                galeShapleyService.runGaleShapley(matchmakingService.getCurrentMen(),
                        matchmakingService.getCurrentWomen());
            }

        }, new AsyncManagerService.TaskCallback() {

            @Override
            public void onComplete(boolean isCompleted) {
                // מה יקרה כשהאלגוריתם יסיים?

                ui.access(() -> {

                    // 1. הצגת השידוכים בעזרת הכרטיסיות שעשינו
                    showAlgoResults(); // זה בידוע שקיימים כבר העדפות לכל משתמש

                    // 2. לפתוח את הכפתור
                    btnRunAlgo.setEnabled(true);

                    // 3. הצגת הודעה למשתמש שהכל נוצר בהצלחה
                    showSuccessNotification("Algorithem Finished !!!");

                    // סגירת גלגל
                    proggressBar.setValue(0);
                    proggressBar.setVisible(false);

                });
            }

            @Override
            public void onProgress(int percentage) {
                // איך נעדכן את המסך בכל פעימה?

                ui.access(() -> {

                    // 1. נרצה שהגלגל יהיה תואם את הזמן וההתקדמות של האלגוריתם
                    showProggressAlgo(proggressBar, percentage); // זה מטופל בתוך הממשק ?

                    // 2. עדכון חצי הדרך
                    if (percentage == 50)
                        showUpdateNotification("We are in the half of the way !!!!");

                });

            }

            @Override
            public void onError(String message) {

                // מה יקרה אם תהיה שגיאה?
                ui.access(() -> {
                    // 1. נקפיץ התראה שאומרת שהתרחשה שגיאה
                    showErrorNotification("Algo has an Fatal Error ! Try Again Later...");
                    // 2. נאפס את כל מה שנעול
                    btnRunAlgo.setEnabled(true);
                    // 3. נבצע תיקון וניקוי של הנתונים , על מנת שלא ימשיך להיות שגוי

                });

            }
        });

    }

    private void showProggressAlgo(ProgressBar proggressBar, int precentage) {
        proggressBar.setValue((precentage / 100.0));
    }

    // פעולת עזר לרענון הטבלה
    private void refreshGrid() {
        userGrid.setItems(userRepository.findAll());
    }

    // פעולת עזר להצגת הודעות קופצות ירוקות
    private void showSuccessNotification(String message) 
    {
        Notification notification = Notification.show(message, 4000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    // פעולת עזר להצגת הודעות של עדכון למשתמש
    private void showUpdateNotification(String message) {
        Notification notification = Notification.show(message, 2000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
    }

    // פעולת עזר להצגת הודעות קופצות אדומות - שגיאה
    private void showErrorNotification(String message) {
        Notification notification = Notification.show(message, 4000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
 
    private void displayPreferenceLists() {
        listsContainer.removeAll(); // מנקים תצוגה קודמת

        // עמודת גברים
        VerticalLayout menColumn = new VerticalLayout();
        menColumn.add(new H3("רשימות העדפות - גברים 🤵"));
        for (User man : matchmakingService.getCurrentMen()) {
            menColumn.add(showAndCreateUserDetails(man));
        }

        // עמודת נשים
        VerticalLayout womenColumn = new VerticalLayout();
        womenColumn.add(new H3("רשימות העדפות - נשים 👰"));
        for (User woman : matchmakingService.getCurrentWomen()) {
            womenColumn.add(showAndCreateUserDetails(woman));
        }

        listsContainer.add(menColumn, womenColumn);
    }

    // הפונקציה שמייצרת את ה"אקורדיון" הנפתח לכל משתמש
    private Details showAndCreateUserDetails(User user) {
        VerticalLayout prefLayout = new VerticalLayout();
        prefLayout.setSpacing(false);
        prefLayout.setPadding(false);

        int rank = 1;
        for (ScorePair pair : user.getPreferencesScores()) {
            String text = rank + ". " + pair.getCandidate().getFullName() + " (ציון: " + String.format("%.1f", pair.getScore()) + ")";
            prefLayout.add(new Span(text));
            rank++;
        }

        // יצירת הרכיב הנפתח עם שם המשתמש בכותרת
        Details details = new Details(user.getFullName(), prefLayout);
        return details;
    }

    /** הפונקציה שמייצרת את הכרטיסיות היפות */
    private void showAlgoResults() {
        cardsContainer.removeAll();

        // עוברים על כל הגברים ובודקים את הסטטוס שלהם
        for (User man : matchmakingService.getCurrentMen()) {
            User partner = man.getCurrentPartner();

            if (partner != null) {
                // יש שידוך! שולפים את הציון
                double score = findScore(man, partner);

                // כאן ניצור את הכרטיסייה המעוצבת החדשה!
                MatchCard card = new MatchCard(man.getFullName(), partner.getFullName(), score);

                cardsContainer.add(card);

            } else {
                // אין שידוך (קופסה פשוטה או טקסט שיתווסף למסך)
                Div singleCard = new Div();
                singleCard.setText("🤵 " + man.getFullName() + " | נותר ללא שידוך בסבב זה 💔");
                singleCard.getStyle().set("padding", "10px").set("border", "1px solid gray").set("border-radius",
                        "8px");

                cardsContainer.add(singleCard);
            }
        }
    }

    /** פונקציית עזר לשליפת ציון ההתאמה מתוך רשימת ההעדפות */
    private double findScore(User man, User partner) {
        if (man.getPreferencesScores() != null) {
            for (ScorePair sp : man.getPreferencesScores()) {
                if (sp.getCandidate().getId().equals(partner.getId())) {
                    return sp.getScore();
                }
            }
        }
        return 0.0;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        User u = (User) SessionHelper.getAttribute("currentUser");

        if (u != null) {

            if (!(u.getRole().equals(ROLE.MASTER_ADMIN)))
                event.forwardTo(UserDashboardView.class);
        } else {
            event.forwardTo(AuthView.class);
        }

    }

}