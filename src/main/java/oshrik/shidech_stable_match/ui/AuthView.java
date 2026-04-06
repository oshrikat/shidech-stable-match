package oshrik.shidech_stable_match.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.datamodels.User.ROLE;
import oshrik.shidech_stable_match.services.UserService;
import oshrik.shidech_stable_match.utilities.RouteHelper;
import oshrik.shidech_stable_match.utilities.SessionHelper;

@Route(value = "/auth")
public class AuthView extends HorizontalLayout {

    // container (Place-Holder) to the form - easy to manage whose place currently...
    private VerticalLayout authFormLayout;

    // services 
    private UserService userService;

    public AuthView(UserService userService) {
        this.userService = userService;

        // --- 1. הגדרות המסך הראשי (רקע על כל המסך) ---
        setSizeFull();
        setPadding(false); // ביטול רווחים פנימיים כדי שהרקע ייגע בקצוות
        setMargin(false);
        getStyle().set("direction", "rtl");

        // הגדרת תמונת הרקע על המסך המרכזי
        getStyle().set("background-image", "url('background.png')");
        getStyle().set("background-size", "cover");
        getStyle().set("background-position", "center");
        getStyle().set("background-repeat", "no-repeat");

        // --- 2. יצירת סרגל הצד (Glassmorphism) ---
        authFormLayout = new VerticalLayout();
        authFormLayout.setWidth("450px"); // רוחב קבוע לסרגל הצד
        authFormLayout.setHeightFull();
        // authFormLayout.getElement().getThemeList().add("dark");

        // צבע רקע כהה ושקוף למחצה (המספר 0.7 בסוף קובע את רמת האטימות)
        authFormLayout.getStyle().set("background-color", "rgba(20, 15, 30, 0.75)");

        // אפקט טשטוש עתידני למה שמוסתר מאחורי הסרגל
        authFormLayout.getStyle().set("backdrop-filter", "blur(10px)");

        // צל עדין בצד שמאל כדי להפריד את הסרגל מהרקע
        authFormLayout.getStyle().set("box-shadow", "-5px 0px 20px rgba(0,0,0,0.6)");

        // באופן דפולטיבי בהתחלה נרצה שיהיה טופס הרשמה
        authFormLayout.add(createRegisterForm());

        // --- 3. הוספת הסרגל למסך ---
        // מכיוון שאנחנו ב-HorizontalLayout עם direction: rtl, הוא אוטומטית ייצמד לימין!
        add(authFormLayout);

    }

    /**
     * יצירת טופס התחברות למערכת - באמצעות vertical 
     * @return החזרת layout מובנה
     */
    public VerticalLayout createLoginForm() {
        VerticalLayout loginForm = new VerticalLayout();
        loginForm.setWidthFull();
        loginForm.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // ממנרכז אנכית
        loginForm.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER); // ממנרכז אופקית

        H2 title = new H2("התחברות למערכת");
        title.getStyle().set("color", "white");

        EmailField emailField = new EmailField("כתובת אימייל");
        emailField.setWidth("80%");
        emailField.setRequiredIndicatorVisible(true);
        emailField.setErrorMessage("Please Fill the email !");
        applyWhiteOutlineStyle(emailField);

        PasswordField passwordField = new PasswordField("סיסמה");
        passwordField.setWidth("80%");
        passwordField.setRequiredIndicatorVisible(true);
        passwordField.setErrorMessage("Please Fill the password !");
        applyWhiteOutlineStyle(passwordField);

        Button btn_Login_Submit = new Button("התחבר למערכת");
        btn_Login_Submit.setWidth("80%");
        btn_Login_Submit.getStyle().set("background-color", "#bababa");
        btn_Login_Submit.getStyle().set("color", "black"); // שיניתי לשחור שיהיה קריא על הרקע הבהיר של הכפתור

        btn_Login_Submit.addClickListener(e -> {
            if (!emailField.isEmpty() && !passwordField.isEmpty() && !emailField.isInvalid()) {
                User loginUser = userService.loginUser(emailField.getValue(), passwordField.getValue());

                if (loginUser != null) {
                    Notification.show("User Seccecfully Logged In to System !", 2000, Position.MIDDLE, true);
                    SessionHelper.setAttribute("currentUser", loginUser);

                    if (loginUser.getRole().equals(ROLE.USER))
                        RouteHelper.navigateTo(UserDashboardView.class);
                    else
                        RouteHelper.navigateTo(AdminView.class);

                } else
                    Notification.show("User Could'nt Log in to System ! Please Try Later...", 2000,
                            Position.TOP_STRETCH, true);
            } else {
                if (emailField.isEmpty())
                    emailField.setInvalid(true);
                if (passwordField.isEmpty())
                    passwordField.setInvalid(true);
            }
        });

