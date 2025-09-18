package br.com.dnsoftware.gestao_producao.controller;

import br.com.dnsoftware.gestao_producao.model.Product;
import br.com.dnsoftware.gestao_producao.model.Production;
import br.com.dnsoftware.gestao_producao.service.ProductService;
import br.com.dnsoftware.gestao_producao.service.ProductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/production")
public class ProductionController {

    @Autowired
    private ProductionService productionService;

    @Autowired
    private ProductService productService;


    @GetMapping
    public String listProduction(Model model){
        List<Production> productionList = productionService.findAll();
        List<Product> productList = productService.findAll();
        model.addAttribute("productionList", productionList);
        model.addAttribute("productList", productList);
        model.addAttribute("production", new Production());
        return "production-list";
    }

    @PostMapping("/save")
    public String saveProduction(@RequestParam Long productId,
                                 @RequestParam Double producedUnits,
                                 @RequestParam(required = false) Double totalWeightKg){

        Optional<Product> optionalProduct = productService.findById(productId);
        if (optionalProduct.isEmpty()){
            return "redirect:/production";
        }

        Production production = new Production();
        production.setProduct(optionalProduct.get());
        production.setProducedUnits(producedUnits);
        production.setTotalWeightKg(totalWeightKg);
        production.setProductionDate(LocalDate.now());

        productionService.save(production);

        return "redirect:/production";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Optional<Production> optionalProduction = productionService.findById(id);
        if (optionalProduction.isEmpty()) {
            return "redirect:/production";
        }
        model.addAttribute("production", optionalProduction.get());
        model.addAttribute("productList", productService.findAll());
        return "edit-production";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduction(@PathVariable("id") Long id) {
        productionService.deleteById(id);
        return "redirect:/production";
    }

    @PostMapping("/upload/production")
    public String uploadProductionFile(@RequestParam("file") MultipartFile file,
                                       @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                       RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Por favor, selecione um arquivo para upload.");
            return "redirect:/production";
        }
        try {
            productionService.importProductionData(file, startDate);
            redirectAttributes.addFlashAttribute("successMessage", "Dados de produção importados com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao importar o arquivo de produção: " + e.getMessage());
        }
        return "redirect:/production";
    }

}