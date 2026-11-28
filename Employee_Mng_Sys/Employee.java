
import java.util.ArrayList;
import java.util.Collections;
import java.util.*;
import java.time.LocalDate;
import java.time.YearMonth;





class Employee {


    private int employeeId;
    private String name;
    private String email;
    private String department;

    public Employee(int employeeId, String name, String email, String department){

        if(employeeId <=0 || name == null || name.isBlank()){
            throw new IllegalArgumentException("Not valid inputs");
        }
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
                    ", Dept: " + emp.getDepartment()+
//Runtime Polymorphism + Low Coupling.
                    ", Salary: "+ emp.calculateSalary());
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


        if (employeeId <= 0) {
            throw new IllegalArgumentException("Invalid employee ID");
        }

        if (newDepartment == null || newDepartment.isBlank()) {// rejects Space-only stirngs.
            throw new IllegalArgumentException("Department cannot be empty");
        }
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

//Phase 4: Salary Calculation Design
public class SalaryCalculator {


    public double calculate(Employee employee){
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        return employee.calculateSalary();
    }
}

public class FreelanceEmployee extends Employee {

    private double hourlyRate;
    private double totalHours;

    public FreelanceEmployee(int employeeId, String name,
                             String email, String department,
                             double hourlyRate, double totalHours) {

        super(employeeId, name, email, department);
        this.hourlyRate = hourlyRate;
        this.totalHours = totalHours;
    }

    @Override
    public double calculateSalary() {
        return hourlyRate * totalHours;
    }
}


//Phase: 5
public enum AttendanceStatus {
    PRESENT,
    ABSENT
}

public class Attendance {

    private int employeeId;
    private LocalDate date;
    private AttendanceStatus status;

    public Attendance(int employeeId, LocalDate date,
                      AttendanceStatus status) {

        if (employeeId <= 0) {
            throw new IllegalArgumentException("Invalid Employee ID");
        }

        if (date == null || status == null) {
            throw new IllegalArgumentException("Date and status cannot be null");
        }

        this.employeeId = employeeId;
        this.date = date;
        this.status = status;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public AttendanceStatus getStatus() {
        return status;
    }
}

public class AttendanceService {
//  save in List of Attendance
    private List<Attendance> records;
//  constructor initialization
    public AttendanceService() {
        records = new ArrayList<>();
    }
//  Mark Attendance
    public void markAttendance(Attendance attendance) {

        if (attendance == null) {
            throw new IllegalArgumentException(
                    "Attendance cannot be null"
            );
        }

//    Lesson 4: Duplicate Attendance Validation
        for (Attendance record : records) {

            if (record.getEmployeeId() == attendance.getEmployeeId()
                    && record.getDate().equals(attendance.getDate())) {

                throw new IllegalArgumentException(
                        "Attendance already marked for this date!"
                );
            }
        }
        records.add(attendance);
    }

//    Lesson 5:View Attendance
public void displayAttendance(int employeeId) {

    boolean found = false;

    for (Attendance record : records) {

        if (record.getEmployeeId() == employeeId) {

            System.out.println("Employee ID: " + record.getEmployeeId());
            System.out.println("Date: " + record.getDate());
            System.out.println("Status: " + record.getStatus());

            found = true;
        }
    }

    if (!found) {
        System.out.println("No attendance records found!");
    }
}
//    Lesson 6: Calculate Attendance Summary
public void attendanceSummary(int employeeId) {

    int presentDays = 0;
    int absentDays = 0;

    for (Attendance record : records) {

        if (record.getEmployeeId() == employeeId) {

            if (record.getStatus() == AttendanceStatus.PRESENT) {
                presentDays++;
            }
            else if (record.getStatus() == AttendanceStatus.ABSENT) {
                absentDays++;
            }
        }
    }

    System.out.println("Employee ID: " + employeeId);
    System.out.println("Present Days: " + presentDays);
    System.out.println("Absent Days: " + absentDays);
    System.out.println("Total Days: " + (presentDays + absentDays));
}

}

//phase 6
public interface Notification {
    void send(String message);
}
public class EmailNotification implements Notification {

    @Override
    public void send(String message) {
        System.out.println("Sending Email: " + message);
    }
}
public class SMSNotification implements Notification {

    @Override
    public void send(String message) {
        System.out.println("Sending SMS: " + message);
    }
}
public class WhatsAppNotification implements Notification {

    @Override
    public void send(String message) {
        System.out.println("Sending WhatsApp: " + message);
    }
}
public class NotificationService {

    private List<Notification> ns;

