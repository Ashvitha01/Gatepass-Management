package in.oasys.gatepass.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ApprovalDTO {
	private String approvalId;
	private UserDTO staffId;
	private Integer requestId;
	private String status;
	private String remarks;
	private LocalDateTime approvedAt;

}
