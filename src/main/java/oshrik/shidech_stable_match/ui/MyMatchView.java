package oshrik.shidech_stable_match.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import oshrik.shidech_stable_match.datamodels.Match;
import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.datamodels.Match.MatchStatus;
import oshrik.shidech_stable_match.datamodels.User.Gender;
import oshrik.shidech_stable_match.datamodels.User.ROLE;
import oshrik.shidech_stable_match.services.MatchService;
import oshrik.shidech_stable_match.services.UserService;
import oshrik.shidech_stable_match.utilities.RouteHelper;
import oshrik.shidech_stable_match.utilities.SessionHelper;

@Route(value = "/my-match", layout = UserAppLayout.class)
public class MyMatchView extends VerticalLayout implements BeforeEnterObserver {

    // Services 
    private final MatchService matchService;
    private UserService userService;

    // Enteties : Data Models...
    private User curUserOnline;
    private Match currMatch;


    // Constructor  :
    public MyMatchView(MatchService matchService,UserService userService) {
        this.matchService = matchService;
        this.userService = userService;
        
        // הגדרות עיצוב כלליות
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();


    }

    // המשתמשים צריכים לקבל החלטה שמא מעוניינים בשידוך המוצע או שלא מעוניינים
    private void buildUI1() {
        removeAll(); // ניקוי למקרה של רענון

        H1 title = new H1("מזל טוב! נמצאה עבורך הצעה 💍");

        // שליפת בן הזוג מתוך אובייקט המאץ'
        User partner = (curUserOnline.getGender().equals(Gender.MALE)) ? getWoman(currMatch.getWomanId())
                : getMan(currMatch.getManId());

        H2 nameHeading = new H2(partner.getFullName());
        Span details = new Span("גיל: " + partner.getAge() + " | עיסוק: " + partner.getOccupation());

        // כפתורי החלטה
        Button btnAccept = new Button("אני מעוניין/ת להמשיך", e -> handleResponse(true));
        btnAccept.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);

        Button btnDecline = new Button("לא נראה לי מתאים", e -> handleResponse(false));
        btnDecline.addThemeVariants(ButtonVariant.LUMO_ERROR);

        HorizontalLayout actions = new HorizontalLayout(btnAccept, btnDecline);

        add(title, nameHeading, details, actions);
    }

    // מסך זה ייבנה במצב בו שני המשתמשים מעוניינים להכיר ! לכן
    private void buildUI2() {
        removeAll(); // ניקוי למקרה של רענון

        H1 title = new H1("מזל טוב , שניכם  מעוניינים להכיר ! בואו נתחיל");

        // שליפת בן הזוג מתוך אובייקט המאץ'
        User partner = (curUserOnline.getGender().equals(Gender.MALE)) ? getWoman(currMatch.getWomanId())
                : getMan(currMatch.getManId());

        H2 nameHeading = new H2(partner.getFullName());
        Span details = new Span("גיל: " + partner.getAge() + " | עיסוק: " + partner.getOccupation());

        Button btnContinue = new Button("נתחיל להכיר    ", e -> handleResponse(false));
        btnContinue.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout actions = new HorizontalLayout(btnContinue);

        add(title, nameHeading, details, actions);
    }

    private User getMan(String manID) 
    {
        return userService.findUserById(manID);
    }

    private User getWoman(String womanID) {
         return userService.findUserById(womanID);
    }

    private void handleResponse(boolean isAccepted) {
        // קריאה לשירות לעדכון התשובה
        boolean result = matchService.updateMatchResponse(currMatch.getId(), curUserOnline.getId(), isAccepted,curUserOnline.getGender());
        
        if(result)
            {
                // השידוך התקבל משני הצדדים !
                Notification.show("The Match Acceppted !!! Both Of You Agreed To Try. Now Let's Decide !");
            }
        else
            {
                // או שהשידוך בוטל , או שמחכה לתגובה מהצד השני

                // בוטל לגמרי
                if(!isAccepted)
                    Notification.show("The Match Canceled as you wish... ");
                else
                    Notification.show("Wait to other side to decide...");


            }
            
            RouteHelper.navigateTo(UserDashboardView.class);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        curUserOnline = (User) SessionHelper.getAttribute("currentUser");
        User user_Partner;

        if (curUserOnline == null) {
            event.forwardTo(AuthView.class);
            return;
        }

        if (!curUserOnline.getRole().equals(ROLE.USER)) {
            event.forwardTo(AdminView.class);
            return;
        }

        // בדיקה האם קיים שידוך פעיל
        currMatch = matchService.getCurrentActiveOrPendingMatch(curUserOnline.getId(), curUserOnline.getGender());
        // נשיג את הזוג

        if (curUserOnline.getGender().equals(Gender.MALE))
            user_Partner = userService.findUserById(currMatch.getWomanId());
        else
            user_Partner = userService.findUserById(currMatch.getManId());

        System.out.println("Match status: " + currMatch);

        if (currMatch == null) {
            event.forwardTo(UserDashboardView.class);
        } else {
            // רק אם הכל תקין, בונים את התצוגה של השידוך הנוכחי - לפי מצבו
            if (!currMatch.getStatus().equals(MatchStatus.ACTIVE_DATING))
                buildUI1();
            else
                buildUI2();
        }
    }
}