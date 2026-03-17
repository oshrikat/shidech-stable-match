package oshrik.shidech_stable_match.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.details.Details; 
import com.vaadin.flow.component.html.H3;


import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.repositories.UserRepository;
import oshrik.shidech_stable_match.services.DataGenerationService;
import oshrik.shidech_stable_match.services.MatchmakingService;
import oshrik.shidech_stable_match.utilities.ScorePair;

// הכתובת אליה ניגש בדפדפן: localhost:8080/match-algo
@Route("/match-algo")
@PageTitle("Algorithm Testing Lab")
public class MatchAlgoView extends VerticalLayout {

    // הזרקת השירותים
    private final DataGenerationService dataGenService;
    private final MatchmakingService matchmakingService;
    private final UserRepository userRepository;

    // רכיב תצוגה - טבלה
    private final Grid<User> userGrid = new Grid<>(User.class, false);
    
    // אזור תצוגה חדש עבור רשימות ההעדפות
    private final HorizontalLayout listsContainer = new HorizontalLayout();

    public MatchAlgoView(DataGenerationService dataGenService, MatchmakingService matchmakingService, UserRepository userRepository)             
    {
       // אתחול תלויות 
        this.dataGenService = dataGenService;
        this.matchmakingService = matchmakingService;
        this.userRepository = userRepository;

        // הגדרות עיצוב כלליות למסך
        setSpacing(true);
        setPadding(true);
        setSizeFull();

        // 1. כותרת המסך
        H2 title = new H2("/t/t🧪 מעבדת אלגוריתם שידוכים");
        Span subtitle = new Span("כאן נבדוק את השלבים השונים של האלגוריתם, צעד אחר צעד.");
        
        // 2. הגדרת כפתורי ההפעלה
        Button btnGenerateData = new Button("1. ייצור נתוני בדיקה (Data Generation)");
        btnGenerateData.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        Button btnCalculateScores = new Button("2. חישוב ציונים והעדפות (Scoring)");
        btnCalculateScores.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

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
        HorizontalLayout buttonsLayout = new HorizontalLayout(btnGenerateData, btnCalculateScores);
        
        listsContainer.setWidthFull();
        add(title, subtitle, buttonsLayout, userGrid, listsContainer);

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


}