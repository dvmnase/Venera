package condorcet.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private final String url = "jdbc:mysql://localhost:3306/venera";
    private final String user = "root";
    private final String password = "root";

    public void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement()) {

            // Таблица пользователей
            String createUserTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "login VARCHAR(50) NOT NULL, " +
                    "password VARCHAR(50) NOT NULL, " +
                    "role ENUM('CLIENT', 'EMPLOYEE', 'ADMIN') NOT NULL" +
                    ");";

            // Таблица клиентов
            String createClientTable = "CREATE TABLE IF NOT EXISTS clients (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "surname VARCHAR(50) NOT NULL, " +
                    "phone VARCHAR(15), " +
                    "sex ENUM('MALE', 'FEMALE'), " +
                    "user_id INT, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) " +
                    ");";

            // Таблица сотрудников
            String createEmployeeTable = "CREATE TABLE IF NOT EXISTS employees (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "surname VARCHAR(50) NOT NULL, " +
                    "specialization VARCHAR(50), " +
                    "shared_data VARCHAR(255), " +
                    "phone VARCHAR(15), " +
                    "sex ENUM('MALE', 'FEMALE'), " +
                    "user_id INT, " +
                    "FOREIGN KEY (user_id) REFERENCES users(id) " +
                    ");";

            // Таблица процедур с добавленным полем для типа услуги
            String createProcedureTable = "CREATE TABLE IF NOT EXISTS procedures (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(50) NOT NULL, " +
                    "description TEXT, " +
                    "duration INT NOT NULL, " +
                    "price DECIMAL(10, 2) NOT NULL, " +
                    "service_type ENUM('МАКИЯЖ', 'БРОВИ', 'УХОД ЗА ЛИЦОМ', 'ПЕДИКЮР', 'МАНИКЮР', 'СТРИЖКА', 'ОКРАШИВАНИЕ', 'УКЛАДКА', 'УХОД ЗА ВОЛОСАМИ') NOT NULL" +
                    ");";

            // Таблица записей
            String createAppointmentTable = "CREATE TABLE IF NOT EXISTS appointments (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "client_id INT, " +
                    "employee_id INT, " +
                    "procedure_id INT, " +
                    "appointment_date DATETIME, " +
                    "notes TEXT, " +
                    "FOREIGN KEY (client_id) REFERENCES clients(id), " +
                    "FOREIGN KEY (employee_id) REFERENCES employees(id), " +
                    "FOREIGN KEY (procedure_id) REFERENCES procedures(id)" +
                    ");";

            // Таблица платежей
            String createPaymentTable = "CREATE TABLE IF NOT EXISTS payments (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "amount DECIMAL(10, 2) NOT NULL, " +
                    "payment_method ENUM('CASH', 'CARD', 'ONLINE') NOT NULL, " +
                    "appointment_id INT, " +
                    "FOREIGN KEY (appointment_id) REFERENCES appointments(id)" +
                    ");";

            // Промежуточная таблица для связи многие ко многим между employees и procedures
            String createEmployeeProcedureTable = "CREATE TABLE IF NOT EXISTS employee_procedures (" +
                    "employee_id INT NOT NULL, " +
                    "procedure_id INT NOT NULL, " +
                    "PRIMARY KEY (employee_id, procedure_id), " +
                    "FOREIGN KEY (employee_id) REFERENCES employees(id), " +
                    "FOREIGN KEY (procedure_id) REFERENCES procedures(id)" +
                    ");";

            // Выполнение запросов на создание таблиц
            statement.execute(createUserTable);
            statement.execute(createClientTable);
            statement.execute(createEmployeeTable);
            statement.execute(createProcedureTable);
            statement.execute(createAppointmentTable);
            statement.execute(createPaymentTable);
            statement.execute(createEmployeeProcedureTable);

            System.out.println("Таблицы успешно созданы.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
