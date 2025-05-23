package in.oasys.gatepass.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.oasys.gatepass.dto.UserDTO;
import in.oasys.gatepass.entity.User;
import in.oasys.gatepass.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public String changePassword(UserDTO request) {

		User user = userRepository.findByUserId(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (passwordEncoder.matches(request.getPasswordHash(), user.getPasswordHash())) {
			return "This password is Already in use";
		}

		user.setPasswordHash(passwordEncoder.encode(request.getPasswordHash()));
		userRepository.save(user);

		return "Password changed successfully.";
	}

	public User checklogin(UserDTO request) {
		try {
			User user = userRepository.findByUserId(request.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found"));

			if (!passwordEncoder.matches(request.getPasswordHash(), user.getPasswordHash())) {
				throw new RuntimeException("Wrong password");
			}

			return user;

		} catch (Exception e) {
			e.printStackTrace(); // or use a logger
			throw new RuntimeException("Login failed: " + e.getMessage());
		}
	}

	// Save User (Create)
	public UserDTO saveUser(UserDTO dto) {
		if (dto.getPasswordHash() == null || dto.getPasswordHash().isEmpty()) {
			throw new IllegalArgumentException("Password cannot be null or empty");
		}

		// Check if the userId already exists
		if (userRepository.existsById(dto.getUserId())) {
			throw new IllegalArgumentException("User ID '" + dto.getUserId() + "' already exists");
		}

		User user = new User();
		user.setUserId(dto.getUserId());
		user.setName(dto.getName());
		user.setEmail(dto.getEmail());
		user.setContactNumber(dto.getContactNumber());
		user.setRole(User.Role.valueOf(dto.getRole())); // Ensure role exists in Enum
		user.setPasswordHash(passwordEncoder.encode(dto.getPasswordHash())); // Directly use the password without
																				// encoding

		User saved = userRepository.save(user);
		return mapToDTO(saved);
	}

	// Update User (Update)
	public UserDTO updateUser(String userId, UserDTO dto) {
		Optional<User> optionalUser = userRepository.findById(userId);
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			user.setName(dto.getName());
			user.setEmail(dto.getEmail());
			user.setPasswordHash(dto.getPasswordHash());
			user.setContactNumber(dto.getContactNumber());
			user.setRole(User.Role.valueOf(dto.getRole())); // Ensure role exists in Enum

			// Update password only if it's provided
			if (dto.getPasswordHash() != null && !dto.getPasswordHash().isEmpty()) {
				user.setPasswordHash(dto.getPasswordHash()); // Directly use the new password without encoding
			}

			User updated = userRepository.save(user);
			return mapToDTO(updated);
		}
		return null; // User not found, return null
	}

	// Get all users
	public List<UserDTO> getAllUsers() {
		return userRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	// Get user by ID
	public Optional<UserDTO> getUserById(String userId) {
		return userRepository.findById(userId).map(this::mapToDTO);
	}

	// Delete user
	public void deleteUser(String userId) {
		userRepository.deleteById(userId);
	}

	// Utility method to convert entity to DTO
	private UserDTO mapToDTO(User user) {
		UserDTO dto = new UserDTO();
		dto.setUserId(user.getUserId());
		dto.setName(user.getName());
		dto.setEmail(user.getEmail());
		dto.setContactNumber(user.getContactNumber());
		dto.setRole(user.getRole().toString()); // Convert Enum to String
		dto.setCreatedAt(user.getCreatedAt());

		// Add this line to include the password hash in the DTO
		dto.setPasswordHash(user.getPasswordHash());

		return dto;
	}

}
