package oshrik.shidech_stable_match.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
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
public class AuthView extends HorizontalLayout
{


    // container (Place-Holder) to the form - easy to manage whose place currently...
    private VerticalLayout authFormLayout ;

    // services 
    private UserService userService;

    public AuthView(UserService userService)
    {
        this.userService = userService;

        getStyle().set("direction", "rtl");
        setSizeFull();
        getStyle().setBackgroundColor("#8492b0");

        // יצירת מופע של השומר מקום לטופס 
        authFormLayout = new VerticalLayout();
        authFormLayout.setWidth("40%");
        authFormLayout.setHeightFull();
        
        // באופן דפולטיבי בתהחלה נרצה שיהיה טופס הרשמה
        authFormLayout.add(createRegisterForm());

        // הוספה למסך הכולל את שני הרכיבים שלי
        add(authFormLayout,createSlideShowIamgesAside());


    }   


    /**
     * יצירת טופס התחברות למערכת - באמצעות vertical 
     * @return החזרת layout מובנה
     */
    public VerticalLayout createLoginForm()
    {
        
        VerticalLayout loginForm = new VerticalLayout();

        loginForm.setWidthFull();
        loginForm.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // ממנרכז אנכית
        loginForm.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER); // ממנרכז אופקית

        H2 title = new H2("התחברות למערכת");
        title.getStyle().set("color", "white");

        EmailField emailField = new EmailField("כתובת אימייל");
        emailField.getStyle().set("color", "white");
        emailField.setWidth("80%");
        emailField.setRequiredIndicatorVisible(true);
        emailField.setErrorMessage("Please Fill the email !");

        PasswordField passwordField = new PasswordField("סיסמה");
        passwordField.getStyle().set("color", "white");
        passwordField.setWidth("80%");
        passwordField.setRequiredIndicatorVisible(true);
        passwordField.setErrorMessage("Please Fill the password !");

        Button btn_Login_Submit = new Button("התחבר למערכת");
        btn_Login_Submit.setWidth("80%");
        btn_Login_Submit.getStyle().set("background-color", "#bababa"); // צבע הכפתור המרכזי שלנו
        btn_Login_Submit.getStyle().set("color", "white");

