package ro.ugal.autopartsstore.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ro.ugal.autopartsstore.dto.report.SalesByCategoryRow;
import ro.ugal.autopartsstore.dto.report.SalesSummaryResponse;
import ro.ugal.autopartsstore.service.ReportsService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final ReportsService service;

    public ReportsController(ReportsService service) {
        this.service = service;
    }

    @GetMapping("/sales-summary")
    public SalesSummaryResponse salesSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return service.salesSummary(from, to);
    }

    @GetMapping("/sales-by-category")
    public List<SalesByCategoryRow> salesByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return service.salesByCategory(from, to);
    }

    @GetMapping("/sales-by-category.xlsx")
    public ResponseEntity<byte[]> exportSalesByCategoryXlsx(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) throws Exception {
        byte[] bytes = service.exportSalesByCategoryXlsx(from, to);

        String filename = "sales_by_category_" + from.toLocalDate() + "_to_" + to.toLocalDate() + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(bytes);
    }

    @GetMapping("/sales-by-category.pdf")
    public ResponseEntity<byte[]> exportSalesByCategoryPdf(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) throws Exception {
        byte[] bytes = service.exportSalesByCategoryPdf(from, to);

        String filename = "sales_by_category_" + from.toLocalDate() + "_to_" + to.toLocalDate() + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

}
