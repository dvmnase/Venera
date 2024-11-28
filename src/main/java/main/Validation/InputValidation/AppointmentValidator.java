package main.Validation.InputValidation;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AppointmentValidator {

    // Проверка, что дата записи не в прошлом и время между 10:00 и 19:00
    public static boolean isValidDateTime(String dateTimeInput) {
        if (dateTimeInput == null || dateTimeInput.isBlank()) {
            return false;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime selectedDate = LocalDateTime.parse(dateTimeInput, formatter);
            LocalDateTime now = LocalDateTime.now();

            // Проверка, что дата не в прошлом
            if (selectedDate.isBefore(now)) {
                return false;
            }

            // Проверка, что время записи с 10:00 до 19:00
            LocalTime startTime = LocalTime.of(10, 0); // 10:00
            LocalTime endTime = LocalTime.of(19, 0);  // 19:00

            if (selectedDate.toLocalTime().isBefore(startTime) || selectedDate.toLocalTime().isAfter(endTime)) {
                return false;
            }

            return true;

        } catch (Exception e) {
            // Если формат неверный или другая ошибка, возвращаем false
            return false;
        }
    }
}
