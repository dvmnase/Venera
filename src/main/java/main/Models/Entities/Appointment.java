package main.Models.Entities;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Appointment {
    private int id;
    private int clientId;
    private int employeeId;
    private int procedureId;

    @SerializedName("appointment_date")
    private LocalDateTime appointmentDate;
    private String notes;
    private Procedure procedure; // Добавлено
    private Employee employee;   // Добавлено

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(int procedureId) {
        this.procedureId = procedureId;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Procedure getProcedure() { // Добавлено
        return procedure;
    }

    public void setProcedure(Procedure procedure) { // Добавлено
        this.procedure = procedure;
    }

    public Employee getEmployee() { // Добавлено
        return employee;
    }

    public void setEmployee(Employee employee) { // Добавлено
        this.employee = employee;
    }
}
