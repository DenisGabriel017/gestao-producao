package br.com.dnsoftware.gestao_producao.service;

import br.com.dnsoftware.gestao_producao.dto.PerformanceReportDTO;
import br.com.dnsoftware.gestao_producao.model.CommandType;
import br.com.dnsoftware.gestao_producao.projections.CommandReportProjection;
import br.com.dnsoftware.gestao_producao.projections.ProductionReportProjection;
import br.com.dnsoftware.gestao_producao.repository.ABCRepository;
import br.com.dnsoftware.gestao_producao.repository.CanceledWeighingRepository;
import br.com.dnsoftware.gestao_producao.repository.CommandRepository;
import br.com.dnsoftware.gestao_producao.repository.ProductionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ProductionRepository productionRepository;

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private ABCRepository abcRepository;

    @Autowired
    private CanceledWeighingRepository canceledWeighingRepository;

    public Map<String, Object> getDashboardReport(String yearStr, String monthStr) {
        LocalDate startDate;
        LocalDate endDate;

        try {
            int year = (yearStr != null && !yearStr.isEmpty()) ? Integer.parseInt(yearStr) : YearMonth.now().getYear();
            int month = 0;
            if (monthStr != null && !monthStr.isEmpty()) {
                String cleanedMonth = monthStr.replaceAll("[^0-9]", "");
                if (!cleanedMonth.isEmpty()) {
                    month = Integer.parseInt(cleanedMonth);
                }
            }

            if (month >= 1 && month <= 12) {
                YearMonth yearMonth = YearMonth.of(year, month);
                startDate = yearMonth.atDay(1);
                endDate = yearMonth.atEndOfMonth();
            } else {
                startDate = LocalDate.of(year, 1, 1);
                endDate = LocalDate.of(year, 12, 31);
            }
        } catch (Exception e) {
            YearMonth currentYearMonth = YearMonth.now();
            startDate = currentYearMonth.atDay(1);
            endDate = currentYearMonth.atEndOfMonth();
        }

        // Safely unpack Optionals and handle specific data types (Double vs Long)
        double totalProduction = productionRepository.sumTotalWeightKgByPeriod(startDate, endDate).orElse(0.0);
        double totalCanceledWeight = canceledWeighingRepository.sumCanceledWeightKgByPeriod(startDate, endDate).orElse(0.0);

        // Correctly handle Optional<Long> and convert to double
        double totalSales = abcRepository.sumSoldUnitsByPeriod(startDate, endDate)
                                     .map(Long::doubleValue)
                                     .orElse(0.0);

        double totalDiscard = commandRepository.sumQuantityByCommandTypeAndPeriod(CommandType.DESPERDICIO_916, startDate, endDate)
                                             .orElse(0.0);

        List<CommandType> excludedTypes = Arrays.asList(CommandType.DESPERDICIO_916, CommandType.TRANSFORMACAO_921);
        double totalInternalConsumption = commandRepository.sumQuantityByCommandTypeNotInAndPeriod(excludedTypes, startDate, endDate)
                                                       .orElse(0.0);

        double totalOutput = totalSales + totalInternalConsumption + totalDiscard;
        double gap = totalProduction - totalOutput;
        double gapPercentage = (totalProduction > 0) ? (gap / totalProduction) * 100 : 0;

        Map<String, Object> reportData = new HashMap<>();
        reportData.put("totalProduction", totalProduction);
        reportData.put("totalOutput", totalOutput);
        reportData.put("totalSales", totalSales);
        reportData.put("totalInternalConsumption", totalInternalConsumption);
        reportData.put("totalDiscard", totalDiscard);
        reportData.put("totalCanceledWeight", totalCanceledWeight);
        reportData.put("gap", gap);
        reportData.put("gapPercentage", String.format("%.2f", gapPercentage));

        return reportData;
    }

    public List<PerformanceReportDTO> generatePerformanceReport(LocalDate startDate, LocalDate endDate) {

        List<ProductionReportProjection> productionData = productionRepository.findProductionReportByPeriod(startDate, endDate);
        List<CommandReportProjection> commandData = commandRepository.findCommandReportByPeriod(startDate, endDate);

        Map<String, PerformanceReportDTO> reportMap = productionData.stream()
                .collect(Collectors.toMap(ProductionReportProjection::getProductName, p -> {
                    PerformanceReportDTO dto = new PerformanceReportDTO(p.getProductName());
                    dto.setTotalProduced(p.getTotalProduced() != null ? p.getTotalProduced() : 0.0);
                    return dto;
                }));


        commandData.forEach(c -> {
            PerformanceReportDTO dto = reportMap.computeIfAbsent(c.getProductName(), PerformanceReportDTO::new);
            dto.setTotalIdaBuffet(c.getTotalIdaBuffet() != null ? c.getTotalIdaBuffet() : 0.0);
            dto.setTotalVoltaBuffet(c.getTotalVoltaBuffet() != null ? c.getTotalVoltaBuffet() : 0.0);
            dto.setTotalDesperdicio(c.getTotalDesperdicio() != null ? c.getTotalDesperdicio() : 0.0);
            dto.setTotalOutrosUsosPessoais(c.getTotalOutrosUsosPessoais() != null ? c.getTotalOutrosUsosPessoais() : 0.0);
            dto.setTotalEmpresa908(c.getTotalEmpresa908() != null ? c.getTotalEmpresa908() : 0.0);
            dto.setTotalEmpresa909(c.getTotalEmpresa909() != null ? c.getTotalEmpresa909() : 0.0);
            dto.setTotalTransformacao(c.getTotalTransformacao() != null ? c.getTotalTransformacao() : 0.0);
        });

        reportMap.values().forEach(dto -> {
            double gapBuffet = dto.getTotalIdaBuffet() - dto.getTotalVoltaBuffet();
            dto.setGapBuffet(gapBuffet);

            double totalSaidas = gapBuffet
                    + dto.getTotalOutrosUsosPessoais()
                    + dto.getTotalDesperdicio()
                    + dto.getTotalEmpresa908()
                    + dto.getTotalEmpresa909()
                    + dto.getTotalTransformacao();
            dto.setTotalSaidas(totalSaidas);

            double gapFinal = dto.getTotalProduced() - totalSaidas;
            dto.setGapFinal(gapFinal);
        });

        return new ArrayList<>(reportMap.values());
    }

}
