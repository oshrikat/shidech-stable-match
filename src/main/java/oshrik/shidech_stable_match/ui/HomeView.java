package oshrik.shidech_stable_match.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style.Display;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin.Minus.Horizontal;

import oshrik.shidech_stable_match.utilities.RouteHelper;

@Route(value = "/", layout = MainLayout.class)
@PageTitle("Shidech Matchmaker | דף הבית")
public class HomeView extends VerticalLayout 
{

    // רכיבים למסך
    private H1 header_heroSection;
    private Paragraph text_heroSection;
    private Button btn_startNow_heroSection;


    public HomeView() {
        // הגדרות בסיסיות לדף כולו - בלי ריווחים כדי שהצבעים יימרחו עד הקצה
        setWidthFull();
        setPadding(false);
        setSpacing(false);

        // קריאה לפונקציה שבונה את הבלוק הראשון והוספתו למסך
        add(createHeroSection());

        // בלוק שני - תהליך המערכת
        add(createHowItWorksSection());

        // בלוק שלישי - הצגת אנשים - חווית משתמש - דירוגים
        add(createTestimonialsSection());

        // בלוק ארבע - הצגת נתונים + סטטיסטיקות נוכחיות על המערכת
        add(createStatsSection());

        // בלוק חמש - הצגת אפשרות הרשמה למערכת בקליק
        add(createCtaSection());

        // בלוק שישי - הצגת פוטר - סרגל תחתון של המערכת
        add(createFooterSection());

        
    }

    private VerticalLayout createHeroSection() {
        VerticalLayout heroLayout = new VerticalLayout();

        // 1. הגדרת צבע רקע כהה (#2b2b2b) ומרכוז כל התוכן לאמצע
        heroLayout.getStyle().set("background-color", "#2b2b2b");
        heroLayout.getStyle().set("color", "white");
        heroLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        heroLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // ממנרכז גם אנכית
        heroLayout.setHeight("55vh"); // גובה של 60% מהמסך

        // 2. יצירת הרכיבים (כותרת, טקסט, כפתור)
        
        // כותרת
        Span textSpan = new Span("שידוך מבוסס ");
        textSpan.getStyle().setColor("white");
        Span purpleSpan = new Span("מדע");
        purpleSpan.getStyle().set("color", "#7F77DD");
        header_heroSection = new H1(textSpan,purpleSpan);
       
        // טקסט אודות התהליך - מערכת 
        text_heroSection = new Paragraph("לא מזל, לא אינטואיציה — אלגוריתם שמנתח אלפי נקודות נתונים ומוצא את ההתאמה האמיתית שלך.\r\n");
        // כפתור הרשמה ראשוני
        btn_startNow_heroSection = new Button("שדך אותי עכשיו   ");
        btn_startNow_heroSection.getStyle().set("color","#d8cfcf");
        btn_startNow_heroSection.getStyle().setBorder("1px solid white"); 
        btn_startNow_heroSection.getStyle().setBorderRadius("45px"); 
        btn_startNow_heroSection.getStyle().set("background-color", "transparent");

        // טיפול בלחיצה על כפתור של רישום או התחברות
        btn_startNow_heroSection.addClickListener(e -> {
            RouteHelper.navigateTo(AuthView.class);
        });

        heroLayout.add(header_heroSection,text_heroSection,btn_startNow_heroSection);

        return heroLayout;
    }


    // יצירת createHowItWorksSection בצורה מסודרת ויעילה
    private VerticalLayout createHowItWorksSection() {
        // 1. העוטף הראשי של כל האזור הזה (עמודה)
        VerticalLayout sectionLayout = new VerticalLayout();
        sectionLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER); // ממרכז את הכותרת
        sectionLayout.getStyle().set("padding", "80px 10%"); // נותן אוויר מלמעלה ולמטה

        // 2. כותרת האזור
        H3 sectionTitle = new H3("איך זה עובד");
        sectionTitle.getStyle().set("color", "gray"); // לפי העיצוב זה טקסט אפור ורגוע

        // 3. יצירת השלבים (הקוד המעולה שלך!)
        VerticalLayout v1 = buildStage(1, "שאלון חכם", "עונים על שאלות שמגלות מי אתה באמת, לא רק מה אתה מחפש");
        v1.add(new Hr());
        VerticalLayout v2 = buildStage(2, "האלגוריתם מנתח", "מערכת מתמטית מנתחת תאימות עמוקה בין כל המשתמשים");
        v2.add(new Hr());
        VerticalLayout v3 = buildStage(3, "קבלת התאמה מדויקת", "מקבלים הצעת שידוך מותאמת אישית עם ציון התאמה");
        v3.add(new Hr());
        