        // כפתור המעבר (Toggle) שמעוצב כמו קישור
        Button btn_toggleToRegister = new Button("עדיין אין לך חשבון? הירשם כאן");
        btn_toggleToRegister.getStyle().set("background-color", "transparent");
        btn_toggleToRegister.getStyle().set("color", "white"); // שיניתי ללבן כדי שיראו על הרקע הכהה
        btn_toggleToRegister.getStyle().set("cursor", "pointer");
        btn_toggleToRegister.getStyle().set("margin-top", "20px");

        // החלפה לטופס של הרשמה
        btn_toggleToRegister.addClickListener(e -> {
            authFormLayout.removeAll();
            authFormLayout.add(createRegisterForm());
        });

        loginForm.add(title, emailField, passwordField, btn_Login_Submit, btn_toggleToRegister);
        return loginForm;
    }

    /**
     * יצירת טופס הרשמה למערכת - באמצעות vertical
     * 
     * @return החזרת layout מובנה
     */
    public VerticalLayout createRegisterForm() {
        VerticalLayout registerForm = new VerticalLayout();
        registerForm.setWidthFull();
        registerForm.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // ממנרכז אנכית
        registerForm.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER); // ממנרכז אופקית

        H2 title = new H2("הרשמה למערכת");
        title.getStyle().set("color", "white");

        TextField nameField = new TextField("שם פרטי");
        nameField.getStyle().setColor("white");
        // צבע הלייבל
        nameField.getStyle().set("--lumo-secondary-text-color", "red");
        nameField.setWidth("80%");
        applyWhiteOutlineStyle(nameField);
        nameField.setErrorMessage("Please Fill the Name !");

        EmailField emailField = new EmailField("כתובת אימייל");
        emailField.getStyle().set("color", "white");
        emailField.setWidth("80%");
        applyWhiteOutlineStyle(emailField);
        emailField.setErrorMessage("Please Fill the email !");

        PasswordField passwordField = new PasswordField("סיסמה");
        passwordField.getStyle().set("color", "white");
        passwordField.setWidth("80%");
        applyWhiteOutlineStyle(passwordField);
        passwordField.setErrorMessage("Please Fill the Password !");

        Button btn_Register_Submit = new Button("הירשם והתחל");
        btn_Register_Submit.setWidth("80%");
        btn_Register_Submit.getStyle().set("background-color", "#bababa");
        btn_Register_Submit.getStyle().set("color", "black");

        btn_Register_Submit.addClickListener(e -> {
            if (!nameField.isEmpty() && !passwordField.isEmpty() && !emailField.isEmpty() && !emailField.isInvalid()) {
                User newUser = new User(nameField.getValue(), passwordField.getValue(), emailField.getValue());

                boolean seccessful = userService.registerNewUser(newUser);
                if (seccessful) {
                    Notification.show("User Seccecfully Registered to System !", 5000, Position.MIDDLE, true);
                    SessionHelper.setAttribute("currentUser", newUser);

                    RouteHelper.navigateTo(UserDashboardView.class);
                } else
                    Notification.show("User Could'nt Register to System ! Please Try Later...", 5000,
                            Position.TOP_STRETCH, true);
            } else {
                if (nameField.isEmpty())
                    nameField.setInvalid(true);
                if (emailField.isEmpty() && emailField.isInvalid())
                    emailField.setInvalid(true);
                if (passwordField.isEmpty())
                    passwordField.setInvalid(true);
            }
        });

        // כפתור המעבר (Toggle) שמעוצב כמו קישור
        Button btn_toggleToLogin = new Button("כבר יש לך חשבון? התחבר כאן");
        btn_toggleToLogin.getStyle().set("background-color", "transparent");
        btn_toggleToLogin.getStyle().set("color", "white"); // שיניתי ללבן
        btn_toggleToLogin.getStyle().set("cursor", "pointer");
        btn_toggleToLogin.getStyle().set("margin-top", "20px");

        // החלפה לטופס של התחברות
        btn_toggleToLogin.addClickListener(e -> {
            authFormLayout.removeAll();
            authFormLayout.add(createLoginForm());
        });

        registerForm.add(title, nameField, emailField, passwordField, btn_Register_Submit, btn_toggleToLogin);
        return registerForm;
    }

    /**
     * פונקציית עזר להלבשת עיצוב אחיד (לייבל לבן, מסגרת לבנה, רקע שקוף) על שדות קלט
     */
    private void applyWhiteOutlineStyle(com.vaadin.flow.component.HasStyle field) {
        // צבע הלייבל
        field.getStyle().set("--lumo-secondary-text-color", "white");
        // צבע הטקסט המוקלד
        field.getStyle().set("--lumo-body-text-color", "white");
        // ביטול הרקע האפור הדיפולטיבי של Vaadin
        field.getStyle().set("--lumo-contrast-10pct", "transparent");
        // צביעת המסגרת בלבן (Vaadin משתמש במשתנה הזה למסגרות של שדות)
        field.getStyle().set("--lumo-contrast-50pct", "white");
    }
}