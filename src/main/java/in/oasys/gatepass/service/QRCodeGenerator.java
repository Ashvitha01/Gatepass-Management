package in.oasys.gatepass.service;

import java.io.File;
import java.nio.file.Path;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import in.oasys.gatepass.entity.User;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class QRCodeGenerator {

	public String generateQRCode(String qrContent) throws WriterException, IOException {
		int width = 300;
		int height = 300;

		// Define encoding settings
		Map<EncodeHintType, Object> hints = new HashMap<>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, width, height, hints);

		// Use safe filename (timestamp-based or UUID)
		String safeFileName = "qrcode_" + System.currentTimeMillis() + ".png";
		File directory = new File("C:/Users/Ashvitha/GatePassManagement/");
		if (!directory.exists()) {
			directory.mkdirs();
		}

		Path path = Paths.get(directory.getAbsolutePath(), safeFileName);
		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

		return path.toString(); // clean file path returned
	}

}
