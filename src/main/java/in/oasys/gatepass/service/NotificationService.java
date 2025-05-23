package in.oasys.gatepass.service;

import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class NotificationService {

	@Autowired
	JavaMailSender emailSender;

	@Value("${staff.email}")
	private String staffemail;

	@Value("${security.email}")
	private String securityemail;

	@Value("${admin.email}")
	private String adminemail;

	// Notify student for approval when the staff aproved the gate pass

	public void sendApprovalNotification(String email, String qrCodePath) throws MessagingException {

		if (email == null || email.isEmpty()) {
			throw new MessagingException("Student email is missing.");
		}

		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(email);
		helper.setSubject("Gate Pass Approved");
		helper.setText("Your Gate Pass has been approved. Please find your QR Code attached.");

		// Attach the QR Code Image
		FileSystemResource file = new FileSystemResource(new File(qrCodePath));
		helper.addAttachment("GatePassQRCode.png", file);

		emailSender.send(message);
		System.out.println("✅ Email with QR Code sent successfully to " + email);
	}

	// Notify student of rejection by the staff to student

	public void sendRejectionNotification(String email, String remarks) {
		// TODO Auto-generated method stub
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("Gate Pass Rejected");
		message.setText("Your Gate Pass request has been rejected. Reason: " + remarks);
		emailSender.send(message);

	}

	// Notify Security about Emergency Pass
	public void sendSecurityNotification(String securityEmail, String name, String reason) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(securityEmail);
		message.setSubject("Emergency Gate Pass Request");
		message.setText("Emergency Pass Requested by Student: " + name + ".\nReason: " + reason);
		emailSender.send(message);
	}

//notify only for admin
	public void notifyAdmin(String message) {
		sendEmailNotification(adminemail, " Admin Alert: Suspicious Activity", message);
	}

//notify only for staff
	public void notifyStaff(String message) {
		sendEmailNotification(staffemail, " Staff Alert: Suspicious Activity", message);
	}

// notify for both admin and staff
	public void notifyMultiple(String message) {
		String[] recipients = { adminemail, staffemail };
		sendEmailNotifications(recipients, " Security Alert", message);
	}

	// notify for both admin and staff
	public void notifySecurityAndStaff(String message) {
		String[] recipients = { securityemail, staffemail };
		sendEmergencyEmailNotifications(recipients, " Security Alert", message);
	}

// email notification for only a admin or staff
	private void sendEmailNotification(String to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		emailSender.send(message);
	}

// email notification for both admin and staff
	private void sendEmailNotifications(String[] to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		emailSender.send(message);
	}

	// email notification for both Security and staff
	private void sendEmergencyEmailNotifications(String[] to, String subject, String body) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		emailSender.send(message);
	}
}