        btn_Login_Submit.addClickListener(e ->
        {
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
            }
            else {
                // צביעת השדות הריקים באדום כדי שה-ErrorMessage יוצג
                if (emailField.isEmpty())
                    emailField.setInvalid(true);
                if (passwordField.isEmpty())
                    passwordField.setInvalid(true);

                // Notification.show("Should fill all the details !!!!!!!!", 3000,
                // Position.TOP_STRETCH);
            }
        });

        
        // כפתור המעבר (Toggle) שמעוצב כמו קישור
        Button btn_toggleToRegister = new Button("עדיין אין לך חשבון? הירשם כאן");
        btn_toggleToRegister.getStyle().set("background-color", "transparent");
        btn_toggleToRegister.getStyle().set("color", "rgb(21, 10, 30)");
        btn_toggleToRegister.getStyle().set("cursor", "pointer");
        btn_toggleToRegister.getStyle().set("margin-top", "20px");

        
        // החלפה לטופס של הרשמה
        btn_toggleToRegister.addClickListener(e -> 
        {
            // 1. נוריד את מה שמוצג כרגע - את הניפוח של ההתחברות
            authFormLayout.removeAll();
            
            // 2. ננפח לתוך החלק הזה את הטופס ההרשמה באמצעות add ל layout
            authFormLayout.add(createRegisterForm());

 
        });

        loginForm.add(title, emailField, passwordField, btn_Login_Submit, btn_toggleToRegister);

        return loginForm;
    }
    
     /**
     * יצירת טופס הרשמה למערכת - באמצעות vertical 
     * @return החזרת layout מובנה
     */
    public VerticalLayout createRegisterForm() 
    {

        VerticalLayout registerForm = new VerticalLayout();
        registerForm.setWidthFull();
        registerForm.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // ממנרכז אנכית
        registerForm.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER); // ממנרכז אופקית

        H2 title = new H2("הרשמה למערכת");
        title.getStyle().set("color", "white");

        TextField nameField = new TextField("שם פרטי");
        nameField.getStyle().setColor("#cbbfbf");
        nameField.setWidth("80%");
        nameField.setRequiredIndicatorVisible(true);
        nameField.setErrorMessage("Please Fill the Name !");

        EmailField emailField = new EmailField("כתובת אימייל");
        emailField.getStyle().set("color", "white");
        emailField.setWidth("80%");
        emailField.setRequiredIndicatorVisible(true);
        emailField.setErrorMessage("Please Fill the email !");

        PasswordField passwordField = new PasswordField("סיסמה");
        passwordField.getStyle().set("color", "white");
        passwordField.setWidth("80%");
        passwordField.setRequiredIndicatorVisible(true);
        passwordField.setErrorMessage("Please Fill the Password !");

        Button btn_Register_Submit = new Button("הירשם והתחל");
        btn_Register_Submit.setWidth("80%");
        btn_Register_Submit.getStyle().set("background-color", "#bababa"); // צבע הכפתור המרכזי שלנו
        btn_Register_Submit.getStyle().set("color", "white");

        btn_Register_Submit.addClickListener(e ->
        {
            if (!nameField.isEmpty() && !passwordField.isEmpty() && !emailField.isEmpty() && !emailField.isInvalid()) {
                User newUser = new User(nameField.getValue(), passwordField.getValue(), emailField.getValue());

                boolean seccessful = userService.registerNewUser(newUser);
                if (seccessful) {
                    Notification.show("User Seccecfully Registered to System !", 5000, Position.MIDDLE, true);
                    SessionHelper.setAttribute("currentUser", newUser);

                    RouteHelper.navigateTo(UserDashboardView.class); // לבינתיים - עד שניצור את השאלון
                } else
                    Notification.show("User Could'nt Register to System ! Please Try Later...", 5000,
                            Position.TOP_STRETCH, true);
            }
            else {
                // צביעת השדות הריקים באדום כדי שה-ErrorMessage יוצג
                if (nameField.isEmpty())
                    nameField.setInvalid(true);
                if (emailField.isEmpty() && emailField.isInvalid())
                    emailField.setInvalid(true);
                if (passwordField.isEmpty())
                    passwordField.setInvalid(true);

                // Notification.show("Should fill all the details !!!!!!!!", 3000,
                // Position.TOP_STRETCH);
            }
        });


        // כפתור המעבר (Toggle) שמעוצב כמו קישור
        Button btn_toggleToLogin = new Button("כבר יש לך חשבון? התחבר כאן");
        btn_toggleToLogin.getStyle().set("background-color", "transparent");
        btn_toggleToLogin.getStyle().set("color", "#0a0707");
        btn_toggleToLogin.getStyle().set("cursor", "pointer");
        btn_toggleToLogin.getStyle().set("margin-top", "20px");

        // החלפה לטופס של התחברות
        btn_toggleToLogin.addClickListener(e -> 
        {
            // 1. נוריד את מה שמוצג כרגע - את הניפוח של ההרשמה
            authFormLayout.removeAll();
            
            // 2. ננפח לתוך החלק הזה את הטופס התחברות באמצעות add ל layout
            authFormLayout.add(createLoginForm());

 
        });

        registerForm.add(title, nameField, emailField, passwordField, btn_Register_Submit, btn_toggleToLogin);
        return registerForm;
    }


     /**
     * יצירת טופס התחברות למערכת - באמצעות vertical 
     * @return החזרת layout מובנה
     */
    public HorizontalLayout createSlideShowIamgesAside()
    {
        // בתכנון להמשך להציג מספר תמונות שיוחלפו כל כמה שניות לחוויה גמישה וזורמת
 
        // יצירת layout
        HorizontalLayout imageLayout = new HorizontalLayout();
        imageLayout.setSizeFull();
        imageLayout.getStyle().set("background-image", "url('Spider-Man.png')");
        imageLayout.getStyle().set("background-size", "cover");
        imageLayout.getStyle().set("background-position", "center");
        
        imageLayout.setWidth("60%");

        return imageLayout;
    }
    

}
