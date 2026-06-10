package truckcheckin;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "driver_checkins")
public class DriverCheckin {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long loadId;
	private String driverFirstName;
	private String driverLastName;
	private String truckingCompany;
	private String phoneNumber;
	private String trailerNumber;
	private LocalDateTime checkinTime;
	private Boolean active;
	
	public Long getId() { return id; }
	public Long getLoadId() {return loadId; }
	public String getDriverFirstName() { return driverFirstName; }
	public String getDriverLastName() { return driverLastName; }
	public String getTruckingCompany() { return truckingCompany; }
	public String getPhoneNumber() { return phoneNumber; }
	public String getTrailerNumber() { return trailerNumber; }
	public LocalDateTime getCheckinTime() { return checkinTime; }
	public Boolean getActive() { return active; }
	
	public void setLoadId(Long loadId) { this.loadId = loadId; }
	public void setDriverFirstname(String driverFirstName) { this.driverFirstName = driverFirstName; }
	public void setDriverLastName(String driverLastName) { this.driverLastName = driverLastName; }
	public void setTruckingCompany(String truckingCompany) { this.truckingCompany = truckingCompany; }
	public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
	public void setTrailerNumber(String trailerNumber) { this.trailerNumber = trailerNumber; }
	public void setCheckinTime(LocalDateTime checkinTime) { this.checkinTime = checkinTime; }
	public void setActive(Boolean active) { this.active = active; }
}
