package it.unisalento.iot2425.vehicleserviceproject.dto;

public class ResultDTO {

    //costanti per codice di uscita
    public final static int OK = 0;
    public final static int TARGA_IN_USO = 1;
    public final static int ELIMINATO = 2;
    public final static int NON_TROVATO = 3;
    public final static int ERRORE = 4;


    private int result;
    private String message;
    private VehicleDTO vehicle;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VehicleDTO getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleDTO vehicle) {
        this.vehicle = vehicle;
    }
}
