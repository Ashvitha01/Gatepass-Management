package in.oasys.gatepass.controller;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import com.lowagie.text.List;

import in.oasys.gatepass.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/admin")
public class ReportController {

	@Autowired
	ReportService reportService;

	@GetMapping("/gatepass-usage")
	public String generateGatePassReport(@RequestParam(required = false) LocalDate startDate,
			@RequestParam(required = false) LocalDate endDate, @RequestParam(required = false) String exitReason,
			@RequestParam String format) throws IOException {

		// Call service to generate the report
		return reportService.generateGatePassReport(startDate, endDate, exitReason, format);
	}

	@GetMapping("/securityincidentreport")
	public String generatesecurityincidentreport(

			@RequestParam String format) throws IOException {

		// Call service to generate the report
		return reportService.generatesecurityincidentreport(format);
	}

	// Endpoint 1: Export student exit report (PDF or CSV)
	@GetMapping("/export-student-exit-report")
	public void exportStudentExitReport(@RequestParam(defaultValue = "pdf") String format, HttpServletResponse response)
			throws IOException {
		// Set appropriate content type
		if (format.equalsIgnoreCase("pdf")) {
			response.setContentType(MediaType.APPLICATION_PDF_VALUE);
			response.setHeader("Content-Disposition", "attachment; filename=student_exit_report.pdf");
		} else if (format.equalsIgnoreCase("csv")) {
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; filename=student_exit_report.csv");
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid format. Use 'pdf' or 'csv'.");
			return;
		}

		reportService.exportStudentExitReport(response, format);
	}

}
