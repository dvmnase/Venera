package condorcet.DataAccessObjects;



import condorcet.Models.Entities.Appointment;
import condorcet.Models.Entities.Payment;
import condorcet.Models.Entities.Procedure;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class PaymentDAO {
    private final Connection connection;

    public PaymentDAO(Connection connection) {
        this.connection = connection;
    }

    public void savePayment(Payment payment) throws SQLException {
        // Убедитесь, что все параметры корректны
        if (payment.getAmount() == null || payment.getPaymentMethod() == null || payment.getAppointment_id() <= 0) {
            throw new SQLException("Некорректные данные для платежа.");
        }

        String insertPaymentQuery = "INSERT INTO payments (amount, payment_method, appointment_id) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertPaymentQuery)) {
            statement.setFloat(1, payment.getAmount());
            statement.setString(2, payment.getPaymentMethod());
            statement.setInt(3, payment.getAppointment_id());
            statement.executeUpdate();
        }
    }

    public Payment getPaymentByAppointmentId(int appointmentId) throws SQLException {
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

    public void deletePaymentByAppointmentId(int appointmentId) throws SQLException {
        String query = "DELETE FROM payments WHERE appointment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, appointmentId);
            stmt.executeUpdate();
            System.out.println("Платеж с appointment_id " + appointmentId + " удален.");
        }
    }


}
