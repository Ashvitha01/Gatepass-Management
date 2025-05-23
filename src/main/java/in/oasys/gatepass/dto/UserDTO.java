package in.oasys.gatepass.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserDTO {
	private String userId;
	private String name;
	private String email;
	private String contactNumber;
	private String role;
	private LocalDateTime createdAt;
	private String passwordHash;

}
