package br.com.dnsoftware.gestao_producao.service;

import br.com.dnsoftware.gestao_producao.model.ABC;
import br.com.dnsoftware.gestao_producao.model.Product;
import br.com.dnsoftware.gestao_producao.repository.ABCRepository;
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

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ABCService {

    @Autowired
    private ABCRepository abcRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<ABC> findALL() {
        return abcRepository.findAll();
    }

    public Optional<ABC> findById(Long id) {
        return abcRepository.findById(id);
    }

    public ABC save(ABC abc) {
        return abcRepository.save(abc);
    }

    public List<ABC> saveAll(List<ABC> abcList) {
        return abcRepository.saveAll(abcList);
    }

    public void deleteById(Long id) {
        abcRepository.deleteById(id);
    }

    @Transactional
    public void deleteByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDate adjustedEndDate = endDate.plusDays(1);
        abcRepository.deleteByDateRange(startDate, adjustedEndDate);
    }

    public List<String> findDistinctSectors() {
        return abcRepository.findDistinctSectors();
    }

    @Transactional
    public List<String> importAbcData(MultipartFile file, LocalDate startDate, LocalDate endDate) throws IOException {
        List<String> errorMessages = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                String code = dataFormatter.formatCellValue(row.getCell(0));
                String soldUnitsStr = dataFormatter.formatCellValue(row.getCell(2)).replace(",", ".");

                if (code.contains(",")) {
                    code = code.replace(",", "");
                }

                if (code.isEmpty() || soldUnitsStr.isEmpty()) {
                    errorMessages.add("Linha " + (row.getRowNum() + 1) + ": Código ou unidades vendidas estão vazios e foram ignorados.");
                    continue;
                }

                Optional<Product> optionalProduct = productRepository.findByCode(code);
                if (optionalProduct.isEmpty()) {
                    errorMessages.add("Linha " + (row.getRowNum() + 1) + ": Produto com código '" + code + "' não encontrado. Registro ignorado.");
                    continue;
                }

                try {
                    Product product = optionalProduct.get();
                    Double soldUnits = Double.parseDouble(soldUnitsStr);

                    Optional<ABC> existingAbc = abcRepository.findByProductAndSaleDate(product, endDate);

                    if (existingAbc.isPresent()) {
                        ABC abc = existingAbc.get();
                        abc.setSoldUnits(soldUnits);
                        abcRepository.save(abc);
                    } else {
                        ABC abc = new ABC();
                        abc.setProduct(product);
                        abc.setSoldUnits(soldUnits);
                        abc.setSaleDate(endDate);
                        abcRepository.save(abc);
                    }

                } catch (NumberFormatException e) {
                    errorMessages.add("Linha " + (row.getRowNum() + 1) + ": O valor de unidades vendidas '" + soldUnitsStr + "' não é um número válido. Registro ignorado.");
                }
            }
        }
        return errorMessages;
    }

    public List<ABC> findFilteredAbcData(String keyword, String sectorName) {
        String finalKeyword = (keyword != null && !keyword.isEmpty()) ? "%" + keyword.toLowerCase() + "%" : null;
        String finalSectorName = (sectorName != null && !sectorName.isEmpty()) ? sectorName.toLowerCase() : null;
        return abcRepository.findFilteredAbcData(finalKeyword, finalSectorName);
    }
}