package org.example;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.element.LineSeparator;

import java.time.LocalDate;

public class BillGenerator {

    public void generateBill(String filePath, String billingAddress, String shippingAddress, String[][] items,
                             double subtotal, double cgst, double sgst, double discount,
                             double totalAmount, double receivedAmount, double dueAmount) {
        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Set global font size for the document
            document.setFontSize(9);

            // Add Header
            document.add(new Paragraph("Prem Vishal Enterprises")
                    .setBold()
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Address: Bakerganj Patna\nPhone: +91 8646586940\nGSTIN: 567890-nnbb")
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));


// Add Horizontal Line
            document.add(new LineSeparator(new SolidLine()));

            document.add(new Paragraph("\n"));

            // Add Invoice Details
            document.add(new Paragraph("TAX INVOICE")
                    .setBold()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("Invoice No: INV-" + System.currentTimeMillis())
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("Invoice Date: " + LocalDate.now())
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("\n"));

            // Add Billing and Shipping Addresses
            Table addressTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            addressTable.addCell(new Cell().add(new Paragraph("Name of customer, Phone Number and Billing Address:")).setBold().setFontSize(9));
            addressTable.addCell(new Cell().add(new Paragraph("Name of customer, Phone Number and Shipping Address:")).setBold().setFontSize(9));
            addressTable.addCell(new Cell().add(new Paragraph(billingAddress).setFontSize(8)));
            addressTable.addCell(new Cell().add(new Paragraph(shippingAddress).setFontSize(8)));
            document.add(addressTable);
            document.add(new Paragraph("\n"));

            // Add Items Table
            Table itemTable = new Table(new float[]{1, 4, 2, 2, 2}).useAllAvailableWidth();
            itemTable.addHeaderCell(new Cell().add(new Paragraph("ID").setBold().setFontSize(9)));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("Name").setBold().setFontSize(9)));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("Quantity").setBold().setFontSize(9)));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("Price").setBold().setFontSize(9)));
            itemTable.addHeaderCell(new Cell().add(new Paragraph("Total").setBold().setFontSize(9)));

            // Add items to the table
            for (String[] item : items) {
                itemTable.addCell(new Cell().add(new Paragraph(item[0]).setFontSize(8))); // ID
                itemTable.addCell(new Cell().add(new Paragraph(item[1]).setFontSize(8))); // Name
                itemTable.addCell(new Cell().add(new Paragraph(item[2]).setFontSize(8))); // Quantity
                itemTable.addCell(new Cell().add(new Paragraph(item[3]).setFontSize(8))); // Price
                itemTable.addCell(new Cell().add(new Paragraph(item[4]).setFontSize(8))); // Total
            }
            document.add(itemTable);

            document.add(new Paragraph("\n"));
// Add Horizontal Line
            document.add(new LineSeparator(new SolidLine()));

            document.add(new Paragraph("\n"));

            // Add Totals
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(String.format("Subtotal: ₹%.2f", subtotal))
                    .setTextAlignment(TextAlignment.RIGHT).setFontSize(9));
            document.add(new Paragraph(String.format("CGST (9.0%%): ₹%.2f", cgst))
                    .setTextAlignment(TextAlignment.RIGHT).setFontSize(9));
            document.add(new Paragraph(String.format("SGST (9.0%%): ₹%.2f", sgst))
                    .setTextAlignment(TextAlignment.RIGHT).setFontSize(9));
            document.add(new Paragraph(String.format("Discount: ₹%.2f", discount))
                    .setTextAlignment(TextAlignment.RIGHT).setFontSize(9));
            document.add(new Paragraph(String.format("Total Amount: ₹%.2f", totalAmount))
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT).setFontSize(10));
            document.add(new Paragraph(String.format("Received Amount: ₹%.2f", receivedAmount))
                    .setTextAlignment(TextAlignment.RIGHT).setFontSize(9));
            document.add(new Paragraph(String.format("Due Amount: ₹%.2f", dueAmount))
                    .setBold()
                    .setTextAlignment(TextAlignment.RIGHT).setFontSize(10));
            document.add(new Paragraph("\n"));

            // Add Terms and Conditions
            document.add(new Paragraph("Terms and Conditions:")
                    .setBold().setFontSize(9));
            document.add(new Paragraph("1. Goods once sold will not be returned.").setFontSize(8));
            document.add(new Paragraph("2. Payment should be made within 15 days.").setFontSize(8));
            document.add(new Paragraph("3. All disputes are subject to Patna jurisdiction.").setFontSize(8));
            document.add(new Paragraph("\n"));

            // Add Signatures
            Table signatureTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            signatureTable.addCell(new Cell().add(new Paragraph("Customer Signature").setFontSize(8)).setTextAlignment(TextAlignment.LEFT));
            signatureTable.addCell(new Cell().add(new Paragraph("Authorized Signatory\nPrem Vishal Enterprises").setFontSize(8)).setTextAlignment(TextAlignment.RIGHT));
            document.add(signatureTable);

            document.close();
            System.out.println("Bill generated successfully at: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}