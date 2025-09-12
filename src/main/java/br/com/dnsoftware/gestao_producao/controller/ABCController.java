package br.com.dnsoftware.gestao_producao.controller;

import br.com.dnsoftware.gestao_producao.model.ABC;
import br.com.dnsoftware.gestao_producao.model.Product;
import br.com.dnsoftware.gestao_producao.service.ABCService;
import br.com.dnsoftware.gestao_producao.service.ExcelService;
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

    @Autowired
    private ExcelService excelService;

    @GetMapping
    public String listABC(Model model) {
        List<ABC> abcList = abcService.findALL();
        List<Product> productList = productService.findAll();
        model.addAttribute("abcList", abcList);
        model.addAttribute("productList", productList);
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
            excelService.importAbcData(file, startDate, endDate);
            redirectAttributes.addFlashAttribute("successMessage", "Dados da Curva ABC importados com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao importar o arquivo: " + e.getMessage());
        }
        return "redirect:/abc";
    }
}