package br.com.dnsoftware.gestao_producao.service;


import br.com.dnsoftware.gestao_producao.dto.ProductDTO;
import br.com.dnsoftware.gestao_producao.model.Product;
import br.com.dnsoftware.gestao_producao.repository.ProductRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Service
public class CsvService {

    @Autowired
    private ProductRepository productRepository;

    public void processProducts(MultipartFile file) {
        try (Reader reader =  new BufferedReader(new InputStreamReader(file.getInputStream()))){
            CsvToBean<ProductDTO> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(ProductDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<ProductDTO> productDTOS = csvToBean.parse();

            for (ProductDTO dto: productDTOS){
                Product product = new Product();
                product.setCode(dto.getCode());
                product.setName(dto.getName());
                product.setSector(dto.getSetor());
                product.setUnit(dto.getUnit());
                product.setSalePrice(dto.getSalePrice());
                productRepository.save(product);
            }
        }catch (IOException e){
            throw new RuntimeException("Erro ao processar o arquivo CSV: " + e.getMessage());
        }

    }


}
