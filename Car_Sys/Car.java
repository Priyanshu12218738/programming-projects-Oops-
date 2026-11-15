package Car_Sys;

import java.util.Scanner;

public class Car {

    private final Engine engine;

    public Car(Engine engine) {
        if (engine == null) {
            throw new IllegalArgumentException("Engine cannot be null");
        }

        this.engine = engine;
    }

    public void startCar() {
        if (engine.start()) {
            System.out.println("Car is ready");
        }
    }

    public void stopCar() {
        if (engine.stop()) {
            System.out.println("Car has stopped");
        }
    }
}




