package oshrik.shidech_stable_match.datamodels;

import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @author Oshri Kataribas
 * @date 2026-02-24
*/


/**
 * הגדרת משתמש - בסיסי בינתיים
 */

@Document(collection = "Users")
public class User 
{
    private String userName;
    private String passWord;
    
    // .....



    // פכולות סט וגט
    
    public User(User user) 
    {
        this.userName = user.getUserName();
        this.passWord = user.getPassWord();
    }

    public User(String us, String pw) 
    {
        
        this.userName = us;
        this.passWord = pw;
    }

    public String getUserName() {
        return userName;
    }
  
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassWord() {
        return passWord;
    }
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
    

}
