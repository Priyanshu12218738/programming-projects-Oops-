import Employee_Mng_Sys.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    @Test
    void shouldRejectDuplicateEmployeeId() {

        EmployeeService service = new EmployeeService();

        Employee emp1 = new Employee(
                101, "Rahul", "rahul@gmail.com", "IT"
        );

        Employee emp2 = new Employee(
                101, "Priya", "priya@gmail.com", "HR"
        );

        service.addEmployee(emp1);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.addEmployee(emp2)
        );
    }
}
