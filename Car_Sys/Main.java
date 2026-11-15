package Car_Sys;

import java.util.Scanner;

//public class Main {
//
//    public static void main(String[] args) {
//
//        Scanner sc = new Scanner(System.in);
//
//        System.out.println("Choose Engine:");
//        System.out.println("1. Diesel");
//        System.out.println("2. Petrol");
//        System.out.println("3. Electric");
//
//        int choice = sc.nextInt();
//
//        Engine engine = null;
//
//        switch (choice) {
//            case 1:
//                engine = new DieselEngine();
//                break;
//
//            case 2:
//                engine = new PetrolEngine();
//                break;
//
//            case 3:
//                engine = new ElectricEngine();
//                break;
//
//            default:
//                System.out.println("Invalid choice");
//                sc.close();
//                return;
//        }
//
//        Car car = new Car(engine);
//
//        boolean exit = false;
//
//        while (!exit) {
//
//            System.out.println("\n1. Start Car");
//            System.out.println("2. Stop Car");
//            System.out.println("3. Exit");
//
//            int option = sc.nextInt();
//
//            switch (option) {
//                case 1:
//                    car.startCar();
//                    break;
//
//                case 2:
//                    car.stopCar();
//                    break;
//
//                case 3:
//                    exit = true;
//                    System.out.println("Exiting...");
//                    break;
//
//                default:
//                    System.out.println("Invalid option");
//            }
//        }
//
//        sc.close();
//    }
//}

public class Main {
    public static void main(String[] args) {

        Engine engine = new DieselEngine();
        Car car = new Car(engine);

        car.stopCar();  // Already stopped
        car.startCar(); // Starts
        car.startCar(); // Already running
        car.stopCar();  // Stops
        car.stopCar();  // Already stopped
    }
}
