package oshrik.shidech_stable_match.ui;

import java.util.concurrent.CompletableFuture;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;


import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.repositories.UserRepository;
import oshrik.shidech_stable_match.services.DataGenerationService;
import oshrik.shidech_stable_match.services.GaleShapleyService;
import oshrik.shidech_stable_match.services.MatchmakingService;
import oshrik.shidech_stable_match.utilities.ScorePair;

// הכתובת אליה ניגש בדפדפן: localhost:8080/match-algo
@Route(value = "/match-algo", layout = MainLayout.class)
@PageTitle("מעבדת אלגוריתם - טסט")
public class MatchAlgoView extends VerticalLayout {

    // הזרקת השירותים
    private final DataGenerationService dataGenService;
    private final MatchmakingService matchmakingService;
    private final UserRepository userRepository;
    private final GaleShapleyService galeShapleyService;

    // רכיב תצוגה - טבלה
    private final Grid<User> userGrid = new Grid<>(User.class, false);
    
    // אזור תצוגה חדש עבור רשימות ההעדפות
    private final HorizontalLayout listsContainer = new HorizontalLayout();

    // אזור תצוגה חדש עבור התוצאות הסופיות (הכרטיסיות)
    private final HorizontalLayout cardsContainer = new HorizontalLayout();

    public MatchAlgoView(DataGenerationService dataGenService, MatchmakingService matchmakingService,
            UserRepository userRepository, GaleShapleyService galeShapleyService) 
    {
       // אתחול תלויות 
        this.dataGenService = dataGenService;
        this.matchmakingService = matchmakingService;
        this.userRepository = userRepository;
        this.galeShapleyService = galeShapleyService;

        // הגדרות עיצוב כלליות למסך
        setSpacing(true);
        setPadding(true);
        setSizeFull();

        // 1. כותרת המסך
        H2 title = new H2("\t\t🧪 מעבדת אלגוריתם שידוכים\t\t\t\t");
        Span subtitle = new Span("כאן נבדוק את השלבים השונים של האלגוריתם, צעד אחר צעד.");
        
        // 2. הגדרת כפתורי ההפעלה
        Button btnGenerateData = new Button("1. ייצור נתוני בדיקה (Data Generation)");
        btnGenerateData.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button btnCalculateScores = new Button("2. חישוב ציונים והעדפות (Scoring)");
        btnCalculateScores.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

        Button btnRunAlgo = new Button("3. הפעל אלגוריתם שידוכים (Gale-Shapley)");
        btnRunAlgo.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btnRunAlgo.getStyle().set("background-color", "purple"); // נצבע אותו בסגול שייבלט

        btnRunAlgo.addClickListener(e -> {
            if (matchmakingService.getCurrentMen() == null || matchmakingService.getCurrentMen().isEmpty()) {
                Notification.show("חובה לחשב ציונים והעדפות (שלב 2) לפני הרצת האלגוריתם!");
                return;
            }

            // 1. בניית חלון הטעינה (ספינר)
            Dialog loadingDialog = new Dialog();
            loadingDialog.setCloseOnEsc(false);
            loadingDialog.setCloseOnOutsideClick(false);

            ProgressBar spinner = new ProgressBar();
            spinner.setIndeterminate(true); // זז ימינה ושמאלה בלי הפסקה

            VerticalLayout dialogLayout = new VerticalLayout(
                    new H3("השידוך מתבצע... ⚙️"),
                    spinner,
                    new Span("האלגוריתם מחשב כעת את השידוכים היציבים ביותר. נא להמתין."));
            dialogLayout.setAlignItems(Alignment.CENTER);
            loadingDialog.add(dialogLayout);

            // הצגת חלון הטעינה
            loadingDialog.open();

            // 2. תפיסת ה-UI הנוכחי כדי שנוכל לחזור אליו מהרקע
            UI ui = UI.getCurrent();

            // 3. הרצת האלגוריתם וההמתנה ברקע (אסינכרוני!)
            CompletableFuture.runAsync(() -> {
                try {
                    // האלגוריתם האמיתי רץ בחלקיק שנייה
                    galeShapleyService.runGaleShapley(matchmakingService.getCurrentMen(),
                            matchmakingService.getCurrentWomen());

                    // ההמתנה המלאכותית: 10 שניות (10,000 מילישניות)
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                // 4. חזרה ל-UI הראשי, סגירת החלון והצגת התוצאות
                ui.access(() -> {
                    loadingDialog.close();

                    // מעלימים את רשימות ההעדפות כדי לעשות מקום לכרטיסיות
                    listsContainer.setVisible(false);
                    userGrid.setVisible(false);

                    drawMatchCards(); // קריאה לפונקציית הציור
                    showSuccessNotification("השידוך הושלם! התוצאות לפניך.");
                });
            });
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
            // מייצרים 10 גברים ו-10 נשים (סה"כ 20)
            dataGenService.generateAndSaveUsers(10); 
            
            // מרעננים את הטבלה שעל המסך
            refreshGrid();
            
            showSuccessNotification("נוצרו 20 משתמשים חדשים בהצלחה!");
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
                Notification.show("שגיאה בחישוב הציונים: " + ex.getMessage());
            }
        });

        // 4. סידור הרכיבים על המסך
        HorizontalLayout buttonsLayout = new HorizontalLayout(btnGenerateData, btnCalculateScores, btnRunAlgo);
        
        listsContainer.setWidthFull();

        cardsContainer.setWidthFull();
        cardsContainer.getStyle().set("flex-wrap", "wrap"); // מסדר אותם יפה בשורות
        cardsContainer.getStyle().set("justify-content", "center");

        add(title, subtitle, buttonsLayout, userGrid, listsContainer, cardsContainer);

        // טעינה ראשונית של נתונים (אם כבר קיימים ב-DB)
        refreshGrid();
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

 
    private void displayPreferenceLists() {
        listsContainer.removeAll(); // מנקים תצוגה קודמת

        // עמודת גברים
        VerticalLayout menColumn = new VerticalLayout();
        menColumn.add(new H3("רשימות העדפות - גברים 🤵"));
        for (User man : matchmakingService.getCurrentMen()) {
            menColumn.add(createUserDetails(man));
        }

        // עמודת נשים
        VerticalLayout womenColumn = new VerticalLayout();
        womenColumn.add(new H3("רשימות העדפות - נשים 👰"));
        for (User woman : matchmakingService.getCurrentWomen()) {
            womenColumn.add(createUserDetails(woman));
        }

        listsContainer.add(menColumn, womenColumn);
    }

    // הפונקציה שמייצרת את ה"אקורדיון" הנפתח לכל משתמש
    private Details createUserDetails(User user) {
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
    private void drawMatchCards() {
        cardsContainer.removeAll();

        // עוברים על כל הגברים ובודקים את הסטטוס שלהם
        for (User man : matchmakingService.getCurrentMen()) {
            User partner = man.getCurrentPartner();

            // יצירת הקופסה של הכרטיסייה
            VerticalLayout card = new VerticalLayout();
            card.setWidth("320px");
            card.getStyle()
                    .set("border", "1px solid var(--lumo-contrast-20pct)")
                    .set("border-radius", "10px")
                    .set("box-shadow", "0 4px 8px rgba(0,0,0,0.1)") // צללית עדינה
                    .set("padding", "var(--lumo-space-l)")
                    .set("background-color", "white")
                    .set("align-items", "center");

            if (partner != null) {
                // יש שידוך! שולפים את הציון
                double score = findScore(man, partner);

                Span coupleText = new Span("🤵 " + man.getFullName() + "  💍  👰 " + partner.getFullName());
                coupleText.getStyle().set("font-weight", "bold").set("font-size", "1.1em");

                Span scoreText = new Span("⭐ ציון התאמה סופי: " + String.format("%.1f", score) + " ⭐");
                scoreText.getStyle().set("color", "green").set("font-weight", "bold").set("margin-top", "10px");

                card.add(coupleText, scoreText);
                card.getStyle().set("border-top", "6px solid purple"); // פס סגול למעלה לשידוך מוצלח
            } else {
                // אין שידוך
                Span singleText = new Span("🤵 " + man.getFullName());
                singleText.getStyle().set("font-weight", "bold");

                Span noMatchText = new Span("נותר ללא שידוך בסבב זה 💔");
                noMatchText.getStyle().set("color", "gray");

                card.add(singleText, noMatchText);
                card.getStyle().set("border-top", "6px solid gray"); // פס אפור
            }

            cardsContainer.add(card);
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

}