package oshrik.shidech_stable_match.services;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.repositories.UserRepository;

@Service
public class UserService 
{

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // C (Create)
    public boolean addUserToDB(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return false;
        }
        userRepository.insert(user);
        return true;
    }

    // החזרת ArrayList כמו שביקשת (בצורה בטוחה)
    // R (Read)
    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(userRepository.findAll());
    }

    // returns the names that like the parameter name
    public ArrayList<User> getAllUsersLikeName(String name) {
        return new ArrayList<>(userRepository.findByUsernameLike(name));
    }


    // U (Update)
    public void updateUserPassword(String username, String newPassword) {
        User user = userRepository.findById(username).orElse(null);
        if (user != null) {
            user.setPassword(newPassword);
            userRepository.save(user);
        }
    }

    public void updateUser(User u) {
        User user = userRepository.findById(u.getUsername()).orElse(null);
        if (user != null) {
            user.setPassword(u.getPassword());
            user.setUsername(u.getUsername());

            // שמירת השינויים
            userRepository.save(user);
        }
    }

    public ArrayList<User> findByUsernameLike(User user) {
        return (ArrayList<User>) userRepository.findByUsernameLike(user.getUsername());

    }

    // isUserExist(User) / (username,pass)
    public boolean isUserExist(User user)// String name, String password
    {
        return userRepository.findOneByUsernameAndPassword(user.getUsername(), user.getPassword()) != null;
    }

    public User getUserByNameAndPassword(String us, String pw) {
        return userRepository.findOneByUsernameAndPassword(us, pw);
    }


    // D (delete)
    // create a delete function
    public void deleteUser(User user) {
        // אין צורך לבדוק אם המשתמש קיים - כי בעצם לחצנו עליו
        userRepository.delete(user);

    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

}