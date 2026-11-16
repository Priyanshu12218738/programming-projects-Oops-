import java.util.ArrayList;
import java.util.Collections;
import java.util.*;



class Employee {


    private int employeeId;
    private String name;
    private String email;
    private String department;

    public Employee(int employeeId, String name, String email, String department){
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.department = department;
    }

    public int getEmployeeId(){
        return this.employeeId;
    }

    public void setEmployeeId(int employeeId) {
        if(employeeId>0) {
            this.employeeId = employeeId;
        }

    }

    public String getName(){
        return this.name;
    }
    public String getEmail(){
        return this.email;
    }
    public String getDepartment(){
        return  this.department;
    }

    public void setDepartment(String department) {
         this.department = department;
    }
}

//Lesson 2: EmployeeService
class EmployeeService{

    private ArrayList<Employee> employees;

    public EmployeeService(){
        this.employees = new ArrayList<>();
    }

    public void addEmployee(Employee employee) {

        if (employee == null) {
            System.out.println("Invalid employee!");
            return;
        }

        if (searchEmployee(employee.getEmployeeId()) != null) {
            System.out.println("Employee ID already exists!");
            return;
        }

        employees.add(employee);
    }

    public List<Employee> getEmployeesDetails() {
        return Collections.unmodifiableList(employees);
    }

    // Loop aur getters ka use karke details print karne ka method
    public void displayEmployees() {
        for (Employee emp : this.employees) {
            System.out.println("ID: " + emp.getEmployeeId() +
                    ", Name: " + emp.getName() +
                    ", Dept: " + emp.getDepartment());
        }
    }

    public Employee searchEmployee(int searchId) {
        // List mein loop chalayein
        for (Employee emp : this.employees) {
            // Har employee ki ID check karein
            if (emp.getEmployeeId() == searchId) {
                // Match milne par employee return karein
                return emp;
            }
        }
        // Match nahi mila toh null return karein
        return null;
    }

    public boolean updateDepartment(int employeeId, String newDepartment) {

        Employee emp = searchEmployee(employeeId);

        if (emp == null) {
            System.out.println("Employee not found!");
            return false;
        }

        emp.setDepartment(newDepartment);

        System.out.println("Department updated successfully!");
        return true;
    }

    public boolean deleteEmployee(int employeeId) {

        Employee emp = searchEmployee(employeeId);

        if (emp == null) {
            System.out.println("Employee not found!");
            return false;
        }

        employees.remove(emp)  ;

        System.out.println(" Employee deleted successfully!");
        return true;
    }

}


public  void main() {

    EmployeeService service = new EmployeeService();

// Create employees
    Employee e1 = new Employee(101, "Rahul", "rahul@gmail.com", "IT");
    Employee e2 = new Employee(102, "Priya", "priya@gmail.com", "HR");

// Add employees
    service.addEmployee(e1);
    service.addEmployee(e2);

// Display employees
    service.displayEmployees();

// Search employee
    Employee result = service.searchEmployee(101);

// Update department
    service.updateDepartment(101, "Finance");

// Delete employee
    service.deleteEmployee(102);

// Display final list
    service.displayEmployees();

}

