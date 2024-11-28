package condorcet.DataAccessObjects;

import condorcet.Models.Entities.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    // Метод для сохранения пользователя в базе данных и получения его ID
    public int saveUser(User user) throws SQLException {
        String query = "INSERT INTO users (login, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.executeUpdate();

            // Получаем сгенерированный ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Возвращаем ID нового пользователя
                } else {
                    throw new SQLException("Failed to obtain user ID.");
                }
            }
        }
    }

    public boolean isValidLogin(String login, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE login = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, password);

            try (ResultSet resultSet = stmt.executeQuery()) {
                return resultSet.next(); // Если результат есть, значит, такой пользователь существует
            }
        }
    }

    public String getUserRole(String login) throws SQLException {
        String query = "SELECT role FROM users WHERE login = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("role");
                }
            }
        }
        return null; // Если роль не найдена
    }

    // Новый метод для получения ID пользователя по логину
    public int getUserId(String login) throws SQLException {
        String query = "SELECT id FROM users WHERE login = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id"); // Возвращаем ID пользователя
                }
            }
        }
        return -1; // Если ID не найден
    }
}
