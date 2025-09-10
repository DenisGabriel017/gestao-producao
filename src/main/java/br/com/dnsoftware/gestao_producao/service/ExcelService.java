package br.com.dnsoftware.gestao_producao.service;

import br.com.dnsoftware.gestao_producao.dto.CommandDTO;
import br.com.dnsoftware.gestao_producao.dto.ProductionDTO;
import br.com.dnsoftware.gestao_producao.model.Command;
import br.com.dnsoftware.gestao_producao.model.Product;
import br.com.dnsoftware.gestao_producao.model.Production;
import br.com.dnsoftware.gestao_producao.model.User;
import br.com.dnsoftware.gestao_producao.repository.ProductRepository;
import br.com.dnsoftware.gestao_producao.repository.ProductionRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ExcelService {

    @Autowired
    private ProductionRepository productionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CommandService commandService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductionService productionService;

    public void processProductionReport(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);


            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar o arquivo Excel: " + e.getMessage());
        }
    }

    public void processComandaReport(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);



            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar o arquivo Excel: " + e.getMessage());
        }
    }
    public void importProductionData(MultipartFile file) {
        if (file.isEmpty()){
            return;
        }
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))){
            CsvToBean<ProductionDTO> csvToBean = new CsvToBeanBuilder<ProductionDTO>(reader)
                    .withType(ProductionDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<ProductionDTO> productionDTOS = csvToBean.parse();

            for (ProductionDTO dto : productionDTOS){
                Optional<Product> optionalProduct = productService.findByName(dto.getProductName());

                if (optionalProduct.isPresent()){
                    Production production = new Production();
                    production.setProduct(optionalProduct.get());
                    production.setProductionDate(LocalDate.now());
                    production.setProducedUnits(dto.getProducedUnits());
                    production.setConsumedUnits(dto.getConsumedUnits());
                    production.setWasteUnits(dto.getWasteUnits());
                    production.setTotalWeightKg(dto.getTotalWeightKg());
                    production.setRealOutlets(0);

                    productionService.save(production);
                }else {
                    System.err.println("Produto não encontrado: " + dto.getProductName());
                }
            }
        }catch (Exception e){
            System.err.println("Erro ao importar dados de produção: " + e.getMessage());
        }
    }

    public void importComandasData(MultipartFile file) {
        if (file.isEmpty()){
            return;
        }
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<CommandDTO> csvToBean = new CsvToBeanBuilder<CommandDTO>(reader)
                    .withType(CommandDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<CommandDTO> commandDTOS = csvToBean.parse();

            for (CommandDTO dto : commandDTOS) {
                Optional<Product> optionalProduct = productService.findByName(dto.getProductName());
                Optional<User> optionalUser = userService.findByUsername(dto.getUserName());

                if (optionalProduct.isPresent() && optionalUser.isPresent()) {
                    Command command = new Command();
                    command.setProduct(optionalProduct.get());
                    command.setQuantity(dto.getCommandQuantity());
                    command.setConsumptionDate(LocalDateTime.now());
                    command.setUser(optionalUser.get());

                    commandService.save(command);
                } else {
                    System.err.println("Produto ou Usuario não encontrado para a comanda: " + dto.getProductName);
                }
            }
        }catch (Exception e){
            System.err.println("Erro ao importar dados de comandas: " + e.getMessage());
        }
    }
}