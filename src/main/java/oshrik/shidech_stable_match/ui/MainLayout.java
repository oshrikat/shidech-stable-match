package oshrik.shidech_stable_match.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

@CssImport("./themes/navbar.css")
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
    }

    private void createHeader() {

        // --- לוגו ---
        Span brandName = new Span("You'r Next Shidech");
        brandName.addClassName("brand-name");

        Span brandTagline = new Span("השידעך הבא שלך");
        brandTagline.addClassName("brand-tagline");

        HorizontalLayout brand = new HorizontalLayout(brandName, brandTagline);
        brand.addClassName("brand");
        brand.setDefaultVerticalComponentAlignment(
                FlexComponent.Alignment.BASELINE);
        brand.setSpacing(false);
        brand.getStyle().set("gap", "6px");

        // --- פריטי ניווט ---
        Div navLinks = new Div(
                createNavItem("אדמין משתמשים", UserView.class),
                createNavItem("פיילוט אלגוריתם", MatchAlgoView.class),
                createNavItemDisabled("צ'אט — בקרוב"));
        navLinks.addClassName("nav-links");

        // --- spacer ---
        Div spacer = new Div();
        spacer.getStyle().set("flex", "1");

        // --- תג dev ---
        Span devBadge = new Span("בפיתוח");
        devBadge.addClassName("env-badge");

        // --- הרכבה ---
        HorizontalLayout navbar = new HorizontalLayout(
                brand, navLinks, spacer, devBadge);
        navbar.addClassName("main-navbar");
        navbar.setDefaultVerticalComponentAlignment(
                FlexComponent.Alignment.CENTER);
        navbar.setWidthFull();

        addToNavbar(navbar);
    }

    private Div createNavItem(String label, Class<? extends com.vaadin.flow.component.Component> view) {
        RouterLink link = new RouterLink(label, view);
        link.addClassName("nav-link");
        link.setTabIndex(-1);

        // --- התיקון: אומרים ל-Vaadin מתי ואיך להדליק את הקו התחתון ---
        link.setHighlightCondition(HighlightConditions.sameLocation());
        link.setHighlightAction((item, highlight) -> {
            if (highlight) {
                item.addClassName("active"); // נדלק
            } else {
                item.removeClassName("active"); // נכבה
            }
        });

        Div item = new Div(link);
        item.addClassName("nav-item");
        return item;
    }

    private Div createNavItemDisabled(String label) {
        Span text = new Span(label);
        Div item = new Div(text);
        item.addClassName("nav-item");
        item.addClassName("nav-item--muted");
        return item;
    }
}