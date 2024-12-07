package condorcet.DataAccessObjects;

import condorcet.Models.Entities.Client;
import condorcet.Models.Entities.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public Client getClientById(int clientId) throws SQLException{
        Client client =null;
        String query ="SELECT id, name, surname, phone,sex FROM clients WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    client = new Client();
                    client.setId(rs.getInt("id"));
                    client.setName(rs.getString("name"));
                    client.setSurname(rs.getString("surname"));
                    client.setPhone(rs.getString("phone"));
                    client.setSex(rs.getString("sex"));
                }
            }
        }
        return client;
    }
}
