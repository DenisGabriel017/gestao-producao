package br.com.dnsoftware.gestao_producao.service;

import br.com.dnsoftware.gestao_producao.model.Production;
import br.com.dnsoftware.gestao_producao.model.Product;
import br.com.dnsoftware.gestao_producao.repository.ProductionRepository;
import br.com.dnsoftware.gestao_producao.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductionService {

    @Autowired
    private ProductionRepository productionRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Production> findAll(){
        return productionRepository.findAll();
    }

    public Optional<Production> findById(Long id){
        return productionRepository.findById(id);
    }

    @Transactional
    public Production save(Production production){
        return productionRepository.save(production);
    }

    public void deleteById(Long id){
        productionRepository.deleteById(id);
    }

    @Transactional
    public List<String> importProductionData(MultipartFile file, LocalDate startDate) throws IOException {
        List<String> errorMessages = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Pula a primeira linha (cabeçalho)
                }

                String code = dataFormatter.formatCellValue(row.getCell(0));
                if (code.contains(",")) {
                    code = code.replace(",", "");
                }

                String producedQuantityStr = dataFormatter.formatCellValue(row.getCell(1)).replace(",", ".");
                String dayFromExcelStr = dataFormatter.formatCellValue(row.getCell(2));

                if (!StringUtils.hasText(code) || !StringUtils.hasText(producedQuantityStr)) {
                    errorMessages.add("Linha " + (row.getRowNum() + 1) + ": Código ou quantidade produzida estão vazios e foram ignorados.");
                    continue;
                }

                Optional<Product> optionalProduct = productRepository.findByCode(code);
                if (optionalProduct.isEmpty()) {
                    errorMessages.add("Linha " + (row.getRowNum() + 1) + ": Produto com código '" + code + "' não encontrado. Registro ignorado.");
                    continue;
                }

                try {
                    Product product = optionalProduct.get();
                    Double producedQuantity = Double.parseDouble(producedQuantityStr);

                    int dayFromExcel = Integer.parseInt(dayFromExcelStr.replace(",", ""));

                    LocalDate productionDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), dayFromExcel);

                    Optional<Production> existingProduction = productionRepository.findByProductAndProductionDate(product, productionDate);

                    if (existingProduction.isPresent()) {
                        Production production = existingProduction.get();
                        production.setProducedUnits(producedQuantity);
                        productionRepository.save(production);
                    } else {
                        Production production = new Production();
                        production.setProduct(product);
                        production.setProducedUnits(producedQuantity);
                        production.setProductionDate(productionDate);
                        productionRepository.save(production);
                    }
                } catch (NumberFormatException | java.time.DateTimeException e) {
                    errorMessages.add("Linha " + (row.getRowNum() + 1) + ": Erro ao processar a data ou a quantidade. Verifique se a terceira coluna tem um dia válido e se a quantidade é um número.");
                }
            }
        }
        return errorMessages;
    }
    public List<String> findDistinctYears() {
        return productionRepository.findDistinctYears();
    }
}