package in.oasys.gatepass.entity;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ForeignKey;

@Data
@Entity
@Table(name = "gatepass_request_table")
public class GatePassRequestEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer requestId;

	public Integer getRequestId() {
		return requestId;
	}

	private String reason;
	private String email;
	private String eventId;
	private Date date;
	@Column(nullable = true)
	private LocalDateTime entrytime;
	@Column(nullable = true)
	private LocalDateTime exittime;
	private String qrcode;
	private String documentPath;
	private LocalDateTime createdAt = LocalDateTime.now();

	@ManyToOne(optional = false)
	@JoinColumn(name = "studentid", nullable = false)
	@ToString.Exclude
	private User studentid;

	// requestid foreign key mapped in approval
	@OneToMany(mappedBy = "requestId", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<Approval> approval;

	// requestid foreign key mapped in securityvalidation
	@OneToMany(mappedBy = "requestId", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<SecurityValidation> securityvalidation;

	@Enumerated(EnumType.STRING)
	private Status status = Status.PENDING;

	public enum Status {
		PENDING, APPROVED, REJECTED, VALIDATED, CANCELLED, COMPLETED, EMMERGENCYAPPROVED, EXPIRED, INVALID
	}

	@Enumerated(EnumType.STRING)
	private Gatepasstype type;

	public enum Gatepasstype {
		ENTRY, EXIT
	}

}
