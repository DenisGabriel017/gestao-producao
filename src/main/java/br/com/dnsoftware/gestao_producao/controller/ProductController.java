package br.com.dnsoftware.gestao_producao.controller;

import br.com.dnsoftware.gestao_producao.model.Product;
import br.com.dnsoftware.gestao_producao.model.Sector;
import br.com.dnsoftware.gestao_producao.service.ProductService;
import br.com.dnsoftware.gestao_producao.service.SectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private SectorService sectorService;

    @GetMapping
    public String listProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sector", required = false) String sector,
            @RequestParam(value = "unit", required = false) String unit,
            Model model) {

        List<Product> productList = productService.findFilteredProducts(keyword, sector, unit);

        model.addAttribute("productList", productList);
        model.addAttribute("sectors", sectorService.findAll());
        model.addAttribute("units", productService.findDistinctUnits()); // Adiciona a lista de unidades
        model.addAttribute("product", new Product());
        model.addAttribute("currentKeyword", keyword);
        model.addAttribute("currentSector", sector);
        model.addAttribute("currentUnit", unit); // Adiciona o filtro atual

        return "product-list";
    }

    @GetMapping("/sectors")
    public List<Sector> listAllSectors() {
        return sectorService.findAll();
    }

    @PostMapping("/save")
    public String saveProduct(Product product) {
        productService.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") long id, Model model) {
        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isPresent()) {
            model.addAttribute("product", optionalProduct.get());
        } else {
            return "redirect:/products";
        }
        model.addAttribute("sectors", sectorService.findAll());
        return "edit-product";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute("sucessMessage", "Produto excluído com sucesso!");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Não foi possível excluir o produto. Existem vendas ou registros de produção associados a ele.");
        }
        return "redirect:/products";
    }

    @PostMapping("/upload-excel")
    public String uploadExcel(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Por favor, selecione um arquivo para importar.");
            return "redirect:/products";
        }

        try {
            productService.importFromExcel(file);
            redirectAttributes.addFlashAttribute("successMessage", "Produtos importados via Excel com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao importar o arquivo Excel: " + e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/download-template")
    public ResponseEntity<String> downloadCsvTemplate() {
        String csvHeader = "code,name,sector,sale_price,unit";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"produtos_template.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvHeader);
    }

    @PostMapping("/sectors/save")
    public String saveSector(@RequestParam String name) {
        Sector sector = new Sector();
        sector.setName(name);
        sectorService.save(sector);
        return "redirect:/products";
    }

    @PostMapping("/clear-base")
    public String clearProductsDatabase(RedirectAttributes redirectAttributes) {
        try {
            productService.deleteAllProducts();
            redirectAttributes.addFlashAttribute("successMessage", "Produto excluído com sucesso!");

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Não foi possível apagar o banco de dados. Existem vendas ou produções vinculadas a eles");

        }
        return "redirect:/products";
    }

    @PostMapping("/delete-all")
    public String deleteAllProductsMvc(RedirectAttributes redirectAttributes) {
        try {
            productService.deleteAllProducts();

            redirectAttributes.addFlashAttribute("successMessage", "Todos os produtos foram excluídos com sucesso.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Não foi possível apagar os produtos. Existem registros vinculados.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro inesperado ao excluir produtos: " + e.getMessage());
        }
        return "redirect:/products";
    }

}