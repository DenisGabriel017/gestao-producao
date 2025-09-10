package br.com.dnsoftware.gestao_producao.controller;

import br.com.dnsoftware.gestao_producao.model.Sector;
import br.com.dnsoftware.gestao_producao.projections.ProductionSummary;
import br.com.dnsoftware.gestao_producao.service.ProductionService;
import br.com.dnsoftware.gestao_producao.service.SectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/")
public class DashboardController {

    @Autowired
    private SectorService sectorService;

    @Autowired
    private ProductionService productionService;

    @GetMapping
    public String showDashboard(Model model) {
        List<Sector> sectors = sectorService.findAll();
        model.addAttribute("sectors", sectors);
        return "dashboard";
    }

    @GetMapping("/api/chart-data")
    @ResponseBody
    public List<ProductionSummary> getChartData(@RequestParam(required = false) String sector,
                                                @RequestParam(required = false) String month,
                                                @RequestParam(required = false) String year) {
        return productionService.getProductionSummaryFiltered(sector, month, year);
    }

    @GetMapping("/api/years")
    @ResponseBody
    public List<String> getYears() {
        return productionService.findDistinctYears();
    }
}