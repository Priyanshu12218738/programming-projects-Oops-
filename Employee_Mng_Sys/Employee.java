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

    public double calculateSalary(){
        return 0.0;
    }
}

//Phase 2: EmployeeService
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

//Phase 3 — Employee Types
//Lessons: calculateSalary,
public class PermanentEmployee extends Employee {

    private double monthlySalary;

    public PermanentEmployee(int employeeId, String name,
                             String email, String department, double monthlySalary) {

        super(employeeId, name, email, department);

        if (monthlySalary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        this.monthlySalary = monthlySalary;
    }

    @Override
    public double calculateSalary() {
            return monthlySalary;
    }
}

public class ContractEmployee extends Employee {


    private double workingHours;
    private double hourlyRate;
    public ContractEmployee(int employeeId, String name,
                             String email, String department, double workingHours, double hourlyRate) {

        super(employeeId, name, email, department);

        if (workingHours < 0 || hourlyRate < 0) {
            throw new IllegalArgumentException(
                    "Salary and hours cannot be negative"
            );
        }
        this.workingHours = workingHours;
        this.hourlyRate = hourlyRate;

    }

    @Override
    public double calculateSalary() {
        return hourlyRate*workingHours;
    }
}

public class InternEmployee extends Employee {

    private double monthlyStipend;
    public InternEmployee(int employeeId, String name,
                             String email, String department, double monthlyStipend ) {

        super(employeeId, name, email, department);

        if (monthlyStipend < 0) {
            throw new IllegalArgumentException(
                    "Stipend cannot be negative"
            );
        }
        this.monthlyStipend = monthlyStipend;
    }

    @Override
    public double calculateSalary() {
        return monthlyStipend;
    }
}



public  void main() {

// Testing: Phase 2
//  Lessongs: Polymorphism

//    EmployeeService service = new EmployeeService();
//
//// Create employees
//    Employee e1 = new Employee(101, "Rahul", "rahul@gmail.com", "IT");
//    Employee e2 = new Employee(102, "Priya", "priya@gmail.com", "HR");
//
//// Add employees
//    service.addEmployee(e1);
//    service.addEmployee(e2);
//
//// Display employees
//    service.displayEmployees();
//
//// Search employee
//    Employee result = service.searchEmployee(101);
//
//// Update department
//    service.updateDepartment(101, "Finance");
//
//// Delete employee
//    service.deleteEmployee(102);
//
//// Display final list
//    service.displayEmployees();

//Testing: phase 3
//    Lessons: Runtime Polymorphism(refrence-> same, object->different, method override)
//    Employee e1 = new PermanentEmployee(
//            101, "Rahul", "rahul@gmail.com", "IT", 30000
//    );
//
//    Employee e2 = new ContractEmployee(
//            102, "Priya", "priya@gmail.com", "HR", 160, 200
//    );
//
//    Employee e3 = new InternEmployee(
//            103, "Aman", "aman@gmail.com", "IT", 10000
//    );
//
//    System.out.println(e1.calculateSalary());
//    System.out.println(e2.calculateSalary());
//    System.out.println(e3.calculateSalary());

//    lesson Polymorphism with ArrayList
    ArrayList<Employee> employees = new ArrayList<>();

    employees.add(new PermanentEmployee(
            101, "Rahul", "rahul@gmail.com", "IT", 30000
    ));

    employees.add(new ContractEmployee(
            102, "Priya", "priya@gmail.com", "HR", 160, 200
    ));

    employees.add(new InternEmployee(
            103, "Aman", "aman@gmail.com", "IT", 10000
    ));

    for (Employee emp : employees) {
        System.out.println(
                emp.getName() + " Salary: " + emp.calculateSalary()
        );
    }

}

