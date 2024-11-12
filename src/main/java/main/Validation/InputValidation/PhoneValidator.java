package main.Validation.InputValidation;

public class PhoneValidator {
    public static boolean isValid(String phone) {
        String regex = "\\+375(29|44|33)[0-9]{7}";
        return phone.matches(regex);
    }
}
