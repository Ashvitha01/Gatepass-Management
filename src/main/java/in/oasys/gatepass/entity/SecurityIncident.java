package in.oasys.gatepass.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
public class SecurityIncident {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer IncidentId;

	private String description;

	private LocalDateTime reportedat;

	@ManyToOne(optional = false)
	@JoinColumn(name = "studentid", nullable = false)
	@ToString.Exclude
	private User studentid;

	@ManyToOne(optional = false)
	@JoinColumn(name = "securityGuard", nullable = false)
	@ToString.Exclude
	private User securityGuard;

	@Enumerated(EnumType.STRING)
	private Status status = Status.PENDING;

	public enum Status {
		PENDING, RESOLVED
	}

}
