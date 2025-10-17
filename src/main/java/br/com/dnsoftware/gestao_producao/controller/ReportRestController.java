package br.com.dnsoftware.gestao_producao.controller;

import br.com.dnsoftware.gestao_producao.dto.PerformanceReportDTO;
import br.com.dnsoftware.gestao_producao.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/commands")
public class ReportRestController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/report")
    public ResponseEntity<List<PerformanceReportDTO>> getPerformanceReport(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
                                                                           @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        if (startDate == null || endDate == null || startDate.isAfter(endDate)){
            return ResponseEntity.badRequest().build();
        }

        List<PerformanceReportDTO> report = reportService.generatePerformanceReport(startDate, endDate);

        return ResponseEntity.ok(report);
    }

}
