package in.oasys.gatepass.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ConfigurationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer configure_id;

	private String keyname;

	private String value;

}
