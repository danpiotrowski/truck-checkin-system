package truckcheckin;

public class Load {

    private String loadNumber;
    private String truckingCompany;
    private String trailerNumber;
    private String status;

    public Load(String loadNumber, String truckingCompany,
                String trailerNumber, String status) {

        this.loadNumber = loadNumber;
        this.truckingCompany = truckingCompany;
        this.trailerNumber = trailerNumber;
        this.status = status;
    }

    public String getLoadNumber() {
        return loadNumber;
    }

    public String getTruckingCompany() {
        return truckingCompany;
    }

    public String getTrailerNumber() {
        return trailerNumber;
    }

    public String getStatus() {
        return status;
    }
}