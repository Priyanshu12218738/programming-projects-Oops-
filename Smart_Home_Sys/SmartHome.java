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

     double getEnergyUsageKWh(double hours){

    }
     String getDetails(){

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


}



public class SmartHome {
}
