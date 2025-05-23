package in.oasys.gatepass.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class NotificationDTO {
	private Integer notificationId;
	private String userId;
	private String message;
	private LocalDateTime sentAt;
	private String status;

}
