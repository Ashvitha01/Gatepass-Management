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
public class SecurityValidation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer Validationid;
	private LocalDateTime EntryTime;
	private LocalDateTime ExitTime;
	private LocalDateTime createdon = LocalDateTime.now();
	@Enumerated(EnumType.STRING)
	private Status status = Status.APPROVED;

	public enum Status {
		ENTRY, EXIT, APPROVED, VALIDATED, COMPLETED, EXPIRED, INVALID
	}

	@ManyToOne(optional = false)
	@JoinColumn(name = "requestId", nullable = false)
	@ToString.Exclude
	private GatePassRequestEntity requestId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "securityGuard", nullable = false)
	@ToString.Exclude
	private User securityGuard;

}
