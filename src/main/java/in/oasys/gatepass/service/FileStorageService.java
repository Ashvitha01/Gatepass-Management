package in.oasys.gatepass.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
	private final String uploadDir = "uploads/"; // your file storage location

	// Method to store file and return the file path
	public String storeFile(MultipartFile file) {
		try {
			// Generate a unique file name
			String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
			Path filePath = Paths.get(uploadDir, fileName);

			// Create the directory if it doesn't exist
			Files.createDirectories(filePath.getParent());

			// Write the file bytes to the disk
			Files.write(filePath, file.getBytes());

			return filePath.toString(); // Return the file path
		} catch (IOException e) {
			throw new RuntimeException("File storage failed", e);
		}
	}
}