    public NotificationService(List<Notification> notifications) {
        //    Lesson 9: Constructor null Validation
        if(notifications == null || notifications.isEmpty()){
            throw new IllegalArgumentException("At least one notification channel is required");
        }
        if (notifications.contains(null)) {
            throw new IllegalArgumentException(
                    "Notification cannot be null"
            );
        }
        this.ns = new ArrayList<>(notifications);
    }

    public  void notifyEmployee(String message){
        for(Notification notifications : ns){
            notifications.send(message);
        }
    }

}
public class PushNotification implements Notification {

    @Override
    public void send(String message) {
        System.out.println("Sending Push Notification: " + message);
    }
}

//phase 7;
public class Payroll {

    private int employeeId;
    private double salary;
    private LocalDate payrollDate;

    public Payroll(int employeeId, double salary,
                   LocalDate payrollDate) {

        if (employeeId <= 0 || salary < 0 || payrollDate == null) {
            throw new IllegalArgumentException("Invalid input field");
        }

        this.employeeId = employeeId;
        this.salary = salary;
        this.payrollDate = payrollDate;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public double getSalary() {
        return salary;
    }

    public LocalDate getPayrollDate() {
        return payrollDate;
    }
}

public class PayrollService {

    private final SalaryCalculator salaryCalculator;
    private final List<Payroll> payrollRecords;
    private final NotificationService notificationService;


    public PayrollService(SalaryCalculator salaryCalculator,NotificationService notificationService) {

        if (salaryCalculator == null || notificationService == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.salaryCalculator = salaryCalculator;
        this.payrollRecords = new ArrayList<>();
        this.notificationService = notificationService;

    }

    public Payroll generatePayroll(Employee employee) {

//        Exception Handling Audit
        if (employee == null) {
            throw new IllegalArgumentException("Employee not valid");
        }

        YearMonth currentMonth = YearMonth.now();

        for (Payroll record : payrollRecords) {

            if (record.getEmployeeId() == employee.getEmployeeId()
                    && YearMonth.from(record.getPayrollDate()).equals(currentMonth)) {

                throw new IllegalArgumentException(
                        "Payroll already generated for this employee this month!"
                );
            }
        }
        double salary = salaryCalculator.calculate(employee);

        Payroll payroll = new Payroll(
                employee.getEmployeeId(),
                salary,
                LocalDate.now()
        );

        payrollRecords.add(payroll);
        // Send notification only after successful payroll creation
        notificationService.notifyEmployee(
                "Payroll generated! Salary: " + salary
        );

        return payroll;
    }
    public void displayPayrollRecords() {

        if (payrollRecords.isEmpty()) {
            System.out.println("No payroll records found!");
            return;
        }

        for (Payroll payroll : payrollRecords) {

            System.out.println("Employee ID: " + payroll.getEmployeeId());
            System.out.println("Salary: " + payroll.getSalary());
            System.out.println("Payroll Date: " + payroll.getPayrollDate());

            System.out.println("----------------------");
        }
    }

}

public class ReportService {

    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    private final PayrollService payrollService;

    public ReportService(EmployeeService employeeService,AttendanceService attendanceService,
    PayrollService payrollService) {

        if ( employeeService== null || attendanceService == null || payrollService== null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.employeeService = employeeService;
        this.attendanceService = attendanceService;
        this.payrollService = payrollService;

    }

    public void generateEmployeeReport(){

        List<Employee> employees = employeeService.getEmployeesDetails();
        if(employees.isEmpty()){
            System.out.println("No employee found!");
            return;
        }

        for(Employee emp : employees){
            System.out.printf("Employee ID: "+ emp.getEmployeeId());
            System.out.println("Name: "+ emp.getName());
            System.out.println("Emali: "+ emp.getEmail());
            System.out.println("Department: "+ emp.getDepartment());
            System.out.printf("-----------------------------------");
        }
    }
    public void generateSalaryReport(){
        List<Employee> employees = employeeService.getEmployeesDetails();
        if(employees.isEmpty()){
            System.out.println("No employee found!");
            return;
        }
        for(Employee emp : employees){
            System.out.println("Name: "+ emp.getName());
            System.out.println("Salary: "+ emp.calculateSalary());
        }
    }
    public void generateDepartmentReport(String department) {

        List<Employee> employees = employeeService.getEmployeesDetails();

        if (employees.isEmpty()) {
            System.out.println("No employee found!");
            return;
        }

        boolean found = false;

        for (Employee emp : employees) {
            if (department.equalsIgnoreCase(emp.getDepartment())) {

                System.out.println("Employee ID: " + emp.getEmployeeId());
                System.out.println("Name: " + emp.getName());

                found = true;
            }
        }
        if (!found) {
            System.out.println("No employees found in this department!");
        }
    }

    public void generateAttendanceReport(int employeeId){
        Employee emp = employeeService.searchEmployee(employeeId);
        if(emp == null){
            System.out.println("Employee not found");
            return;
        }
        attendanceService.attendanceSummary(emp.getEmployeeId());
    }

    public void generatePayrollReport() {
        payrollService.displayPayrollRecords();
    }
}


public interface ReportGenerator {
    void generate();
}
public class HTMLReportGenerator implements ReportGenerator {

