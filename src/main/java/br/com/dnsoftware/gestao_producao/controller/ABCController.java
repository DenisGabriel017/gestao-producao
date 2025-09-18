package br.com.dnsoftware.gestao_producao.controller;

import br.com.dnsoftware.gestao_producao.model.ABC;
import br.com.dnsoftware.gestao_producao.model.Product;
import br.com.dnsoftware.gestao_producao.service.ABCService;
import br.com.dnsoftware.gestao_producao.service.ProductService;
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
@RequestMapping("/abc")
public class ABCController {

    @Autowired
    private ABCService abcService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String listABC(Model model,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String sector) {

        List<ABC> abcList = abcService.findFilteredAbcData(keyword, sector);
        List<Product> productList = productService.findAll();
        List<String> sectors = abcService.findDistinctSectors(); // Fixed method name

        model.addAttribute("abcList", abcList);
        model.addAttribute("productList", productList);
        model.addAttribute("sectors", sectors);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedSector", sector);
        return "abc-list";
    }

    @PostMapping("/save")
    public String saveABC(@RequestParam Long productId, @RequestParam Integer soldUnits) {
        Optional<Product> optionalProduct = productService.findById(productId);
        if (optionalProduct.isEmpty()) {
            return "redirect:/abc";
        }

        ABC abc = new ABC();
        abc.setProduct(optionalProduct.get());
        abc.setSoldUnits(soldUnits);
        abc.setSaleDate(LocalDate.now());

        abcService.save(abc);
        return "redirect:/abc";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Optional<ABC> optionalABC = abcService.findById(id);
        if (optionalABC.isEmpty()) {
            return "redirect:/abc";
        }
        model.addAttribute("abc", optionalABC.get());
        model.addAttribute("productList", productService.findAll());
        return "edit-abc";
    }

    @PostMapping("/update")
    public String updateABC(@RequestParam Long id, @RequestParam Long productId, @RequestParam Integer soldUnits) {
        Optional<ABC> optionalABC = abcService.findById(id);
        Optional<Product> optionalProduct = productService.findById(productId);

        if (optionalABC.isPresent() && optionalProduct.isPresent()) {
            ABC abc = optionalABC.get();
            abc.setProduct(optionalProduct.get());
            abc.setSoldUnits(soldUnits);
            abcService.save(abc);
        }
        return "redirect:/abc";
    }

    @GetMapping("/delete/{id}")
    public String deleteABC(@PathVariable("id") Long id) {
        abcService.deleteById(id);
        return "redirect:/abc";
    }

    @PostMapping("/upload")
    public String uploadAbcData(@RequestParam("file") MultipartFile file,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Por favor, selecione um arquivo para upload.");
            return "redirect:/abc";
        }
        try {
            List<String> errorMessages = abcService.importAbcData(file, startDate, endDate);

            if (errorMessages.isEmpty()) {
                redirectAttributes.addFlashAttribute("successMessage", "Dados da Curva ABC importados com sucesso!");
            } else {
                String fullErrorMessage = "A importação foi concluída, mas com os seguintes erros:<br>" +
                        String.join("<br>", errorMessages);
                redirectAttributes.addFlashAttribute("errorMessage", fullErrorMessage);
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado ao importar o arquivo: " + e.getMessage());
        }
        return "redirect:/abc";
    }

    @PostMapping("/deleteByRange")
    public String deleteByDateRange(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                    @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                    RedirectAttributes redirectAttributes){
        try{
            abcService.deleteByDateRange(startDate,endDate);
            redirectAttributes.addFlashAttribute("SucessMessage", "Dados do período de " + startDate + " a " + endDate + " foram excluídos com sucesso!");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao tentar excluir os dados: " + e.getMessage());
        }
        return "redirect:/abc";
    }
}