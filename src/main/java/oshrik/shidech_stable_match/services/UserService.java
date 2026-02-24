package oshrik.shidech_stable_match.services;

import org.springframework.stereotype.Service;

import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.repositories.UserRepository;

@Service
public class UserService 
{

    private UserRepository userRepository ;

    /**
     * 
     * @param userRepository - Dependency Injection : הזרקת תלות בשירות של משתמש 
     */
    public UserService(UserRepository userRepository)
    {
        this.userRepository = userRepository;





    }

    // פעולות 


    public void insertUser(User user) throws Exception
    {
        // בדיקת ואלידציה - בדיקת האם תקין 
        // האם המשתמש כבר קיים ?

        if(!(userRepository.existsById(user.getUserName())))
            userRepository.insert(user);
        else
        {
            throw new Exception("User Already Exists... ");
        }

    }




}
