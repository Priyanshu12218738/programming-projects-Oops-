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

abstract class SmartDevice{
    String id;
    String name;
    DeviceStatus status;
    ConnectivityStatus connectivity;
    double ratedPowerWatts;
    LocalDateTime createdAt;

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

    private String getId(){
        return id;
    }
    private String getName(){
        return name;
    }
    private DeviceStatus getStatus(){
        return status;
    }
    private ConnectivityStatus getConnectivity(){
        return connectivity;
    }
    private void turnOn(){

    }
    private void turnOf(){

    }
    private double getEnergyUsageKWh(double hours){

    }
    private String getDetais(){

    }
    protected void ensureOnline(){

    }
    protected void validateCanTurnOn(){

    }

}



public class SmartHome {
}