        // 4. מכניסים את שלושת השלבים לשורה אחת
        HorizontalLayout stepsRow = new HorizontalLayout(v1, v2, v3);
        stepsRow.setWidthFull();
        stepsRow.setJustifyContentMode(FlexComponent.JustifyContentMode.EVENLY); // מרווח אותם יפה ורחב אחד מהשני

        // 5. מוסיפים את הכותרת ומייד אחריה את שורת השלבים
        sectionLayout.add(sectionTitle, stepsRow);

        return sectionLayout;
    }



    // פונקציית עזר: בניית עמודה של שלב בודד (מספר, כותרת, טקסט)
    private VerticalLayout buildStage(int numLevel, String headerText, String descriptionText) {
        VerticalLayout stageLayout = new VerticalLayout();

        // 1. העיגול הכהה עם המספר
        Div circle = new Div();
        circle.setText(String.valueOf(numLevel));
        circle.getStyle().set("width", "40px");
        circle.getStyle().set("height", "40px");
        circle.getStyle().set("background-color", "#2b2b2b"); // צבע כהה
        circle.getStyle().set("color", "white");
        circle.getStyle().set("border-radius", "50%"); // הופך לעיגול
        // הטריק למרכז את המספר בדיוק באמצע העיגול:
        circle.getStyle().set("display", "flex"); 
        circle.getStyle().set("align-items", "center");
        circle.getStyle().set("justify-content", "center");
        circle.getStyle().set("font-weight", "bold");

        // 2. כותרת השלב (משתמשים ב-H3 כדי שלא יהיה ענק כמו H1)
        H3 header = new H3(headerText);
        header.getStyle().set("margin-top", "15px");
        header.getStyle().set("margin-bottom", "0");

        // 3. טקסט ההסבר
        Paragraph text = new Paragraph(descriptionText);
        text.getStyle().set("text-align", "center");
        text.getStyle().set("color", "gray"); // צבע עדין יותר לטקסט משני

        // 4. חיבור והרכבה
        stageLayout.add(circle, header, text);
        // ממרכזים את כל הרכיבים (העיגול והטקסטים) לאמצע העמודה
        stageLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER); 

        return stageLayout;
    }


    // ---------------------------------------------------------
    // אזור המלצות (Testimonials)
    // ---------------------------------------------------------
    private VerticalLayout createTestimonialsSection() {
        VerticalLayout sectionLayout = new VerticalLayout();
        sectionLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        // רקע אפרפר בהיר כדי לשבור את הלבן, וריווח נדיב
        sectionLayout.getStyle().set("background-color", "#f9f9f9"); 
        sectionLayout.getStyle().set("padding", "80px 10%");

        // כותרת האזור
        H3 sectionTitle = new H3("מה אומרים המשתמשים");
        sectionTitle.getStyle().set("color", "gray");
        sectionTitle.getStyle().set("margin-bottom", "40px");

        // בניית שלוש הכרטיסיות עם הטקסטים מהעיצוב
        VerticalLayout card1 = buildReviewCard(
            "הייתי סקפטית. אחרי ההצעה הראשונה כבר לא הייתי. הבנתי את האיכויות של המערכת המדהימה הזו.", 
            "שירה מ.", "חיפה");
            
        VerticalLayout card2 = buildReviewCard(
            "סוף סוף שידוכים ללא פוליטיקה. האלגוריתם מצא מישהו שמתאים לי ממש, לא מה שכולם חשבו שאני צריך.", 
            "יונתן ר.", "ירושלים");
            
        VerticalLayout card3 = buildReviewCard(
            "תוך שלושה שבועות קיבלתי הצעה שלא האמנתי שאפשר למצוא. הציון היה 94 — ואחרי חצי שנה אנחנו עדיין ביחד.", 
            "מיכל כ.", "תל אביב");

        // הכנסת הכרטיסיות לשורה
        HorizontalLayout cardsRow = new HorizontalLayout(card1, card2, card3);
        cardsRow.setWidthFull();
        cardsRow.setJustifyContentMode(FlexComponent.JustifyContentMode.EVENLY);
        cardsRow.getStyle().set("flex-wrap", "wrap"); // שובר שורה במסכים קטנים
        cardsRow.getStyle().set("gap", "20px");

        sectionLayout.add(sectionTitle, cardsRow);
        return sectionLayout;
    }

    // פונקציית עזר לבניית כרטיסיית ביקורת בודדת
    private VerticalLayout buildReviewCard(String reviewText, String authorName, String location) {
        VerticalLayout card = new VerticalLayout();
        card.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        
        // עיצוב הקופסה הלבנה המרחפת
        card.getStyle().set("background-color", "white");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("padding", "30px");
        card.getStyle().set("box-shadow", "0 4px 15px rgba(0,0,0,0.05)"); // הצללית העדינה
        card.setWidth("30%");
        card.setMinWidth("250px"); // מונע מהכרטיסייה להיות צרה מדי

        // כוכבים
        Span stars = new Span("⭐⭐⭐⭐⭐");
        stars.getStyle().set("font-size", "14px");
        stars.getStyle().set("margin-bottom", "10px");

        // טקסט הביקורת
        Paragraph text = new Paragraph(reviewText);
        text.getStyle().set("text-align", "center");
        text.getStyle().set("font-size", "14px");
        text.getStyle().set("color", "#4a4a4a");

        // אזור שם הכותב והמיקום
        Div authorInfo = new Div();
        authorInfo.getStyle().set("text-align", "center");
        authorInfo.getStyle().set("margin-top", "15px");
        
        Span name = new Span(authorName);
        name.getStyle().set("font-weight", "bold");
        name.getStyle().set("display", "block"); // יורד שורה מעל המיקום
        
        Span loc = new Span(location);
        loc.getStyle().set("color", "gray");
        loc.getStyle().set("font-size", "12px");
        
        authorInfo.add(name, loc);

        card.add(stars, text, authorInfo);
        return card;
    }

    // 1. פונקציית העזר (מייצרת פריט סטטיסטיקה בודד)
    private VerticalLayout buildStatItem(String number, String label) {
        VerticalLayout stat = new VerticalLayout();
        stat.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        stat.setSpacing(false); // מבטל רווח מיותר בין המספר לטקסט
        stat.setPadding(false);

        H2 numElement = new H2(number);
        numElement.getStyle().set("color", "white");
        numElement.getStyle().set("margin", "0");
        numElement.getStyle().set("font-size", "4rem"); // פונט ענק ובולט למספר

        Span labelElement = new Span(label);
        labelElement.getStyle().set("color", "#a0a0a0"); // אפור עדין לטקסט
        labelElement.getStyle().set("font-size", "1.2rem"); 

        stat.add(numElement, labelElement);
        return stat;
    }

    // 2. הפונקציה שבונה את כל השורה הכהה
    private HorizontalLayout createStatsSection() {
        HorizontalLayout statsRow = new HorizontalLayout();
        statsRow.setWidthFull();
        statsRow.getStyle().set("background-color", "#222222"); // שחור-אפור כהה
        statsRow.getStyle().set("padding", "80px 10%"); // שוליים עליונים ותחתונים למראה מרווח
        statsRow.setJustifyContentMode(FlexComponent.JustifyContentMode.EVENLY); // פיזור שווה של 3 הבלוקים לרוחב המסך
        statsRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER); // מרכוז אנכי

        // מייצרים את 3 הבלוקים בעזרת פונקציית העזר
        VerticalLayout stat1 = buildStatItem("+2,400", "משתמשים רשומים");
        VerticalLayout stat2 = buildStatItem("89%", "שביעות רצון");
        VerticalLayout stat3 = buildStatItem("+340", "זוגות שנוצרו");

        // מכניסים אותם לשורה
        statsRow.add(stat1, stat2, stat3);

        return statsRow;
    }

    // ---------------------------------------------------------
    // אזור 5: קריאה לפעולה (CTA) - הרצועה הסגולה
    // ---------------------------------------------------------
    private VerticalLayout createCtaSection() {
        VerticalLayout ctaLayout = new VerticalLayout();
        ctaLayout.setWidthFull();
        // ממרכז את הכותרת והכפתור
        ctaLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        
        // הגדרות עיצוב: צבעים, ריווחים וגובה
        ctaLayout.getStyle().set("background-color", "#7F77DD"); // הסגול המזוהה שלנו
        ctaLayout.getStyle().set("color", "white");
        ctaLayout.getStyle().set("padding", "80px 20px");
        ctaLayout.setMinHeight("300px"); // מבטיח עומק מינימלי לרצועה

        // רכיבי התוכן
        H2 ctaTitle = new H2("מוכן לפגוש את השידוך הבא שלך?");
        ctaTitle.getStyle().set("margin-top", "0");
        Paragraph ctaSub = new Paragraph("הצטרף לאלפי משתמשים שכבר מצאו את ההתאמה שלהם");
        
        // כפתור חלול (הטריק ששכללנו ב-Hero)
        Button finalBtn = new Button("התחל עכשיו — בחינם");
        finalBtn.getStyle().set("background-color", "transparent");
        finalBtn.getStyle().set("color", "white");
        finalBtn.getStyle().set("border", "1px solid white");
        finalBtn.getStyle().setBorderRadius("50px"); // צורת גלולה
        finalBtn.getStyle().set("margin-top", "20px");
        finalBtn.getStyle().set("padding", "10px 30px"); // כפתור רחב יותר

        ctaLayout.add(ctaTitle, ctaSub, finalBtn);
        return ctaLayout;
    }

    // ---------------------------------------------------------
    // אזור 6: פוטר (השחור הדינמי)
    // ---------------------------------------------------------
    private HorizontalLayout createFooterSection() {
        // העוטף הראשי (שורה שחורה לכל הרוחב)
        HorizontalLayout footerWrapper = new HorizontalLayout();
        footerWrapper.setWidthFull();
        footerWrapper.getStyle().set("background-color", "#111111"); // שחור פוטר
        footerWrapper.getStyle().set("color", "#a0a0a0"); // אפור עדין לטקסט
        footerWrapper.getStyle().set("padding", "40px 10%");
        
        // פיזור הרכיבים: [טקסט1] ... (מרכז: לינקים) ... [טקסט2]
        footerWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        footerWrapper.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        // תמיכה בפריסה אנכית במסכים קטנים
        footerWrapper.getStyle().set("flex-wrap", "wrap"); 
        footerWrapper.getStyle().set("gap", "20px");

        // 1. רכיב ראשון: טקסט קבוע (Copyright)
        Span copyRight = new Span("© 2026 O.K  כל הזכויות שמורות.");
        copyRight.getStyle().set("font-size", "14px");

        // 2. רכיב מרכזי: רשת הקישורים הדינמית (CSS Grid)
        // אנחנו מגדירים כאן את הקישורים כ-Strings פשוטים
        Div linksGrid = createLinksGrid(
            "דף הבית", "איך זה עובד?", "על האלגוריתם",
            "תנאי שימוש", "מדיניות פרטיות", "צור קשר",
            "בלוג כתבות", "שאלות נפוצות", "קריירה"
        );

        // 3. רכיב אחרון: טקסט קבוע או לוגו (למשל מודל פרימיום)
        Span poweredBy = new Span("You'r Next Shidech");
        poweredBy.getStyle().set("font-size", "25px");
        poweredBy.getStyle().set("font-style", "bold");

        // מחברים את הרכיבים לשורה
        footerWrapper.add(copyRight, linksGrid, poweredBy);
        return footerWrapper;
    }

    // פונקציית עזר: יצירת רשת קישורים (3 בשורה) בעזרת CSS Grid
    private Div createLinksGrid(String... linkNames) {
        Div grid = new Div();
        // הגדרת CSS Grid ישירות בקוד: 3 עמודות שוות שמתאימות את עצמן אוטומטית
        grid.getStyle().set("display", "grid");
        grid.getStyle().set("grid-template-columns", "repeat(3, 1fr)"); 
        grid.getStyle().set("gap", "15px 40px"); // רווח: [שורות] [עמודות]
        grid.getStyle().set("text-align", "center");
        grid.getStyle().set("width", "40%"); // הרשת תתפוס 40% מרוחב הפוטר
        grid.getStyle().set("min-width", "300px"); // מונע מחיצה במסך צר

        // הלולאה הדינמית - עוברת על כל מחרוזת במערך
        for (String name : linkNames) {
            // מייצרים Anchor (קישור HTML תקני) במקום Span
            Anchor link = new Anchor("#", name); // כרגע "#" הוא קישור זמני
            // עיצוב הקישור (אפור, ללא קו תחתון, משתנה במעבר עכבר)
            link.getStyle().set("color", "#a0a0a0");
            link.getStyle().set("text-decoration", "none");
            link.getStyle().set("font-size", "14px");
            
            // אפשרות להוסיף Hover Effect פשוט (עובד בדפדפנים מודרניים)
            link.getElement().getStyle().set("transition", "color 0.2s");

            grid.add(link);
        }
        
        return grid;
    }


}