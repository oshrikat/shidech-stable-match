package oshrik.shidech_stable_match.ui;

import java.util.ArrayList;

import com.vaadin.flow.component.button.Button;
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
import oshrik.shidech_stable_match.utilities.SessionHelper;

@Route("/")
public class UserView extends VerticalLayout implements BeforeEnterObserver 
{

    private UserService userService;
    private Grid<User> usersGrid;

    // רכיבי הוספת משתמש
    private TextField userNameTextField;
    private TextField userPassWordField;
    private Button btnInsert;

    public UserView(UserService userService) {

        // אתחול התלויות
        this.userService = userService;

        // --- 1. כותרות ופרטי סשן ---
        String userName = "?";
        User user = (User) SessionHelper.getAttribute("USER");
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

        // טעינה ראשונית של הנתונים
        refreshGrid();

        // --- 5. הוספת כל הרכיבים למסך לפי הסדר ---
        add(title, userInfo, logoutBtn, addLayout, searchLayout, usersGrid);
    }

    private void insertUserToDB() {
        String userName = userNameTextField.getValue();
        String userPassword = userPassWordField.getValue();

        if (userName.isBlank() || userPassword.isBlank()) {
            Notification.show("Username and Password can't be empty!", 3000, Position.MIDDLE);
            return;
        }

        try {
            // קריאה לפונקציה הנכונה מה-Service והוספת המשתמש
            boolean isAdded = userService.addUserToDB(new User(userName, userPassword));

            if (isAdded) {
                Notification.show("User Inserted Successfully!", 3000, Position.BOTTOM_CENTER);
                userNameTextField.clear(); // ניקוי השדות אחרי הוספה
                userPassWordField.clear();
                refreshGrid(); // רענון הטבלה שתראה את המשתמש החדש
            } else {
                Notification.show("User already exists!", 4000, Position.MIDDLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Notification.show("Error: " + e.getMessage(), 5000, Position.MIDDLE);
        }
    }

    private void refreshGrid() {
        usersGrid.setItems(userService.getAllUsers());
    }

    private void logout() {
        SessionHelper.removeAttribute("USER");
        SessionHelper.invalidate();
        // RouteHelper.navigateTo(HomeView.class);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // מנגנון הגנה - אם אין סשן, תיאורטית יזרוק לעמוד לוגין
        // כרגע בהערה כדי שלא יקרוס אם LoginView לא קיים
        /*
         * if (!SessionHelper.isAttributeExist("USER")) {
         * event.forwardTo(LoginView.class);
         * }
         */
    }
}