    @Override
    public void generate() {
        System.out.println("Generating HTML Report...");
    }
}



//phase 8
public void main(){
//    EmployeeService employeeService = new EmployeeService();
//    AttendanceService attendanceService = new AttendanceService();
//    SalaryCalculator calculator = new SalaryCalculator();
//
//    List<Notification> channels = Arrays.asList(
//            new EmailNotification(),
//            new SMSNotification()
//    );
//    NotificationService notificationService =
//            new NotificationService(channels);
//
//    PayrollService payrollService =
//            new PayrollService(calculator, notificationService);
//
//    ReportService reportService =
//            new ReportService(
//                    employeeService,
//                    attendanceService,
//                    payrollService
//            );
//
//    reportService.generateEmployeeReport();
//
//    reportService.generateSalaryReport();
//
//    reportService.generateDepartmentReport("IT");
//
//    reportService.generateAttendanceReport(101);
//
//    reportService.generatePayrollReport();

    EmployeeService employeeService = new EmployeeService();

// 1. First add employees
        Employee e1 = new PermanentEmployee(
            101, "Rahul", "rahul@gmail.com", "IT", 30000
    );


    Employee e2 = new ContractEmployee(
            102, "Priya", "priya@gmail.com", "HR", 160, 200
    );

    Employee e3 = new InternEmployee(
            103, "Aman", "aman@gmail.com", "IT", 10000
    );
    employeeService.addEmployee(e1);
    employeeService.addEmployee(e2);
    employeeService.addEmployee(e3);

// 2. Create other services
    SalaryCalculator calculator = new SalaryCalculator();
    List<Notification> channels = new ArrayList<>();

    channels.add(new EmailNotification());
    channels.add(new SMSNotification());
    channels.add(new WhatsAppNotification());

    NotificationService notificationService =
            new NotificationService(channels);

    AttendanceService attendanceService = new AttendanceService();

    PayrollService payrollService =
            new PayrollService(calculator, notificationService);

// 3. Generate payroll records
    payrollService.generatePayroll(e1);
    payrollService.generatePayroll(e2);

// 4. Inject the SAME service objects
    ReportService reportService =
            new ReportService(
                    employeeService,
                    attendanceService,
                    payrollService
            );

// 5. Generate reports
    reportService.generateEmployeeReport();
    reportService.generateSalaryReport();
    reportService.generateDepartmentReport("IT");
    reportService.generateAttendanceReport(101);
    reportService.generatePayrollReport();
}
//phase 7
//public void main(String[] args){
//    SalaryCalculator calculator = new SalaryCalculator();
//
//    PayrollService payrollService =
//            new PayrollService(calculator);
//
//    Employee e1 = new PermanentEmployee(
//            101, "Rahul", "rahul@gmail.com", "IT", 30000
//    );
//    Employee e2 = new PermanentEmployee(
//            101, "Rahul", "rahul@gmail.com", "IT", 30000
//    );

//    Employee e2 = new ContractEmployee(
//            102, "Priya", "priya@gmail.com", "HR", 160, 200
//    );
//
//    Employee e3 = new InternEmployee(
//            103, "Aman", "aman@gmail.com", "IT", 10000
//    );

//    payrollService.generatePayroll(e1);
//    payrollService.generatePayroll(e2);

//    Lesson: Exception Handling
//    try {
//        payrollService.generatePayroll(e2);
//        System.out.println("Payroll generated successfully!");
//    }catch (IllegalArgumentException e){
//        System.out.println(" Error: "+ e.getMessage());
//    }

//    payrollService.generatePayroll(e3);

//    payrollService.displayPayrollRecords();

//    Lesson:Payroll + Notification Integration
//    SalaryCalculator calculator = new SalaryCalculator();
//
//    List<Notification> channels = new ArrayList<>();
//    channels.add(new EmailNotification());
//    channels.add(new SMSNotification());
//
//    NotificationService notificationService =
//            new NotificationService(channels);
//
//    PayrollService payrollService =
//            new PayrollService(calculator, notificationService);
//
//    payrollService.generatePayroll(e1);
//
//    try {
//        payrollService.generatePayroll(e1);
//    } catch (IllegalArgumentException e) {
//        System.out.println("Error: " + e.getMessage());
//    }
//}

//phase 6
//public void main(String[] args) {

//    Lesson 4: NotificationService
//    Notification email = new EmailNotification();
//    Notification sms = new SMSNotification();
//    Notification whatsapp = new WhatsAppNotification();
//
//    NotificationService service1 = new NotificationService(email);
//    NotificationService service2 = new NotificationService(sms);
//    NotificationService service3 = new NotificationService(whatsapp);
//
//    service1.notifyEmployee("Salary credited!");
//    service2.notifyEmployee("Attendance marked!");
//    service3.notifyEmployee("Welcome!");
//
//    Lesson 5: Understanding DI
//    NotificationService service = new NotificationService(new EmailNotification());
//    service.notifyEmployee("Email Notification");
//
//    Lesson 6: OCP + DIP + Constructor DI.
//    Notification push = new PushNotification();
//    NotificationService service = new NotificationService(push);
//    service.notifyEmployee("New Update Available");
//
//    Lesson 7: Multiple Notifications,
//    List<Notification> channel = Arrays.asList(
//            new EmailNotification(),
//            null,
//            new SMSNotification()
//    );
//
//    NotificationService service = new NotificationService(channel);
//    service.notifyEmployee("Notification send In all platform");
//
//    Lesson 10: Integration Testing
//    List<Notification> channels = Arrays.asList(
//            new EmailNotification(),
//            new SMSNotification(),
//            new WhatsAppNotification()
//    );
//
//    NotificationService service = new NotificationService(channels);
//
//    service.notifyEmployee("Salary credited!");
//
//}
//phase 5
//public void main() {
//    AttendanceService service = new AttendanceService();
//
//    service.markAttendance(
//            new Attendance(101, LocalDate.of(2026, 11, 20),
//                    AttendanceStatus.PRESENT)
//    );
//
//    service.markAttendance(
//            new Attendance(101, LocalDate.of(2026, 11, 21),
//                    AttendanceStatus.ABSENT)
//    );
//
//    service.markAttendance(
//            new Attendance(101, LocalDate.of(2026, 11, 22),
//                    AttendanceStatus.PRESENT)
//    );
//
//    service.attendanceSummary(101);
//}

//Phase 1 ,2 3, 4
//public  void main() {
//
// Testing: Phase 2
//  Lessongs: Polymorphism
//
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
//
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
//
//    lesson Polymorphism with ArrayList
//    ArrayList<Employee> employees = new ArrayList<>();
//
//    employees.add(new PermanentEmployee(
//            101, "Rahul", "rahul@gmail.com", "IT", 30000
//    ));
//
//    employees.add(new ContractEmployee(
//            102, "Priya", "priya@gmail.com", "HR", 160, 200
//    ));
//
//    employees.add(new InternEmployee(
//            103, "Aman", "aman@gmail.com", "IT", 10000
//    ));
//
//    for (Employee emp : employees) {
//        System.out.println(
//                emp.getName() + " Salary: " + emp.calculateSalary()
//        );
//    }
//
//    lesson: Integrate Employee Types with EmployeeService
//    EmployeeService service = new EmployeeService();
//
//    service.addEmployee(
//            new PermanentEmployee(
//                    101, "Rahul", "rahul@gmail.com", "IT", 30000
//            )
//    );
//
//    service.addEmployee(
//            new ContractEmployee(
//                    102, "Priya", "priya@gmail.com", "HR", 160, 200
//            )
//    );
//
//    service.addEmployee(
//            new InternEmployee(
//                    103, "Aman", "aman@gmail.com", "IT", 10000
//            )
//    );
//
//    service.displayEmployees();
//
//    Phase 4:
//    lesson:SalaryCalculator Integration
//    SalaryCalculator calculator = new SalaryCalculator();
//
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
//
//    System.out.println(calculator.calculate(e1));
//    System.out.println(calculator.calculate(e2));
//    System.out.println(calculator.calculate(e3));
//
////without modification.
//    Employee emp = new FreelanceEmployee(
//            104, "Amit", "amit@gmail.com", "Design", 500, 40
//    );
//    System.out.println(calculator.calculate(emp));
//
//}



