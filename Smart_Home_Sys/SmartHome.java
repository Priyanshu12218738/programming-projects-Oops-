package Smart_Home_Sys;

import java.time.LocalDateTime;

enum DeviceStatus{
    ON,
    OFF
}
enum ConnectivityStatus{
    ONLINE,
    OFFLINE
}

enum LightColorMode{
    WARM,
    COOL,
    RGB
}

//2.1 SmartHome
public class SmartHome {
}

//2.3 SmartDevice (abstract base class)
abstract class SmartDevice{
    private String id;
    private String name;
    private DeviceStatus status;
    private ConnectivityStatus connectivity;
    private double ratedPowerWatts;
    private LocalDateTime createdAt;

    public SmartDevice(String id,
                       String name,
                        DeviceStatus status,
                        ConnectivityStatus connectivity,
                        double ratedPowerWatts,
                        LocalDateTime createdAt
                       ){
        this.id = id;
        this.name = name;
        this.status = status;
        this.connectivity = connectivity;
        this.ratedPowerWatts = ratedPowerWatts;
        this.createdAt = createdAt;
    }

     String getId(){
        return id;
    }
     String getName(){
        return name;
    }
     DeviceStatus getStatus(){
        return status;
    }
     ConnectivityStatus getConnectivity(){
        return connectivity;
    }




    public void turnOn(){
        ensureOnline();
        validateCanTurnOn();
        this.status = DeviceStatus.ON;
    }

    public void setConnectivity(ConnectivityStatus connectivity) {
        this.connectivity = connectivity;
    }

    public void turnOff(){
        ensureOnline();

        this.status = DeviceStatus.OFF;
    }
    protected void ensureOnline(){
        if(this.connectivity== ConnectivityStatus.OFFLINE){
            throw  new IllegalStateException("Device Is offline");
        }
    }
    protected void validateCanTurnOn(){
            if(this.status==DeviceStatus.ON){
                throw new IllegalStateException("This device is Already ON");
            }
    }
    String getDetails(){
        return "Device ID:" + id+ "\n" +
                "Name: "+ name +  "\n"+
                "Status: " + status +"\n"+
                "Connectivity: "+connectivity +"\n"+
                "Rated Power: "+ ratedPowerWatts +"\n";
    }
    double getEnergyUsageKWh(double hours){
        if(hours<0){
            throw new IllegalArgumentException("Invalid input hour");
        }
        double energy = ratedPowerWatts * hours/1000;
        return energy;

    }




}


//2.4 SmartLight extends SmartDevice
class SmartLight extends SmartDevice{

    private int brigntness;
    private LightColorMode  colorMode;
    public SmartLight(String id, String name, DeviceStatus status, ConnectivityStatus connectivity, double ratedPowerWatts, LocalDateTime createdAt) {
        super(id, name, status, connectivity, ratedPowerWatts, createdAt);

        this.brigntness = 0;
        this.colorMode = LightColorMode.COOL;

    }

    public void setBrightness(int value){
        if(value >= 0 && value <= 100){
            this.brigntness = value;
        }
        else throw new IllegalArgumentException("Brightness not valid");
    }

    public int getBrigntness(){
        return brigntness;
    }
    public void setColorMode(LightColorMode mode){

        if(mode == null){
            throw new IllegalArgumentException("Input valid colour");
        }
        else{
            this.colorMode = mode;
        }

    }
    public LightColorMode getColorMode(){
        return this.colorMode;
    }

    @Override
    public String getDetails(){
        return super.getDetails() + "\n"+
                "Brightness" + brigntness +"\n"+
                "Colour Mode"+ colorMode ;

    }
    @Override
    public double getEnergyUsageKWh(double hours){
        return super.getEnergyUsageKWh(hours) * brigntness/100.0;

    }
}





