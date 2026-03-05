package oshrik.shidech_stable_match.utilities;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteConfiguration;

public class RouteHelper 
{
    public static <T extends Component> void navigateTo(Class<T> page)
    {
        // UI.getCurrent().access(() -> UI.getCurrent().navigate(page));

        String path = RouteConfiguration.forSessionScope().getUrl(page);
        if(path.isEmpty())
            path = "/";
            
        UI.getCurrent().getPage().setLocation(path);

    }    

    public static void navigateTo (String pageRoute){
        UI.getCurrent().getPage().setLocation(pageRoute);
    }

}