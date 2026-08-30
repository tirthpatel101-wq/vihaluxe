package com.vihaluxe.controller;

import com.itextpdf.text.Image;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.vihaluxe.model.Order;
import com.vihaluxe.model.OrderItem;
import com.vihaluxe.repository.OrderRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

@Controller
public class InvoiceController {

    private final OrderRepository orderRepository;

    public InvoiceController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/invoice/{id}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id)
            throws Exception {

        Order order = orderRepository.findById(id).orElse(null);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document();

        PdfWriter.getInstance(document, out);

        document.open();

        Font title =
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);

        document.add(new Paragraph("Viha Luxe Invoice", title));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Invoice ID : " + order.getId()));
        document.add(new Paragraph("Date : " + order.getOrderDate()));
        document.add(new Paragraph("Customer : " + order.getUser().getFullName()));
        document.add(new Paragraph("Status : " + order.getStatus()));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("-------------------------------------------"));
        document.add(new Paragraph("Products"));
        document.add(new Paragraph("-------------------------------------------"));

        for (OrderItem item : order.getItems()) {

            if (item.getProduct() != null) {

                document.add(new Paragraph(
                        item.getProduct().getName()
                                + "   x"
                                + item.getQuantity()
                                + "   ₹"
                                + item.getPrice()
                ));

            } else {

                document.add(new Paragraph("🕯️ Custom Luxury Candle"));

                try {

                    String imagePath = "src/main/resources/static/images/"
                            + item.getCustomCandle().getImagePath();

                    Image image = Image.getInstance(imagePath);

                    image.scaleToFit(120, 120);

                    image.setAlignment(Image.ALIGN_CENTER);

                    document.add(image);

                } catch (Exception e) {

                    document.add(new Paragraph("Image not available"));

                }

                document.add(new Paragraph(
                        "Fragrance : " + item.getCustomCandle().getFragrance()));

                document.add(new Paragraph(
                        "Jar : " + item.getCustomCandle().getJar()));

                document.add(new Paragraph(
                        "Wax : " + item.getCustomCandle().getWax()));

                document.add(new Paragraph(
                        "Color : " + item.getCustomCandle().getColor()));

                document.add(new Paragraph(
                        "Size : " + item.getCustomCandle().getSize()));

                document.add(new Paragraph(
                        "Message : " + item.getCustomCandle().getPersonalizedMessage()));

                document.add(new Paragraph(
                        "Quantity : " + item.getQuantity()));

                document.add(new Paragraph(
                        "Price : ₹" + item.getPrice()));

                document.add(new Paragraph("-------------------------------------------"));
            }
        }

        document.add(new Paragraph(" "));
        document.add(new Paragraph("-------------------------------------------"));
        document.add(new Paragraph("Total : ₹" + order.getTotalAmount()));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Shipping Address"));
        document.add(new Paragraph(order.getShippingAddress()));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Thank you for shopping with Viha Luxe ❤️"));

        document.close();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Invoice-" + order.getId() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(out.toByteArray());
    }
}