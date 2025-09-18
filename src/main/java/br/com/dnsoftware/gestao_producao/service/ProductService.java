package br.com.dnsoftware.gestao_producao.service;

import br.com.dnsoftware.gestao_producao.model.Product;
import br.com.dnsoftware.gestao_producao.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll(){
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id){
        return productRepository.findById(id);
    }

    @Transactional
    public Product save(Product product){
        return productRepository.save(product);
    }

    public void deleteById(Long id){
        productRepository.deleteById(id);
    }


    public Optional<Product> findByName(String name){
        return productRepository.findByName(name);
    }

    public Optional<Product> findByCode(String code){
        if (code == null || code.trim().isEmpty()){
            return  Optional.empty();
        }
        return productRepository.findByCode(code.trim());
    }

    @Transactional
    public void deleteAllProducts(){
        productRepository.deleteAllInBatch();
    }

    public List<Product> findFilteredProducts(String keyword,String sectorName){
       return productRepository.findFilteredProducts(keyword, sectorName);
    }

    @Transactional
    public  void importFromExcel(MultipartFile file) throws IOException{
        try(Workbook workbook = new XSSFWorkbook(file.getInputStream())){
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            for (Row row : sheet){
                if (row.getRowNum() == 0){
                    continue;
                }
                String code = dataFormatter.formatCellValue(row.getCell(0));
                String name = dataFormatter.formatCellValue(row.getCell(1));
                String unit = dataFormatter.formatCellValue(row.getCell(2));
                String salePriceStr= dataFormatter.formatCellValue(row.getCell(3)).replace(",", ".");
                String sector = dataFormatter.formatCellValue(row.getCell(4));

                if (!StringUtils.hasText(code) || !StringUtils.hasText(name)){
                    continue;
                }

                Optional<Product> existingProduct = productRepository.findByCode(code);
                Product product;

                if (existingProduct.isPresent()){
                    product = existingProduct.get();
                    product.setName(name);
                    product.setSector(sector);
                    product.setUnit(unit);
                }else{
                    product = new Product();
                    product.setCode(code);
                    product.setName(name);
                    product.setSector(sector);
                    product.setUnit(unit);
                }
                try {
                    product.setSalePrice(Double.parseDouble(salePriceStr));
                }catch (NumberFormatException e){
                    product.setSalePrice(0.0);
                }
                productRepository.save(product);
            }
        }
    }




}