package oshrik.shidech_stable_match.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.DataProvider;

import oshrik.shidech_stable_match.datamodels.Match;
import oshrik.shidech_stable_match.datamodels.User;

@Service
public class MatchAlgoFacadeService 
{
     
    private final MatchScoreService matchScoreService;
    private final MatchService matchService;
    private final MatchmakingService matchmakingService;
    private final UserService userService; 

    public MatchAlgoFacadeService(MatchService matchService, MatchmakingService matchmakingService,
            UserService userService, MatchScoreService matchScoreService)
    {
        this.matchService = matchService;
        this.matchmakingService = matchmakingService;
        this.userService = userService;
        this.matchScoreService = matchScoreService;

    }


    public void generateAndSaveUsers_Safe(int i) {
         // 1.  נמחק מה שקיים
        
        //  userService.deleteAllUsers_NO_ADMIN();

        // 2. מייצרים 10 גברים ו-10 נשים (סה"כ 20)
        // dateGenerateService....(i);

        
    }


    public List<User> findAll() 
    {   
        return userService.findAll();
    }


    public void prepareAndFillPreferences() {
       matchmakingService.prepareAndFillPreferences();

    }


    public List<User> getCurrentMen() {
        return matchmakingService.getCurrentMen();
    }

       public List<User> getCurrentWomen() {
        return matchmakingService.getCurrentWomen();
    }

    public List<Match> runFullMatchmaking() {
    // הקבלן לוקח את הרשימות וקורא לשירות השידוכים
    return matchService.runAlgo_performFullMatchmaking(
            matchmakingService.getCurrentMen(),
            matchmakingService.getCurrentWomen()
    );
}


    public List<Match> findAllMatches() 
    {

        return matchService.findAllMatches();
    
    }


    public void deleteALL_Matches() {
        matchService.deleteAll();
    }

    public void deleteALL_MatchScores() {
        matchScoreService.cleanScoreMatchTable();
    }

    public void freeUsersFromPartner() {
        userService.resetAllRegularUsers();

    }

    public List<User> getAllUsersLikeName(String searchTerm) {
        return userService.getAllUsersLikeName(searchTerm);
    }

    public void deleteUser(User cu) {
        userService.deleteUser(cu);
    }

    public void deleteAllUsers_NO_ADMIN() {
        userService.deleteAllUsers_NO_ADMIN();
    }

    public boolean saveAdminUser(User newAdminUser) {
        return userService.saveAdminUser(newAdminUser);
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


}
