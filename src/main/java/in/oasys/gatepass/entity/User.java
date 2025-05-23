package in.oasys.gatepass.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))

public class User {

	@Id
	@Column(name = "user_id", length = 50, nullable = false, unique = true)
	private String userId;

	@OneToMany(mappedBy = "studentid", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<GatePassRequestEntity> gatePassRequests;

	@OneToMany(mappedBy = "staffId", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<Approval> approval;

	@OneToMany(mappedBy = "securityGuard", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<SecurityValidation> securityvalidation;

	@OneToMany(mappedBy = "securityGuard", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<SecurityIncident> securityincident;
	@OneToMany(mappedBy = "studentid", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<SecurityIncident> incident;

	@OneToMany(mappedBy = "studentid", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<BlacklistedStudents> blacklistedstudents;

	@OneToMany(mappedBy = "userid", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private List<NotificationEntity> notification;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Column(name = "email", length = 100, nullable = false, unique = true)
	private String email;

	@Column(name = "password_hash", length = 255, nullable = false)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role role;

	@Column(name = "contact_number", length = 15, nullable = false)
	private String contactNumber;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	public enum Role {
		STUDENT, STAFF, SECURITY, ADMIN
	}

}