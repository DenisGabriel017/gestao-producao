package br.com.dnsoftware.gestao_producao.controller;

import br.com.dnsoftware.gestao_producao.model.Product;
import br.com.dnsoftware.gestao_producao.model.Sector;
import br.com.dnsoftware.gestao_producao.service.CsvService;
import br.com.dnsoftware.gestao_producao.service.ProductService;
import br.com.dnsoftware.gestao_producao.service.SectorService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CsvService csvService;

    @Autowired
    private SectorService sectorService;

    @GetMapping
    public String listProducts(Model model){
        List<Product> products = productService.findAll();
        List<Sector> sectors = sectorService.findAll();
        model.addAttribute("products",products);
        model.addAttribute("sectors", sectors);
        return "product-list";
    }

    @PostMapping("/save")
    public String saveProduct(@RequestParam String code,
                              @RequestParam String name,
                              @RequestParam String sector,
                              @RequestParam Double salePrice){

        Product product = new Product();
        product.setCode(code);
        product.setName(name);
        product.setSector(sector);
        product.setSalePrice(salePrice);

        productService.save(product);

        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") long id, Model model){
        Optional<Product> optionalProduct = productService.findById(id);
        if (optionalProduct.isPresent()){
            model.addAttribute("product", optionalProduct.get());
        }else {
            return "redirect:/products";
        }
        model.addAttribute("sectors", sectorService.findAll());
        return "edit-product";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id){
        productService.deletedById(id);
        return "redirect:/products";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file){
        if (!file.isEmpty()){
            csvService.processProducts(file);
        }
        return "redirect:/products";
    }

    @GetMapping("/download-template")
    public ResponseEntity<String> downloadCsvTemplate(){
        String csvHeader = "codigo,nome,setor,preco_venda,unidade";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"produtos_template.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvHeader);
    }

}
