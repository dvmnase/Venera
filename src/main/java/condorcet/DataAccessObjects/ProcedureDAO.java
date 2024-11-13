package condorcet.DataAccessObjects;

import condorcet.Models.Entities.Procedure;
import condorcet.Models.Entities.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProcedureDAO {
    private Connection connection;


    public ProcedureDAO(Connection connection) {
        this.connection = connection;
    }

    // Метод для сохранения пользователя в базе данных и получения его ID
    public void saveProcedure(Procedure procedure) throws SQLException {
        String query = "INSERT INTO procedures (title, description , duration ,price) VALUES (?, ?, ?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, procedure.getTitle());
            stmt.setString(2, procedure.getDescription());
            stmt.setInt(3, procedure.getDuration());
            stmt.setFloat(4, procedure.getPrice());
            stmt.executeUpdate();


        }
    }

    public List<Procedure> getAllProcedures() throws SQLException {
        List<Procedure> procedures = new ArrayList<>();
        String query = "SELECT id, title, description, duration, price FROM procedures";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Procedure procedure = new Procedure();
                procedure.setId(rs.getInt("id"));
                procedure.setTitle(rs.getString("title"));
                procedure.setDescription(rs.getString("description"));
                procedure.setDuration(rs.getInt("duration"));
                procedure.setPrice(rs.getFloat("price"));
                procedures.add(procedure);
            }
        }
        return procedures;
    }

    public void deleteProcedure(int procedureId) throws SQLException {
        String query = "DELETE FROM procedures WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, procedureId);
            stmt.executeUpdate();
        }
    }
}





