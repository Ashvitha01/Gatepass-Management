package in.oasys.gatepass.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import in.oasys.gatepass.entity.GatePassRequestEntity;
import in.oasys.gatepass.entity.SecurityIncident;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Component
public class ReportExporter {

	private static final String REPORTS_DIR = "reports/";

	// used to fetch the report based on the start and end date
	public String exportReport(List<GatePassRequestEntity> requests, String format) throws IOException {
		// Ensure the reports directory exists
		Files.createDirectories(Paths.get(REPORTS_DIR));

		String filePath = REPORTS_DIR + "gatepass_report_" + System.currentTimeMillis() + "." + format;

		if (format.equalsIgnoreCase("csv")) {
			exportToCSV(requests, filePath);
		} else if (format.equalsIgnoreCase("pdf")) {
			exportToPDF(requests, filePath);
		}

		return "Report saved at: " + filePath;
	}

	private void exportToCSV(List<GatePassRequestEntity> requests, String filePath) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath));
				CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
						.setHeader("ID", "Student ID", "Reason", "Email", "Event ID", "Date", "Entry Time", "Exit Time")
						.build())) {

			for (GatePassRequestEntity request : requests) {
				csvPrinter.printRecord(request.getRequestId(), request.getStudentid(), request.getReason(),
						request.getEmail(), request.getEventId(), request.getDate(), request.getEntrytime(),
						request.getExittime());
			}
		}

	}

	private void exportToPDF(List<GatePassRequestEntity> requests, String filePath) throws IOException {
		Document document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream(filePath));
			document.open();

			Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
			Paragraph title = new Paragraph("Gate Pass Report", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			PdfPTable table = new PdfPTable(3);
			table.setWidthPercentage(100);
			table.addCell("ID");
			table.addCell("Student ID");
			table.addCell("Reason");

			for (GatePassRequestEntity request : requests) {
				table.addCell(String.valueOf(request.getRequestId()));
				table.addCell(request.getStudentid().getUserId());
				table.addCell(request.getReason());
			}

			document.add(table);
		} catch (DocumentException e) {
			e.printStackTrace(); // Handle the exception properly (log it)
		} finally {

			document.close();
		}
	}

	// Security incidents

	public String SecurityReport(List<SecurityIncident> requests, String format) throws IOException {
		// Ensure the reports directory exists
		Files.createDirectories(Paths.get(REPORTS_DIR));

		String filePath = REPORTS_DIR + "gatepass_report_" + System.currentTimeMillis() + "." + format;

		if (format.equalsIgnoreCase("csv")) {
			exportToCSVSecurity(requests, filePath);
		} else if (format.equalsIgnoreCase("pdf")) {
			exportToPDFSecurity(requests, filePath);
		}

		return "Report saved at: " + filePath;
	}

	private void exportToCSVSecurity(List<SecurityIncident> requests, String filePath) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath));
				CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
						.setHeader("ID", "Student ID", "Description", "ReportedAt", " Status", "UserId").build())) {

			for (SecurityIncident request : requests) {
				csvPrinter.printRecord(request.getIncidentId(), request.getStudentid(), request.getDescription(),
						request.getReportedat(), request.getStatus(), request.getSecurityGuard()

				);
			}
		}

	}

	private void exportToPDFSecurity(List<SecurityIncident> requests, String filePath) throws IOException {
		Document document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream(filePath));
			document.open();

			Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
			Paragraph title = new Paragraph("Gate Pass Report", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			PdfPTable table = new PdfPTable(3);
			table.setWidthPercentage(100);
			table.addCell("ID");
			table.addCell("Student ID");
			table.addCell("Description");

			for (SecurityIncident request : requests) {
				table.addCell(String.valueOf(request.getIncidentId()));
				table.addCell(request.getStudentid().getUserId());
				table.addCell(request.getDescription());
			}

			document.add(table);
		} catch (DocumentException e) {
			e.printStackTrace(); // Handle the exception properly (log it)
		} finally {
			document.close();
		}
	}

	// generate report for the maximum exist
	public void generateReport(HttpServletResponse response, List<Map<String, Object>> data, String format)
			throws IOException {
		if ("csv".equalsIgnoreCase(format)) {
			exportToCSVListStudent(response, data);
		} else if ("pdf".equalsIgnoreCase(format)) {
			exportToPDFListStudent(response, data);
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid format. Use 'csv' or 'pdf'.");
		}
	}

	public static void exportToCSVListStudent(HttpServletResponse response, List<Map<String, Object>> data)
			throws IOException {
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=highest_exits_report.csv");

		PrintWriter writer = response.getWriter();
		writer.println("Student ID,Exit Count");

		for (Map<String, Object> row : data) {
			writer.println(row.get("Student ID") + "," + row.get("Exit Count"));
		}

		writer.flush();// sends all data to the output.
		writer.close();
	}

	public static void exportToPDFListStudent(HttpServletResponse response, List<Map<String, Object>> data)
			throws IOException {
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=highest_exits_report.pdf");

		Document document = new Document();
		ServletOutputStream outStream = response.getOutputStream();

		try {
			PdfWriter.getInstance(document, outStream);
			document.open();

			Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
			document.add(new Paragraph("Students with Highest Exits Report", titleFont));
			document.add(new Paragraph(" "));

			PdfPTable table = new PdfPTable(2);
			table.addCell("Student ID");
			table.addCell("Exit Count");

			for (Map<String, Object> row : data) {
				table.addCell(row.get("Student ID").toString());
				table.addCell(row.get("Exit Count").toString());
			}

			document.add(table);
			document.close();
			outStream.close();

		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
}
