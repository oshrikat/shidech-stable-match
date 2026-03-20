package oshrik.shidech_stable_match.ui.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;

public class MatchCard extends Div {

    public MatchCard(String name1, String name2, double score) {
        addClassName("match-card");

        // חישוב SVG
        double circumference = 188.5;
        double offset = circumference * (1 - score / 100.0);
        String color = scoreColor(score);

        // טבעת SVG
        String svg = """
            <svg viewBox="0 0 72 72" width="72" height="72"
                 style="transform:rotate(-90deg)">
              <circle cx="36" cy="36" r="30" fill="none"
                      stroke="#e0e0e0" stroke-width="5"/>
              <circle cx="36" cy="36" r="30" fill="none"
                      stroke="%s" stroke-width="5"
                      stroke-linecap="round"
                      stroke-dasharray="188.5"
                      stroke-dashoffset="%.1f"/>
            </svg>
            """.formatted(color, offset);

        Div ring = new Div();
        ring.addClassName("score-ring");
        ring.getElement().setProperty("innerHTML", svg);

        Span scoreNum = new Span(String.format("%.1f", score));
        scoreNum.getStyle().set("color", color);
        scoreNum.addClassName("score-num");

        Span scoreSub = new Span("התאמה");
        scoreSub.addClassName("score-sub");

        Div scoreLabel = new Div(scoreNum, scoreSub);
        scoreLabel.addClassName("score-label");

        Div scoreWrap = new Div(ring, scoreLabel);
        scoreWrap.addClassName("score-wrap");

        Hr divider = new Hr();

        Span n1 = new Span(name1);
        Span sep = new Span("×");
        Span n2 = new Span(name2);
        Div names = new Div(n1, sep, n2);
        names.addClassName("names");

        add(scoreWrap, divider, names);
    }

    private String scoreColor(double score) {
        if (score >= 90) return "#7F77DD";
        if (score >= 75) return "#1D9E75";
        if (score >= 50) return "#888780";
        return "#E24B4A";
    }
}