package condorcet.DataAccessObjects;

import condorcet.Models.Entities.Procedure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class employee_proceduresDAO {
    private Connection connection;

    // Конструктор для инициализации соединения
    public employee_proceduresDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Procedure> getProceduresByEmployeeId(int employeeId) throws SQLException {
        List<Procedure> procedures = new ArrayList<>();
        String query = "SELECT p.* FROM procedures p " +
                "JOIN employee_procedures ep ON p.id = ep.procedure_id " +
                "WHERE ep.employee_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
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
}