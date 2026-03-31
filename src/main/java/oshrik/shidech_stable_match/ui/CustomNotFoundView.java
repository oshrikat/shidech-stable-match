package oshrik.shidech_stable_match.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.ParentLayout;

import oshrik.shidech_stable_match.utilities.RouteHelper;

// שים לב: אין פה @Route! 
@ParentLayout(UserAppLayout.class) // אופציונלי: משאיר את התפריט הראשי גם בדף השגיאה
public class CustomNotFoundView extends VerticalLayout implements HasErrorParameter<NotFoundException> {

    public CustomNotFoundView() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        H1 title = new H1("אופס! 404 🕵️‍♂️");
        Span description = new Span("נראה שהלכת לאיבוד. העמוד שחיפשת לא קיים במערכת.");
        
        Button homeButton = new Button("קח אותי הביתה");
        homeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        homeButton.addClickListener(e -> { 
            RouteHelper.navigateTo("/");
    });

        add(title, description, homeButton);
    }

    // זו הפונקציה ש-Vaadin מפעיל ברגע שיש שגיאת 404
    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        // מחזיר את קוד השגיאה הרשמי של שרת (404 - Not Found)
        return 404; 
    }
}