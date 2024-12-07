package condorcet.DataAccessObjects;

import condorcet.Models.Entities.Client;
import condorcet.Models.Entities.Employee;
import condorcet.Models.Entities.Procedure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDAO {
    private Connection connection;

    public EmployeeDAO(Connection connection) {
        this.connection = connection;
    }

    public void saveEmployee(Employee employee, int userId) throws SQLException {
        String sex = employee.getSex();
        if (!sex.equals("MALE") && !sex.equals("FEMALE")) {
            throw new IllegalArgumentException("Invalid sex value: " + sex);
        }

        String query = "INSERT INTO employees (name, surname, specialization, sex, phone, shared_data, user_id) VALUES (?, ?, ?, ?, ?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, employee.getName());
            stmt.setString(2, employee.getSurname());
            stmt.setString(3, employee.getSpecialization());
            stmt.setString(4, sex);  // Ensure this is either "MALE" or "FEMALE"
            stmt.setString(5, employee.getPhone());
            stmt.setString(6, employee.getShared_data());
            stmt.setInt(7, userId);  // Добавляем user_id
            stmt.executeUpdate();
        }
    }

    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        String query = "SELECT id,name, surname, specialization, sex, phone, shared_data FROM employees";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setName(rs.getString("name"));
                employee.setSurname(rs.getString("surname"));
                employee.setSpecialization(rs.getString("specialization"));
                employee.setSex(rs.getString("sex"));
                employee.setPhone(rs.getString("phone"));
                employee.setShared_data(rs.getString("shared_data"));
                employees.add(employee);
            }
        }
        return employees;
    }
public Employee getEmployeeById(int employeeId) throws SQLException{
        Employee employee =null;
        String query ="SELECT id, name, surname, specialization, phone,sex FROM employees WHERE id=?";
    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setInt(1, employeeId);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                employee = new Employee();
                employee.setId(rs.getInt("id"));
                employee.setName(rs.getString("name"));
                employee.setSurname(rs.getString("surname"));
                employee.setSpecialization(rs.getString("specialization"));
                employee.setPhone(rs.getString("phone"));
                employee.setSex(rs.getString("sex"));
            }
        }
    }
    return employee;
}

    public Employee getEmployeeByUserId(int employeeId) throws SQLException{
        Employee employee =null;
        String query ="SELECT id, name, surname, specialization, phone,sex FROM employees WHERE user_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, employeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    employee = new Employee();
                    employee.setId(rs.getInt("id"));
                    employee.setName(rs.getString("name"));
                    employee.setSurname(rs.getString("surname"));
                    employee.setSpecialization(rs.getString("specialization"));
                    employee.setPhone(rs.getString("phone"));
                    employee.setSex(rs.getString("sex"));
                }
            }
        }
        return employee;
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


    public List<Employee> getEmployeesByProcedureId(int procedureId) throws SQLException {
        String sql = "SELECT e.id, e.name, e.surname, e.specialization, e.shared_data, e.phone, e.sex " +
                "FROM employees e " +
                "JOIN employee_procedures ep ON e.id = ep.employee_id " +
                "WHERE ep.procedure_id = ?";
        List<Employee> employees = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, procedureId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Employee employee = new Employee();
                    employee.setId(rs.getInt("id"));
                    employee.setName(rs.getString("name"));
                    employee.setSurname(rs.getString("surname"));
                    employee.setSpecialization(rs.getString("specialization"));
                    employee.setShared_data(rs.getString("shared_data"));
                    employee.setPhone(rs.getString("phone"));
                    employee.setSex(rs.getString("sex"));

                    employees.add(employee);
                }
            }
        }

        return employees;
    }

    public List<Map<String, Object>> getEmployeeStatistics() throws SQLException {
        // SQL для подсчета общего количества записей
        String totalAppointmentsQuery = "SELECT COUNT(*) AS total_count FROM appointments";
        int totalAppointments = 0;

        // Получаем общее количество записей
        try (PreparedStatement stmt = connection.prepareStatement(totalAppointmentsQuery);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalAppointments = rs.getInt("total_count");
            }
        }

        if (totalAppointments == 0) {
            throw new SQLException("No appointments found in the database.");
        }

        // SQL для подсчета записей по каждому employee_id
        String employeeStatsQuery = "SELECT employee_id, COUNT(*) AS appointment_count " +
                "FROM appointments " +
                "GROUP BY employee_id";
        List<Map<String, Object>> employeeStatistics = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(employeeStatsQuery);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int employeeId = rs.getInt("employee_id");
                int appointmentCount = rs.getInt("appointment_count");

                // Вычисляем процент
                double percentage = (appointmentCount / (double) totalAppointments) * 100;

                // Получаем данные сотрудника
                Employee employee = getEmployeeById(employeeId);
                if (employee != null) {
                    Map<String, Object> employeeData = new HashMap<>();
                    employeeData.put("employee", employee);
                    employeeData.put("percentage", percentage);
                    employeeStatistics.add(employeeData);
                }
            }
        }

        return employeeStatistics;
    }

}
