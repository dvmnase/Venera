package condorcet.DataAccessObjects;



import condorcet.Models.Entities.Appointment;
import condorcet.Models.Entities.Procedure;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    private final Connection connection;

    public AppointmentDAO(Connection connection) {
        this.connection = connection;
    }


    public int saveAppointment(Appointment appointment) throws SQLException {
        String query = "INSERT INTO appointments (client_id, employee_id, procedure_id, appointment_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, appointment.getClientId());  // Вставляем client_id
            stmt.setInt(2, appointment.getEmployeeId());
            stmt.setInt(3, appointment.getProcedureId());
            stmt.setTimestamp(4, Timestamp.valueOf(appointment.getAppointmentDate()));

            // Выполняем запрос на вставку
            stmt.executeUpdate();

            // Получаем сгенерированный ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);  // Возвращаем сгенерированный ID
                } else {
                    throw new SQLException("Failed to obtain appointment ID");
                }
            }
        }
    }



}
