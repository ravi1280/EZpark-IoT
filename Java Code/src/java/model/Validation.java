package model;

public class Validation {

    public static boolean isMobileNumberValid(String text) {
        return text.matches("^07[01245678]{1}[0-9]{7}$");
    }
    
  
}
