package truckcheckin;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loads")
public class Load {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "load_number")
	private String loadNumber;
	
	@Column(name = "trucking_company")
	private String truckingCompany;
	
	@Column(name = "trailer_number")
	private String trailerNumber;
	
	@Column(name = "scheduled_pickup_date")
	private LocalDate scheduledPickupDate;
	
	private Boolean active;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	private String status;
	
	public Load() {
	}
	
	public Long getId() {
		return id;
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
	
	public LocalDate getScheduledPickupDate() {
		return scheduledPickupDate;
	}
	
	public Boolean getActice() {
		return active;
	}
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
}	
