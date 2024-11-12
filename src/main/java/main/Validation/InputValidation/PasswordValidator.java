package main.Validation.InputValidation;

public class PasswordValidator {
    public static boolean isValid(String password, String confirmPassword) {
        if (password.length() < 6) {
            return false;
        }
        return password.equals(confirmPassword);
    }
}
