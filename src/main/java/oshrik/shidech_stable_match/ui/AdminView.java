package oshrik.shidech_stable_match.ui;

import java.util.ArrayList;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.services.DataGenerationService;
import oshrik.shidech_stable_match.services.MatchmakingService;
import oshrik.shidech_stable_match.services.UserService;
import oshrik.shidech_stable_match.utilities.RouteHelper;
import oshrik.shidech_stable_match.utilities.SessionHelper;

@Route(value = "/admin", layout = MainLayout.class)
public class AdminView extends VerticalLayout implements BeforeEnterObserver 
{

    private UserService userService;
    private Grid<User> usersGrid;

    // רכיבי הוספת משתמש
    private TextField userNameTextField;
    private TextField userPassWordField;
    private Button btnInsert, btnClearData;

    // פרטי משתמש
    String userName;

    public AdminView(UserService userService) {

        // אתחול התלויות
        this.userService = userService;

        // --- 1. כותרות ופרטי סשן ---
        userName = "?";
        User user = (User) SessionHelper.getAttribute("currentUser");
        if (user != null) {
            userName = user.getUsername();
        }

        H1 title = new H1("👥 All Users");
        H4 userInfo = new H4("Logged in as: " + userName + " | SessionID: " + SessionHelper.getSessionID());
        Button logoutBtn = new Button("Logout", e -> logout());

        // --- 2. אזור הוספת משתמש חדש ---
        userNameTextField = new TextField("New Username");
        userPassWordField = new TextField("New Password");
        btnInsert = new Button("Add User", e -> insertUserToDB());

        HorizontalLayout addLayout = new HorizontalLayout(userNameTextField, userPassWordField, btnInsert);
        addLayout.setDefaultVerticalComponentAlignment(Alignment.END);

        // --- 3. אזור חיפוש ---
        TextField searchField = new TextField();
        searchField.setPlaceholder("Filter by name...");
        searchField.setClearButtonVisible(true);
        Button searchButton = new Button("Search", VaadinIcon.SEARCH.create());

        searchButton.addClickListener(e -> {
            String searchTerm = searchField.getValue();
            if (searchTerm.isEmpty()) {
                refreshGrid();
            } else {
                usersGrid.setItems(userService.getAllUsersLikeName(searchTerm));
            }
        });

        HorizontalLayout searchLayout = new HorizontalLayout(searchField, searchButton);
        searchLayout.setDefaultVerticalComponentAlignment(Alignment.END);

        // --- 4. הגדרת הגריד (טבלת משתמשים) ---
        usersGrid = new Grid<>(User.class);
        usersGrid.setColumns("username", "password");

        // מחיקת משתמש בלחיצה כפולה
        usersGrid.addItemDoubleClickListener(e -> {
            User cu = e.getItem();
            userService.deleteUser(cu);
            refreshGrid();
            Notification.show("User " + cu.getUsername() + " was deleted!", 3000, Position.BOTTOM_CENTER);
        });

        // כפתור מחיקת משתמשים - ניקיון
        // 1. יצירת רכיב הדיאלוג
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("איפוס נתונים (Fresh Start)");
        dialog.setText("האם אתה בטוח שברצונך למחוק את כל המשתמשים מהמערכת? פעולה זו אינה ניתנת לביטול.");

        // 2. הגדרת כפתור הביטול
        dialog.setCancelable(true);
        dialog.setCancelText("ביטול");

        // 3. הגדרת כפתור האישור (ה-Action האמיתי)
        dialog.setConfirmText("מחק הכל!");
        dialog.setConfirmButtonTheme("error primary"); // הופך את הכפתור לאדום (אזהרה)

        dialog.addConfirmListener(event -> {
            // מחיקה של כל המשתמשים
            userService.deleteAllUsers();
            refreshGrid(); // רענון הטבלה במסך
            Notification.show("המסד נוקה בהצלחה! ✨");
        });

        // 4. יצירת הכפתור שפותח את הדיאלוג
        btnClearData = new Button("Fresh Start 🧹", e -> dialog.open());
        btnClearData.addThemeVariants(ButtonVariant.LUMO_ERROR); // עיצוב אדום לכפתור הראשי

        // טעינה ראשונית של הנתונים
        refreshGrid();

        // --- 5. הוספת כל הרכיבים למסך לפי הסדר ---
        add(title, userInfo, logoutBtn, btnClearData, addLayout, searchLayout, usersGrid);
    }

    private void insertUserToDB() {



    }

    private void refreshGrid() {
        usersGrid.setItems(userService.getAllUsers());
    }

    private void logout() {
        SessionHelper.removeAttribute("currentUser");
        SessionHelper.invalidate();
        // RouteHelper.navigateTo(HomeView.class);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        User u = (User) SessionHelper.getAttribute("currentUser");

        if (u == null)
            event.forwardTo(AuthView.class);
        else {
            if (u.isProfileComplete()) {
                // מדובר במשתמש קיים שכבר עבר את השאלון
                // לא נעשה כלום ..?

            } else {
                // קיים משתמש , אך לא השלים עדיין את השאלון
                // event.forwardTo(Wizard.class);

            }

        }

    }
}