package in.oasys.gatepass.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import lombok.extern.log4j.Log4j2;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin/users")
@Log4j2
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
	public ResponseEntity<?> checklogin(@RequestBody UserDTO request) {
	    try {
	        User user = userService.checklogin(request);

	        if (user == null) {
	            Map<String, String> error = new HashMap<>();
	            error.put("message", "Invalid user ID or password.");
	            return ResponseEntity.status(401).body(error);
	        }

	        UserDTO dto = userService.getUserById(user.getUserId())
	                .orElseThrow(() -> new RuntimeException("User not found after login"));

	        return ResponseEntity.ok(dto);

	    } catch (Exception e) {
	        Map<String, String> error = new HashMap<>();
	        error.put("message", "Login failed: " + e.getMessage());
	        return ResponseEntity.status(500).body(error);
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
