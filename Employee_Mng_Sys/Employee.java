package Employee_Mng_Sys;

import java.util.ArrayList;
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

}

//Lesson 2: EmployeeService
class EmployeeServices{

    private ArrayList<Employee> employees;

    public EmployeeServices(){
        this.employees = new ArrayList<>();
    }

    public void addEmployee(Employee employee){
        employees.add(employee);
    }

    public ArrayList<Employee> getEmployeesDetails() {
        return this.employees;
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

}

