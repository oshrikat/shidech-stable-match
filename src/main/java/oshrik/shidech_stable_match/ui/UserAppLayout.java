package oshrik.shidech_stable_match.ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.utilities.RouteHelper;
import oshrik.shidech_stable_match.utilities.SessionHelper;

@CssImport("./themes/navbar.css")
public class UserAppLayout extends AppLayout {

    public UserAppLayout() {
        createHeader();
    }

    private void createHeader() 
    {

        // --- עיצוב הלוגו ---
        Span brandName = new Span("You'r Next Shidech");
        brandName.addClassName("brand-name");
        Span brandTagline = new Span("השידעך הבא שלך");
        brandTagline.addClassName("brand-tagline");

        HorizontalLayout brand = new HorizontalLayout(brandName, brandTagline);
        brand.addClassName("brand");
        brand.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        brand.setSpacing(false);
        brand.getStyle().set("gap", "6px");

        // --- המשימה שלך: קישורי המשתמש ---
        Div navLinks = new Div(
              createNavItem("אזור אישי", UserDashboardView.class),
                createNavItem("השידוך שלי",UserDashboardView.class) // בינתיים
        );
        navLinks.addClassName("nav-links");

        Div spacer = new Div();
        spacer.getStyle().set("flex", "1");

        // --- הרכבה כולל כפתור התנתקות ---
        HorizontalLayout navbar = new HorizontalLayout(brand, navLinks, spacer, createLogoutButton());
        navbar.addClassName("main-navbar");
        navbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        navbar.setWidthFull();

        addToNavbar(navbar);
    }

    private Div createNavItem(String label, Class<? extends com.vaadin.flow.component.Component> view) {
        RouterLink link = new RouterLink(label, view);
        link.addClassName("nav-link");
        link.setTabIndex(-1);
        link.setHighlightCondition(HighlightConditions.sameLocation());
        link.setHighlightAction((item, highlight) -> {
            if (highlight) item.addClassName("active");
            else item.removeClassName("active");
        });
        Div item = new Div(link);
        item.addClassName("nav-item");
        return item;
    }

     private Button createLogoutButton() {
        // כפתור ההתנתקות

        Button logout = new Button("התנתק");

        logout.addClickListener(e ->{
            // ניגש למשתמש שכרגע מחובר ונמחק אותו - נעיף אותו 
            SessionHelper.removeAttribute("currentUser");

            SessionHelper.invalidate();
            
            // נפנה את המשתמש שהתנתק ישר לדף הבית
            RouteHelper.navigateTo(HomeView.class);



        });


        return logout;
    }
}