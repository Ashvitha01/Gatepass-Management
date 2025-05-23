package in.oasys.gatepass.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;
import jakarta.persistence.Id;

@Data
@Entity
public class BlacklistedStudents {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer Id;
	private String Reason;

	@Enumerated(EnumType.STRING)
	private Status status = Status.BLACKLISTED;

	public enum Status {
		BLACKLISTED, UNBLACKLISTED
	}

	private LocalDateTime Blacklistedat;

	@ManyToOne(optional = false)
	@JoinColumn(name = "studentid", nullable = false)
	@ToString.Exclude
	private User studentid;

}
