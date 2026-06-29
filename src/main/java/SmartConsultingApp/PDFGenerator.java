package SmartConsultingApp;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PDFGenerator {

    // ✅ Method to generate Invoice PDF
    public static void generateInvoice(String patientName, String doctorName, String date, String amount) {
        try {
            java.io.File dir = new java.io.File("documents");
            if (!dir.exists()) dir.mkdirs();
            String fileName = "documents/Invoice_" + getTimestamp() + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

            // Title
            Paragraph title = new Paragraph("Smart Consulting App\nInvoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Invoice Details
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.addCell(getCell("Patient Name:", headerFont));
            table.addCell(getCell(patientName, normalFont));
            table.addCell(getCell("Doctor Name:", headerFont));
            table.addCell(getCell(doctorName, normalFont));
            table.addCell(getCell("Appointment Date:", headerFont));
            table.addCell(getCell(date, normalFont));
            table.addCell(getCell("Total Amount:", headerFont));
            table.addCell(getCell("₹" + amount, normalFont));

            document.add(table);
            document.add(new Paragraph("\nThank you for choosing Smart Consulting App!", normalFont));
            document.close();

            System.out.println("✅ Invoice PDF generated successfully: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Method to generate Prescription PDF
    public static void generatePrescription(String patientName, String doctorName, String medicineDetails, String dosage) {
        try {
            java.io.File dir = new java.io.File("documents");
            if (!dir.exists()) dir.mkdirs();
            String fileName = "documents/Prescription_" + getTimestamp() + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

            // Title
            Paragraph title = new Paragraph("Smart Consulting App\nPrescription", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Prescription Details
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.addCell(getCell("Patient Name:", headerFont));
            table.addCell(getCell(patientName, normalFont));
            table.addCell(getCell("Doctor Name:", headerFont));
            table.addCell(getCell(doctorName, normalFont));
            table.addCell(getCell("Medicine Details:", headerFont));
            table.addCell(getCell(medicineDetails, normalFont));
            table.addCell(getCell("Dosage:", headerFont));
            table.addCell(getCell(dosage, normalFont));

            document.add(table);
            document.add(new Paragraph("\nPlease follow your doctor's instructions.", normalFont));
            document.close();

            System.out.println("✅ Prescription PDF generated successfully: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Method to generate Health Report PDF
    public static void generateHealthReport(String patientName, String age, String gender, String weight, String height, String medicalHistory) {
        try {
            java.io.File dir = new java.io.File("documents");
            if (!dir.exists()) dir.mkdirs();
            String fileName = "documents/HealthReport_" + getTimestamp() + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);

            // Title
            Paragraph title = new Paragraph("Smart Consulting App\nHealth Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            // Health Report Details
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.addCell(getCell("Patient Name:", headerFont));
            table.addCell(getCell(patientName, normalFont));
            table.addCell(getCell("Age:", headerFont));
            table.addCell(getCell(age, normalFont));
            table.addCell(getCell("Gender:", headerFont));
            table.addCell(getCell(gender, normalFont));
            table.addCell(getCell("Weight:", headerFont));
            table.addCell(getCell(weight + " kg", normalFont));
            table.addCell(getCell("Height:", headerFont));
            table.addCell(getCell(height + " cm", normalFont));

            // Medical History Section
            document.add(table);
            document.add(new Paragraph("\nMedical History:", headerFont));
            document.add(new Paragraph(medicalHistory, normalFont));
            document.add(new Paragraph("\nStay healthy and take care!", normalFont));
            document.close();

            System.out.println("✅ Health Report PDF generated successfully: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Method to generate formatted table cell
    private static PdfPCell getCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        return cell;
    }

    // ✅ Method to generate unique timestamp
    private static String getTimestamp() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    public static void main(String[] args) {
        new PDFGenerator();
    }
}