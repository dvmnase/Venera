package condorcet.DataAccessObjects;

import condorcet.Models.Entities.Client;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClientDAO {
    private Connection connection;

    public ClientDAO(Connection connection) {
        this.connection = connection;
    }

    public void saveClient(Client client, int userId) throws SQLException {
        String sex = client.getSex();
        if (!sex.equals("MALE") && !sex.equals("FEMALE")) {
            throw new IllegalArgumentException("Invalid sex value: " + sex);
        }

        String query = "INSERT INTO clients (name, surname, sex, phone, user_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getSurname());
            stmt.setString(3, sex);  // Ensure this is either "MALE" or "FEMALE"
            stmt.setString(4, client.getPhone());
            stmt.setInt(5, userId);  // Добавляем user_id
            stmt.executeUpdate();
        }
    }
}
