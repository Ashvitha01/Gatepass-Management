package in.oasys.gatepass.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BlacklistedStudentDTO {
	private Integer id;
	private String studentId;
	private String reason;
	private String status;
	private LocalDateTime blacklistedAt;

}
