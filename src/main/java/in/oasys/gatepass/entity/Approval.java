package in.oasys.gatepass.entity;

import jakarta.persistence.*;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "approvals")

//@Setter
//@Getter
public class Approval {

	@Id
	@Column(name = "approval_id", length = 50)
	private String approvalId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "staffId", nullable = false)
	@ToString.Exclude
	private User staffId;

	@ManyToOne(optional = false)
	@JoinColumn(name = "requestId", nullable = false)
	@ToString.Exclude
	private GatePassRequestEntity requestId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;

	@Column(columnDefinition = "TEXT")
	private String remarks;

	@Column(name = "approved_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime approvedAt = LocalDateTime.now();

	public enum Status {
		APPROVED, REJECTED
	}

}
