package oshrik.shidech_stable_match.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;

/**
 * ה-Layout הראשי - שימוש ברכיב Tabs המובנה של Vaadin לתפריט עליון מקצועי.
 */
public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
    }

    private void createHeader() {
        // 1. הלוגו שלנו
        H1 logo = new H1("Shidech Matchmaker 💍");
        logo.getStyle()
            .set("font-size", "var(--lumo-font-size-l)")
            .set("margin", "1000 var(--lumo-space-l) 1000 var(--lumo-space-m)");

        // 2. יצירת תפריט הלשוניות (Tabs)
        Tabs menuTabs = new Tabs();
        
        // הוספת הלשוניות (משתמשים בפונקציית העזר שיצרנו למטה)
        menuTabs.add(
            createTab("אדמין משתמשים", UserView.class),
            createTab("פיילוט אלגוריתם", MatchAlgoView.class),
            createTab("צ'אט (בקרוב)", UserView.class) // כרגע מפנה לאותו מקום עד שניצור דף
        );

        // הגדרה שהתפריט ייקח את הרוחב שנותר וייראה טוב
        menuTabs.setWidthFull();

        // 3. חיבור הלוגו והתפריט לשורה אחת (Header)
        HorizontalLayout header = new HorizontalLayout(logo, menuTabs);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        
        // הוספה ל-Navbar העליון של האפליקציה
        addToNavbar(header);
    }

    /**
     * פונקציית עזר (Helper) מקצועית: 
     * מחברת בין לשונית (Tab) לבין קישור לדף (RouterLink) בצורה תקנית של Vaadin.
     */
    private Tab createTab(String viewName, Class<? extends com.vaadin.flow.component.Component> viewClass) {
        RouterLink link = new RouterLink();
        link.add(viewName);
        link.setRoute(viewClass);
        link.setTabIndex(-1); // מונע בעיות ניווט עם המקלדת

        return new Tab(link);
    }
}