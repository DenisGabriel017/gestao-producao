package br.com.dnsoftware.gestao_producao.controller;

import br.com.dnsoftware.gestao_producao.model.Production;
import br.com.dnsoftware.gestao_producao.service.ProductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/production")
public class ProductionController {

    @Autowired
    private ProductionService productionService;

    @GetMapping
    public String listProduction(Model model){
        List<Production> productionList = productionService.findAll();
        model.addAttribute("productionList", productionList);
        return "production-list";
    }

    @PostMapping("/save")
    public String saveProduction(@RequestParam Long productId,
                                 @RequestParam Integer producedUnits,
                                 @RequestParam(required = false) Integer wasteUnits,
                                 @RequestParam(required = false) Integer realOutlets,
                                 @RequestParam(required = false) Double totalWeightKg){
        Production production = new Production();
        production.setProducedUnits(producedUnits);
        production.setWasteUnits(wasteUnits);
        production.setRealOutlets(realOutlets);
        production.setTotalWeightKg(totalWeightKg);
        production.setProductionDate(LocalDate.now());

        productionService.save(production);

        return "redirect:/production";
    }

}
