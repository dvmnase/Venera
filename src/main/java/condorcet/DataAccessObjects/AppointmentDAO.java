package condorcet.DataAccessObjects;



import condorcet.Models.Entities.Appointment;
import condorcet.Models.Entities.Payment;
import condorcet.Models.Entities.Procedure;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<Appointment> getAppointmentsByClientId(int clientId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT id, client_id, employee_id, procedure_id, appointment_date, notes FROM appointments WHERE client_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = new Appointment();
                    appointment.setId(rs.getInt("id"));
                    appointment.setClientId(rs.getInt("client_id"));
                    appointment.setEmployeeId(rs.getInt("employee_id"));
                    appointment.setProcedureId(rs.getInt("procedure_id"));

                    // Конвертация Timestamp в LocalDateTime
                    Timestamp timestamp = rs.getTimestamp("appointment_date");
                    if (timestamp != null) {
                        appointment.setAppointmentDate(timestamp.toLocalDateTime());
                    } else {
                        // Логирование или обработка случая, когда дата отсутствует
                        System.out.println("Appointment ID " + appointment.getId() + " has no date.");
                    }

                    // Устанавливаем примечания (если они есть)
                   // appointment.setNotes(rs.getString("notes")); // Убедитесь, что это не null
                    appointments.add(appointment);
                }
            }
        }
        return appointments;
    }

    public List<Appointment> getAppointmentsByEmployeeId(int employeeId) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        String query = "SELECT id, client_id, employee_id, procedure_id, appointment_date, notes FROM appointments WHERE employee_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = new Appointment();
                    appointment.setId(rs.getInt("id"));
                    appointment.setClientId(rs.getInt("client_id"));
                    appointment.setEmployeeId(rs.getInt("employee_id"));
                    appointment.setProcedureId(rs.getInt("procedure_id"));

                    // Конвертация Timestamp в LocalDateTime
                    Timestamp timestamp = rs.getTimestamp("appointment_date");
                    if (timestamp != null) {
                        appointment.setAppointmentDate(timestamp.toLocalDateTime());
                    } else {
                        // Логирование или обработка случая, когда дата отсутствует
                        System.out.println("Appointment ID " + appointment.getId() + " has no date.");
                    }

                    // Устанавливаем примечания (если они есть)
                    // appointment.setNotes(rs.getString("notes")); // Убедитесь, что это не null
                    appointments.add(appointment);
                }
            }
        }
        return appointments;
    }


    public void deleteAppointmentById(int appointmentId) throws SQLException {
        String query = "DELETE FROM appointments WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, appointmentId);
            stmt.executeUpdate();
            System.out.println("Запись с ID " + appointmentId + " удалена.");
        }
    }
    public void updateAppointmentDate(int appointmentId, LocalDateTime newDateTime) throws SQLException {
        String query = "UPDATE appointments SET appointment_date = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setTimestamp(1, Timestamp.valueOf(newDateTime));
            stmt.setInt(2, appointmentId);
            stmt.executeUpdate();
        }
    }

    public Map<String, Double> getVisitStatistics() throws SQLException {
        Map<String, Double> dateSums = new HashMap<>();
        String query = "SELECT DATE(a.appointment_date) AS appointment_date, SUM(p.amount) AS total_amount " +
                "FROM appointments a " +
                "JOIN payments p ON a.id = p.appointment_id " +
                "GROUP BY DATE(a.appointment_date)";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String dateKey = rs.getString("appointment_date"); // Получаем дату в формате YYYY-MM-DD
                double totalAmount = rs.getDouble("total_amount"); // Сумма по дате

                dateSums.put(dateKey, totalAmount); // Добавляем в мапу
            }
        }

        return dateSums;
    }



    private Payment getPaymentByAppointmentId(int appointmentId) throws SQLException {
        String query = "SELECT * FROM payments WHERE appointment_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, appointmentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Payment payment = new Payment();
                    payment.setAmount(resultSet.getFloat("amount"));
                    payment.setPaymentMethod(resultSet.getString("payment_method"));
                    payment.setAppointment_id(resultSet.getInt("appointment_id"));
                    return payment;
                }
            }
        }
        return null; // Если запись о платеже не найдена
    }
}
