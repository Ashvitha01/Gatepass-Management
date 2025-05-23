package in.oasys.gatepass.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SecurityIncidentDTO {
	private Integer incidentId;
	private String studentId;
	private String securityGuardId;
	private String description;
	private LocalDateTime reportedAt;
	private String status;

}
