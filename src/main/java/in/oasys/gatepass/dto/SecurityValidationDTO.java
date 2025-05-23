package in.oasys.gatepass.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SecurityValidationDTO {
	private Integer validationId;
	private Integer requestId;
	private String securityGuardId;
	private LocalDateTime entryTime;
	private LocalDateTime exitTime;
	private LocalDateTime createdOn;
	private String status;

}
