package condorcet.Validation;

import condorcet.DataAccessObjects.UserDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserValidation {
    private Connection connection;

    public UserValidation(Connection connection) {
        this.connection = connection;
    }

    // Проверка, существует ли логин
    public boolean isLoginExists(String login) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE login = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    public boolean isPhoneExists(String phone) throws SQLException {
        String query = "SELECT COUNT(*) FROM clients WHERE phone = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
}
