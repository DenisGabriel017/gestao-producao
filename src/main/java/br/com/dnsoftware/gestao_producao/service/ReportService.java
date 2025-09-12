package br.com.dnsoftware.gestao_producao.service;

import br.com.dnsoftware.gestao_producao.model.ABC;
import br.com.dnsoftware.gestao_producao.model.CanceledWeighing;
import br.com.dnsoftware.gestao_producao.model.Command;
import br.com.dnsoftware.gestao_producao.model.Production;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ProductionService productionService;

    @Autowired
    private CommandService commandService;

    @Autowired
    private ABCService abcService;

    @Autowired
    private CanceledWeighingService canceledWeighingService;

    public Map<String, Object> getConsolidatedReport(String year, String month) {

        String filterString = (year != null && month != null) ? year + "-" + month.split("-")[1] : null;

        List<Production> filteredProduction = productionService.findAll().stream()
                .filter(p -> (year == null || p.getProductionDate().format(DateTimeFormatter.ofPattern("yyyy")).equals(year)) &&
                        (filterString == null || p.getProductionDate().format(DateTimeFormatter.ofPattern("yyyy-MM")).equals(filterString)))
                .collect(Collectors.toList());

        List<Command> filteredCommands = commandService.findAll().stream()
                .filter(c -> (year == null || c.getConsumptionDate().format(DateTimeFormatter.ofPattern("yyyy")).equals(year)) &&
                        (filterString == null || c.getConsumptionDate().format(DateTimeFormatter.ofPattern("yyyy-MM")).equals(filterString)))
                .collect(Collectors.toList());

        List<ABC> filteredABC = abcService.findALL().stream()
                .filter(a -> (year == null || a.getSaleDate().format(DateTimeFormatter.ofPattern("yyyy")).equals(year)) &&
                        (filterString == null || a.getSaleDate().format(DateTimeFormatter.ofPattern("yyyy-MM")).equals(filterString)))
                .collect(Collectors.toList());

        List<CanceledWeighing> filteredCanceledWeighings = canceledWeighingService.findAll().stream()
                .filter(cw -> (year == null || cw.getCancellationDate().format(DateTimeFormatter.ofPattern("yyyy")).equals(year)) &&
                        (filterString == null || cw.getCancellationDate().format(DateTimeFormatter.ofPattern("yyyy-MM")).equals(filterString)))
                .collect(Collectors.toList());

        double totalProduction = filteredProduction.stream()
                .mapToDouble(Production::getTotalWeightKg)
                .sum();

        double totalCanceledWeight = filteredCanceledWeighings.stream()
                .mapToDouble(CanceledWeighing::getCanceledWeightKg)
                .sum();

        double totalSales = filteredABC.stream()
                .mapToDouble(ABC::getSoldUnits)
                .sum();

        double totalDiscard = filteredCommands.stream()
                .filter(c -> c.getCommandNumber().equals(916))
                .mapToDouble(Command::getQuantity)
                .sum();

        double totalInternalConsumption = filteredCommands.stream()
                .filter(c -> !c.getCommandNumber().equals(916) && !c.getCommandNumber().equals(921))
                .mapToDouble(Command::getQuantity)
                .sum();


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
}