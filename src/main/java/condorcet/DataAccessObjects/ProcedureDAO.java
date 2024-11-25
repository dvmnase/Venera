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
        String query = "SELECT id, title, description, duration, price, service_type FROM procedures";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Procedure procedure = new Procedure();
                procedure.setId(rs.getInt("id"));
                procedure.setTitle(rs.getString("title"));
                procedure.setDescription(rs.getString("description"));
                procedure.setDuration(rs.getInt("duration"));
                procedure.setPrice(rs.getFloat("price"));
                procedure.setService_type(rs.getString("service_type"));
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

    public void updateProcedureDescription(int procedureId, String newDescription) throws SQLException {
        String query = "UPDATE procedures SET description = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newDescription);
            stmt.setInt(2, procedureId);
            stmt.executeUpdate();
        }
    }

    public void updateProcedurePrice(int procedureId, float newPrice) throws SQLException {
        String query = "UPDATE procedures SET price = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setFloat(1, newPrice);
            stmt.setInt(2, procedureId);
            stmt.executeUpdate();
        }
    }

    public void updateProcedureDuration(int procedureId, int newDuration) throws SQLException {
        String query = "UPDATE procedures SET duration = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, newDuration);
            stmt.setInt(2, procedureId);
            stmt.executeUpdate();
        }
    }

    public List<Procedure> getUnassignedProcedures(int employeeId) throws SQLException {
        List<Procedure> procedures = new ArrayList<>();
        String query = """
        SELECT p.id, p.title, p.description, p.duration, p.price, p.service_type
        FROM procedures p
        WHERE NOT EXISTS (
            SELECT 1
            FROM employee_procedures ep
            WHERE ep.procedure_id = p.id AND ep.employee_id = ?
        )
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Procedure procedure = new Procedure();
                    procedure.setId(rs.getInt("id"));
                    procedure.setTitle(rs.getString("title"));
                    procedure.setDescription(rs.getString("description"));
                    procedure.setDuration(rs.getInt("duration"));
                    procedure.setPrice(rs.getFloat("price"));
                    procedure.setService_type(rs.getString("service_type"));
                    procedures.add(procedure);
                }
            }
        }
        return procedures;
    }

}





