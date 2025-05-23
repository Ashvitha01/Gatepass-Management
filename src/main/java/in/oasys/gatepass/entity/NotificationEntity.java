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
public class NotificationEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer notification_id;

	private String message;

	private LocalDateTime sent_at;

	@ManyToOne(optional = false)
	@JoinColumn(name = "userid", nullable = false)
	@ToString.Exclude
	private User userid;

	@Enumerated(EnumType.STRING)
	private Status status = Status.PENDING;

	public enum Status {

		SENT, PENDING
	}

}
