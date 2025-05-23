package in.oasys.gatepass.dto;

import java.time.LocalDateTime;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.sql.Date;

@Data
public class GatePassRequestDTO {
	private Integer requestId;
	private UserDTO studentid;
	private String reason;
	private String email;
	private String eventId;
	private Date date;
	private LocalDateTime entrytime;
	private LocalDateTime exittime;
	private String qrcode;
	private String status;
	private String documentPath;
	private LocalDateTime createdAt;

	@Enumerated(EnumType.STRING)
	private Gatepasstype type;

	public enum Gatepasstype {
		ENTRY, EXIT
	}

}
