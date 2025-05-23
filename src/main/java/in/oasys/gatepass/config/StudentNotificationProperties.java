package in.oasys.gatepass.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

public class StudentNotificationProperties {
	@Component
	@ConfigurationProperties(prefix = "student")
	public class StaffNotificationProperties {
		private String email;

		// Getters and Setters
		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

	}
}
