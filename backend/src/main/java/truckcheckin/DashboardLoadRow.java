package truckcheckin;

	/*
	 * This class represents one row ofn the shipper dashboard.
	 * 
	 * It is not a database table.
	 * It is a DTO: Data Transfer Object.
	 *
	 * Purpose:
	 * Combine data from:
	 * - loads
	 * - driver_checkins
	 * into one object that React can easily display.
	 */
	 
public class DashboardLoadRow {
		
	private Long loadId;
	private String loadNumber;
	private String status;
		
	private String driverFirstName;
	private String driverLastName;
	private String truckingCompany;
	private String phoneNumber;
	private String trailerNumber;
		
	public DashboardLoadRow(
		Long loadId,
		String loadNumber,
		String status,
		String driverFirstName,
		String driverLastName,
		String truckingCompany,
		String phoneNumber,
		String trailerNumber) {
			
			
	this.loadId = loadId;
	this.loadNumber = loadNumber;
	this.status = status;
	this.driverFirstName = driverFirstName;
	this.driverLastName = driverLastName;
	this.truckingCompany = truckingCompany;
	this.phoneNumber = phoneNumber;
	this.trailerNumber = trailerNumber;
    }

public Long getLoadId() { return loadId; }
public String getLoadNumber() { return loadNumber; }
public String getStatus() { return status; }
public String getDriverFirstName() { return driverFirstName; }
public String getDriverLastName() { return driverLastName; }
public String getTruckingCompany() { return truckingCompany; }
public String getPhoneNumber() { return phoneNumber; }
public String getTrailerNumber() { return trailerNumber; }

}

	