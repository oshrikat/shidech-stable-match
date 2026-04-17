package oshrik.shidech_stable_match.services;

import org.springframework.stereotype.Service;

import oshrik.shidech_stable_match.datamodels.User;
import oshrik.shidech_stable_match.utilities.Location;

@Service
public class AsyncManagerService {

    public static final TaskCallback TaskCallback = null;
    private Thread t;

    private static LocationApiService locationApiService;
    private static UserService userService;

    public AsyncManagerService(LocationApiService locationApiService, UserService userService) {
        this.locationApiService = locationApiService;
        this.userService = userService;
    }

    // ממשק שמטפל בתהליכי רקע - אסינכרונים
    public interface TaskCallback 
    {
        // דיווח כאשר תהליכוון מסתיים ברקע
        void onComplete(boolean isCompleted);
        
        // דיווח כאשר תהליכים רץ ברקע
        void onProgress(int percentage);
        
        // דיווח כאשר ישנה שגיאה בתהליכון שרץ ברקע
        void onError(String message);

    }

    /**
     * פונקציה גנרית להרצת משימות ארוכות ברקע עם דיווח התקדמות.
     * @param coreTask - הלוגיקה האמיתית שאנחנו רוצים להריץ (למשל, האלגוריתם).
     * @param callback - הצינור דרכו נדווח למסך על ההתקדמות.
     */
    public void executeWithProgress(Runnable coreTask, TaskCallback callback) {
        
        t = new Thread(() -> {
            boolean isOk = true;
            try {
                // 1. קודם כל, מריצים את משימת הליבה שהוזרקה פנימה
                if (coreTask != null) 
                {
                    coreTask.run();
                }

                // 2. סימולציית ההתקדמות למען חווית המשתמש (הלולאה שלך מ-1 עד 100)
                for (int i = 1; i <= 100; i++) {
                    callback.onProgress(i); // 
                    Thread.sleep(200);  
                }

            }
            catch (InterruptedException e) 
            {

                System.out.println("Task was interrupted: " + e.getMessage());
                isOk = false;
            }
            


            catch (Exception e) {
                System.out.println("System Error: " + e.getMessage());
                callback.onError("התרחשה שגיאה במהלך ביצוע משימת הרקע.");
                isOk = false;
            }

            // 3. הדיווח הסופי - סיימנו!
            callback.onComplete(isOk);
        });

        t.start(); // זינוק לדרך
    }

    // פונקציית עצירת החירום שלך
    public void stopTask() 
    {
        if (t != null && t.isAlive()) {
            t.interrupt();
        }
    }

    public void fetchAndSaveLocationBackground(User user, String city) {
        new Thread(() -> {
            try {
                Location loc = locationApiService.getRealLocationFromCityName(city);
                if (loc != null) {
                    user.setAddress(loc);
                    userService.updateFullUser(user);
                    System.out.println("Async Task: Successfully updated location for user!");
                }
            } catch (Exception e) {
                System.err.println("Async Task Failed: " + e.getMessage());
            }
        }).start(); // זינוק לדרך
    }
}
