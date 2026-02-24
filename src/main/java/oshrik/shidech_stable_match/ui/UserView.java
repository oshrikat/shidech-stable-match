package oshrik.shidech_stable_match.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.services.UserService;

@Route("/")
public class UserView extends VerticalLayout
{
    private UserService userService;
    private Button btnInsert;
    private TextField userNameTextField;
    private TextField userPassWordField;

    public UserView(UserService userService)
    {
        this.userService = userService;


        add(new H1("User Name View !`"));
        HorizontalLayout horizontalLayout = new HorizontalLayout(Alignment.BASELINE);
        horizontalLayout.add(userNameTextField = new TextField("User Name  : ____-"));
        horizontalLayout.add(userPassWordField = new TextField("User Password  : ____-"));
        horizontalLayout.add(btnInsert = new Button("Insert User To DB"));
        btnInsert.addClickListener(clickEvent -> insertUserToDB());


        add(horizontalLayout);

        
    }


    private void insertUserToDB()
    {
        String userName = userNameTextField.getValue();
        String userPassword = userPassWordField.getValue();


        // validation
        // ....
        if(userName.isBlank() || userPassword.isBlank())
            {  
            Notification.show("User Name and Password Cant Be Empty",3000,Position.MIDDLE);
                return;
            }


        try
        {
            userService.insertUser(new User(userName,userPassword));
            Notification.show("User Inserted Seccessfully !",3000,Position.MIDDLE);

        }
        catch(Exception e)
        {
            e.printStackTrace();

            // update user by notification for this error
            Notification.show("User Not inserted ! " + e.getMessage(),5000,Position.MIDDLE);
        }




    }



}
