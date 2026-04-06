package oshrik.shidech_stable_match.ui;

import java.util.List;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.messages.MessageListItem;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import oshrik.shidech_stable_match.datamodels.ChatMessage;
import oshrik.shidech_stable_match.datamodels.Match;
import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.datamodels.Match.MatchStatus;
import oshrik.shidech_stable_match.datamodels.User.Gender;
import oshrik.shidech_stable_match.datamodels.User.ROLE;
import oshrik.shidech_stable_match.services.ChatService;
import oshrik.shidech_stable_match.services.MatchService;
import oshrik.shidech_stable_match.services.UserService;
import oshrik.shidech_stable_match.services.ChatService.ChatCallBack;
import oshrik.shidech_stable_match.utilities.RouteHelper;
import oshrik.shidech_stable_match.utilities.SessionHelper;

@Route(value = "/my-match", layout = UserAppLayout.class)
public class MyMatchView extends VerticalLayout implements BeforeEnterObserver, ChatCallBack {

    // Services 
    private final MatchService matchService;
    private final UserService userService;
    private final ChatService chatService;

    // Enteties : Data Models...
    private User curUserOnline;
    private Match currMatch;
    private MessageList chatList;
    private UI ui;
    private User partner;


    // Constructor  :
    public MyMatchView(MatchService matchService, UserService userService, ChatService chatService) {
        this.matchService = matchService;
        this.userService = userService;
        this.chatService = chatService;

        // הגדרות עיצוב כלליות
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();


    }

    // המשתמשים צריכים לקבל החלטה שמא מעוניינים בשידוך המוצע או שלא מעוניינים
    private void buildUI1() {
        removeAll(); // ניקוי למקרה של רענון

        H1 title = new H1("מזל טוב! נמצאה עבורך הצעה 💍");

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

    // מסך זמני דפולטיבי כאשר אני לא יודע לאן לנווט - זמני !
    private void buildUI3() {
        removeAll(); // ניקוי למקרה של רענון

        H1 title = new H1("מזל טוב , שניכם  מעוניינים להכיר !  - אנחנו לא יודעים מה לעשות אתכם למען האמת");

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

    private void buildUI2_CHAT() {
        removeAll();

        H2 title = new H2("צ'אט היכרות עם " + partner.getFirstName() + " 💬");

        chatList = new MessageList();
        chatList.setWidth("100%");
        chatList.getStyle().set("max-height", "400px");

        MessageInput input = new MessageInput();
        input.setWidth("100%");
        input.addSubmitListener(e -> {
            // ---> שליחת הודעה <---
            chatService.sendMessage(currMatch.getId(), curUserOnline.getId(), partner.getId(), e.getValue());

            // שליהה אליי
            onNewMessageArrived(
                    new ChatMessage(currMatch.getId(), curUserOnline.getId(), curUserOnline.getId(), e.getValue()));
        });

        add(title, chatList, input);
    }

    @Override
    public void onNewMessageArrived(ChatMessage message) {
        ui.access(() -> {
            // ---> קבלת הודעה בלייב <---
            // צור MessageListItem חדש מתוך ה-message שקיבלת והוסף אותו ל-chatList
            MessageListItem newMessage = new MessageListItem();
            newMessage.setText(message.getContent());
            newMessage.setTime(message.getTimestamp().toInstant(java.time.ZoneOffset.UTC));
            newMessage.setUserName(message.getSenderId().equals(curUserOnline.getId()) ? curUserOnline.getUsername()
                    : partner.getFirstName());
            newMessage.setUserAbbreviation("***SENDER...****");

            chatList.addItem(newMessage);

        });
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        ui = attachEvent.getUI();

        if (chatList != null) {

        // ---> הרשמה למרכזייה <---
        // קרא ל-register והעבר את ה-ID שלך ואת this (המסך שמאזין)

        chatService.register(curUserOnline.getId(), this);

        // ---> טעינת היסטוריה <---
        // שלוף את ההיסטוריה מה-Service לתוך רשימה
        List<ChatMessage> history = chatService.getChatHistory(currMatch.getId());

        // המרה לפורמט התצוגה של

        if (history != null) {
            List<MessageListItem> items = history.stream()
                    .map(msg -> new MessageListItem(
                            msg.getContent(),
                            msg.getTimestamp().toInstant(java.time.ZoneOffset.UTC),
                            msg.getSenderId().equals(curUserOnline.getId()) ? curUserOnline.getFirstName()
                                    : partner.getFirstName()))
                    .toList();
            chatList.setItems(items);
        }
    }

    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        super.onDetach(detachEvent);

        // ---> התנתקות (רק אם באמת היינו בצ'אט!) <---
        if (chatList != null) {
        chatService.unregister(curUserOnline.getId());
    }

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        curUserOnline = (User) SessionHelper.getAttribute("currentUser");

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
            partner = userService.findUserById(currMatch.getWomanId());
        else
            partner = userService.findUserById(currMatch.getManId());

        System.out.println("Match status: " + currMatch);

        if (currMatch == null) {
            event.forwardTo(UserDashboardView.class);
        } else {
            // רק אם הכל תקין, בונים את התצוגה של השידוך הנוכחי - לפי מצבו
            if (currMatch.getStatus().equals(MatchStatus.PENDING_RESPONSES))
                buildUI1();
            else if (currMatch.getStatus().equals(MatchStatus.PRE_DATING_EVALUATION))
                buildUI2_CHAT();
            else
                buildUI3();

        }
    }

}