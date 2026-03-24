package oshrik.shidech_stable_match.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLink;

@CssImport("./themes/navbar.css")
public class MainLayout_old2 extends AppLayout implements AfterNavigationObserver {

    // משתנים לשמירת הכפתורים כדי שנוכל לשנות להם צבע
    private Div usersTab;
    private Div algoTab;

    public MainLayout_old2() {
        createHeader();
    }

    private void createHeader() {
        // --- לוגו ---
        Div brandIcon = new Div();
        brandIcon.addClassName("brand-icon");

        Span brandName = new Span("Shidech");
        brandName.addClassName("brand-name");
        Span brandSub = new Span("Matchmaker");
        brandSub.addClassName("brand-sub");

        Div brandText = new Div(brandName, brandSub);
        Div brand = new Div(brandIcon, brandText);
        brand.addClassName("brand");

        // --- מפריד ---
        Div divider = new Div();
        divider.addClassName("nav-divider-v");

        // --- פריטי ניווט ---
        // כאן אנחנו משתמשים במשתנים שהגדרנו למעלה
        usersTab = createNavItem("אדמין משתמשים", AdminView.class);
        algoTab = createNavItem("פיילוט אלגוריתם", MatchAlgoView.class);
        Div chatTab = createNavItemDisabled("צ'אט", "בקרוב");

        Div navItems = new Div(usersTab, algoTab, chatTab);
        navItems.addClassName("nav-items");

        // --- אווטר משתמש ---
        Div avatar = new Div(new Span("א"));
        avatar.addClassName("nav-avatar");

        // --- הרכבה ---
        HorizontalLayout navbar = new HorizontalLayout(brand, divider, navItems, avatar);
        navbar.addClassName("main-navbar");
        navbar.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        navbar.setWidthFull();

        addToNavbar(navbar);
    }

    private Div createNavItem(String label, Class<? extends com.vaadin.flow.component.Component> view) {
        Span dot = new Span();
        dot.addClassName("nav-dot");

        RouterLink link = new RouterLink(label, view);
        link.addClassName("nav-link");

        Div item = new Div(dot, link);
        item.addClassName("nav-item");
        return item;
    }

    private Div createNavItemDisabled(String label, String badgeText) {
        Span dot = new Span();
        dot.addClassName("nav-dot");
        Span text = new Span(label);
        Span badge = new Span(badgeText);
        badge.addClassName("nav-badge");

        Div item = new Div(dot, text, badge);
        item.addClassName("nav-item");
        item.addClassName("disabled");
        return item;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // א. מנקים את הצבע מכל הכפתורים במעבר דף
        usersTab.removeClassName("active");
        algoTab.removeClassName("active");

        // ב. בודקים באיזו כתובת (URL) אנחנו
        String currentUrl = event.getLocation().getPath();

        // ג. מדליקים את הכפתור הנכון לפי הכתובת
        if (currentUrl.equals("match-algo")) {
            algoTab.addClassName("active");
        } else if (currentUrl.equals("")) { // כאן צריך להיות הראוט של עמוד המשתמשים
            usersTab.addClassName("active");
        }
    }
}