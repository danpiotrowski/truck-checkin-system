package truckcheckin;
import jakarta.persistence.*;

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
}	
