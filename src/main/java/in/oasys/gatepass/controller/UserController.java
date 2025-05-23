package in.oasys.gatepass.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.oasys.gatepass.dto.UserDTO;
import in.oasys.gatepass.entity.User;
import in.oasys.gatepass.service.UserService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/change-password")
	public ResponseEntity<String> changePassword(@RequestBody UserDTO request) {
		String result = userService.changePassword(request);
		if (result.equals("Password changed successfully.")) {
			return ResponseEntity.ok(result);
		} else {
			return ResponseEntity.badRequest().body(result);
		}
	}

	@PostMapping("/checklogin")
	public ResponseEntity<UserDTO> checklogin(@RequestBody UserDTO request) {
		User result = userService.checklogin(request);
		if (result != null) {
			UserDTO dto = userService.getUserById(result.getUserId())
					.orElseThrow(() -> new RuntimeException("User not found after login"));
			return ResponseEntity.ok(dto);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}

	// Create User
	@PostMapping("/createuser")
	public ResponseEntity<UserDTO> saveUser(@RequestBody UserDTO userDTO) {
		UserDTO savedUser = userService.saveUser(userDTO);
		return ResponseEntity.ok(savedUser);
	}

	// Update User
	@PutMapping("/updateuser/{userId}")
	public ResponseEntity<UserDTO> updateUser(@PathVariable String userId, @RequestBody UserDTO userDTO) {
		UserDTO updatedUser = userService.updateUser(userId, userDTO);
		if (updatedUser != null) {
			return ResponseEntity.ok(updatedUser);
		}
		return ResponseEntity.notFound().build();
	}

	// Get All Users
	@GetMapping("/getallusers")
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		List<UserDTO> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}

	// Get User By ID
	@GetMapping("/getbyid/{userId}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable String userId) {
		Optional<UserDTO> user = userService.getUserById(userId);
		return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	// Delete User
	@DeleteMapping("/deleteuserbyid/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable String userId) {
		userService.deleteUser(userId);
		return ResponseEntity.ok("User deleted successfully.");
	}

}
