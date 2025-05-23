package in.oasys.gatepass.service;

import in.oasys.gatepass.entity.GatePassRequestEntity;
import in.oasys.gatepass.entity.SecurityIncident;
import in.oasys.gatepass.entity.User;
import in.oasys.gatepass.repository.GatePassRequestRepository;
import in.oasys.gatepass.repository.SecurityIncidentRepository;
import in.oasys.gatepass.service.ReportExporter;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

	private final GatePassRequestRepository gatePassRequestRepository;
	private final ReportExporter reportExporter;
	@Autowired
	SecurityIncidentRepository securityincidentrepository;

	public ReportService(GatePassRequestRepository gatePassRequestRepository, ReportExporter reportExporter) {
		this.gatePassRequestRepository = gatePassRequestRepository;
		this.reportExporter = reportExporter;
	}

	public String generateGatePassReport(LocalDate startDate, LocalDate endDate, String exitReason, String format)
			throws IOException {
		// If no date range is provided, set a default range (e.g., last 30 days)
		if (startDate == null) {
			startDate = LocalDate.now().minusDays(30);
		}
		if (endDate == null) {
			endDate = LocalDate.now();
		}

		// If exitReason is not provided, use an empty string to match all reasons
		if (exitReason == null) {
			exitReason = "";
		}

		// Fetch gate pass requests based on filters
		List<GatePassRequestEntity> requests = gatePassRequestRepository.findByDateBetweenAndReasonContaining(startDate,
				endDate, exitReason);

		// If no data found, return an error message
		if (requests.isEmpty()) {
			return "No data found for the selected filters.";
		}

		// Generate report and return the file path
		return reportExporter.exportReport(requests, format);
	}

	public String generatesecurityincidentreport(String format) throws IOException {

		// Fetch gate pass requests based on filters
		List<SecurityIncident> security = securityincidentrepository.findAll();
		// If no data found, return an error message
		if (security.isEmpty()) {
			return "No data found for the selected filters.";
		}

		// Generate report and return the file path
		return reportExporter.SecurityReport(security, format);

	}

	public void exportStudentExitReport(HttpServletResponse response, String format) throws IOException {
		List<GatePassRequestEntity> completedExits = gatePassRequestRepository
				.findByStatus(GatePassRequestEntity.Status.COMPLETED);

		// Map to count exits per student
		Map<User, Long> exitCounts = new HashMap<>();

		for (GatePassRequestEntity request : completedExits) {
			User student = request.getStudentid();
			exitCounts.put(student, exitCounts.getOrDefault(student, 0L) + 1);
		}

		// Convert to List<Map<String, Object>> for exporting
		List<Map<String, Object>> data = new ArrayList<>();

		// Sort by highest exit count (manually)
		List<Map.Entry<User, Long>> sortedEntries = new ArrayList<>(exitCounts.entrySet());
		sortedEntries.sort((a, b) -> Long.compare(b.getValue(), a.getValue())); // Descending order

		for (Map.Entry<User, Long> entry : sortedEntries) {
			User student = entry.getKey();
			Long exitCount = entry.getValue();

			Map<String, Object> row = new LinkedHashMap<>();
			row.put("Student ID", student.getUserId());
			row.put("Student Name", student.getName());
			row.put("Exit Count", exitCount);

			data.add(row);
		}

		// Handle empty data
		if (data.isEmpty()) {
			response.sendError(HttpServletResponse.SC_NO_CONTENT, "No data found for the report.");
			return;
		}

		// Export the data using report exporter
		reportExporter.generateReport(response, data, format);
	}

}
