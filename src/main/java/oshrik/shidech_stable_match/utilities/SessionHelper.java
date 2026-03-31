package oshrik.shidech_stable_match.utilities;

import com.vaadin.flow.server.VaadinSession;

import oshrik.shidech_stable_match.datamodels.User;


public class SessionHelper 
{
    
    public static String getSessionID()
    {
        return VaadinSession.getCurrent().getSession().getId();
    }

    public static Object getAttribute(String key)
    {
        return VaadinSession.getCurrent().getSession().getAttribute(key);
    }

    public static boolean isAttributeExist(String key)
    {
        Object object = VaadinSession.getCurrent().getSession().getAttribute(key);
        
        return object == null ? false : true;

    }

    // remove attribute
    public static void removeAttribute(String key)
    {
        VaadinSession.getCurrent().getSession().removeAttribute(key);
    }

    // invalidate - remove all.... cookies
    public static void invalidate()
    {
        VaadinSession.getCurrent().getSession().invalidate();
    }

    public static void setAttribute(String key, Object value) {
        VaadinSession.getCurrent().getSession().setAttribute(key,value);
    }

}
