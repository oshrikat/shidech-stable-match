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

import oshrik.shidech_stable_match.utilities.RouteHelper;
import oshrik.shidech_stable_match.utilities.SessionHelper;

@CssImport("./themes/navbar.css")
public class AdminAppLayout extends AppLayout {

    public AdminAppLayout() {
        createHeader();
    }

    private void createHeader() {
        
        Span brandName = new Span("You'r Next Shidech");
        brandName.addClassName("brand-name");
        Span brandTagline = new Span(" שִׁידֶעךְ הבא שלך");
        brandTagline.addClassName("brand-tagline");

        HorizontalLayout brand = new HorizontalLayout(brandName, brandTagline);
        brand.addClassName("brand");
        brand.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        brand.setSpacing(false);
        brand.getStyle().set("gap", "6px");

        // --- קישורי אדמין ---
        Div navLinks = new Div(
                createNavItem("אדמין משתמשים", AdminView.class),
                createNavItem("פיילוט אלגוריתם", MatchAlgoView.class)
        );
        navLinks.addClassName("nav-links");

        Div spacer = new Div();
        spacer.getStyle().set("flex", "1");
        
        Span devBadge = new Span("Admin Mode 🛠️");
        devBadge.addClassName("env-badge");

        // --- הרכבה כולל כפתור התנתקות ---
        HorizontalLayout navbar = new HorizontalLayout(brand, navLinks, spacer, devBadge, createLogoutButton());
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