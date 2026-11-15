package Car_Sys;

public class PetrolEngine implements Engine {

    private boolean running = false;

    @Override
    public boolean start() {
        if (running) {
            System.out.println("Engine already running");
            return false;
        }

        running = true;
        System.out.println("Petrol engine started");
        return true;
    }

    @Override
    public boolean stop() {
        if (!running) {
            System.out.println("Engine already stopped");
            return false;
        }

        running = false;
        System.out.println("Petrol engine stopped");
        return true;
    }
}
