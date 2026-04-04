package oshrik.shidech_stable_match.ui.components;

public class StatusCardData 
{

   private String title,subtitle,buttonText;

    public StatusCardData()
    {
        
    }
    
   public StatusCardData(String title, String subtitle, String buttonText) {
    this.title = title;
    this.subtitle = subtitle;
    this.buttonText = buttonText;
}

   public String getTitle() {
    return title;
   }

   public void setTitle(String title) {
    this.title = title;
   }

   public String getSubtitle() {
    return subtitle;
   }

   public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
   }

   public String getButtonText() {
    return buttonText;
   }

   public void setButtonText(String buttonText) {
    this.buttonText = buttonText;
   }

   



}
