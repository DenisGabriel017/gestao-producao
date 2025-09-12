package br.com.dnsoftware.gestao_producao.service;

import br.com.dnsoftware.gestao_producao.dto.ABC_DTO;
import br.com.dnsoftware.gestao_producao.dto.CommandDTO;
import br.com.dnsoftware.gestao_producao.dto.ProductionDTO;
import br.com.dnsoftware.gestao_producao.model.*;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ExcelService {

    @Autowired
    private ProductionService productionService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CommandService commandService;

    @Autowired
    private UserService userService;

    @Autowired
    private ABCService abcService;

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
        if (file.isEmpty()) {
            return;
        }
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<ProductionDTO> csvToBean = new CsvToBeanBuilder<ProductionDTO>(reader)
                    .withType(ProductionDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            csvToBean.parse().forEach(dto -> {
                Optional<Product> optionalProduct = productService.findByName(dto.getProductName());

                if (optionalProduct.isPresent()) {
                    Production production = new Production();
                    production.setProduct(optionalProduct.get());
                    production.setProductionDate(LocalDate.now());
                    production.setProducedUnits(dto.getProducedUnits());
                    production.setConsumedUnits(dto.getConsumedUnits());
                    production.setWasteUnits(dto.getWasteUnits());
                    production.setTotalWeightKg(dto.getTotalWeightKg());
                    production.setRealOutlets(0);

                    productionService.save(production);
                } else {
                    System.err.println("Produto não encontrado: " + dto.getProductName());
                }
            });
        } catch (Exception e) {
            System.err.println("Erro ao importar dados de produção: " + e.getMessage());
        }
    }

    public void importComandasData(MultipartFile file) {
        if (file.isEmpty()) {
            return;
        }
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            CsvToBean<CommandDTO> csvToBean = new CsvToBeanBuilder<CommandDTO>(reader)
                    .withType(CommandDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            csvToBean.parse().forEach(dto -> {
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
                    System.err.println("Produto ou Usuario não encontrado para a comanda: " + dto.getProductName());
                }
            });
        } catch (Exception e) {
            System.err.println("Erro ao importar dados de comandas: " + e.getMessage());
        }
    }

    public void importAbcData(MultipartFile file, LocalDate startDate, LocalDate endDate){
        if (file.isEmpty()){
            return;
        }

        List<ABC_DTO> dtos = convertExcelToAbcDtos(file);

        System.out.println("DEBUG: Datas de importação - Início: " + startDate + ", Fim: " + endDate);

        abcService.deleteByDateRange(startDate,endDate);

        List<ABC> abcList = new ArrayList<>();

        for (ABC_DTO dto : dtos){
            Optional<Product> optionalProduct = productService.findByCode(dto.getProductCode());

            if (optionalProduct.isPresent()){
                ABC abc = new ABC();
                abc.setProduct(optionalProduct.get());
                abc.setSoldUnits(dto.getTotalProduced());
                abc.setSaleDate(LocalDate.now());

                abcList.add(abc);
            }else {
                System.err.println("Porduto não encontrado: " + dto.getProductCode());
            }
        }

        if (!abcList.isEmpty()) {
            abcService.saveAll(abcList);
        }
    }


    public List<ABC_DTO> convertExcelToAbcDtos(MultipartFile file){
        List<ABC_DTO> abcDtos = new ArrayList<>();
        DataFormatter dataFormatter= new DataFormatter();
        try (InputStream inputStream = file.getInputStream()){
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()){
                rowIterator.next();
            }

            while(rowIterator.hasNext()){
                Row currentRow = rowIterator.next();

                if (currentRow == null){
                    continue;
                }
                ABC_DTO dto = new ABC_DTO();

                Cell codeCell = currentRow.getCell(0);
                if (codeCell != null) {
                    dto.setProductCode(dataFormatter.formatCellValue(codeCell));
                }


                Cell nameCell = currentRow.getCell(1);
                if (nameCell != null) {
                    dto.setProductName(dataFormatter.formatCellValue(nameCell));
                }


                Cell totalProducedCell = currentRow.getCell(2);
                if (totalProducedCell != null && totalProducedCell.getCellType() == CellType.NUMERIC) {
                    dto.setTotalProduced(totalProducedCell.getNumericCellValue());
                } else {
                    dto.setTotalProduced(0.0);
                }

                Cell salePriceCell = currentRow.getCell(3);
                if (salePriceCell != null && salePriceCell.getCellType() == CellType.NUMERIC) {
                    dto.setSalePrice(salePriceCell.getNumericCellValue());
                } else {
                    dto.setSalePrice(0.0);
                }

                Cell totalRevenueCell = currentRow.getCell(4);
                if (totalRevenueCell != null && totalRevenueCell.getCellType() == CellType.NUMERIC) {
                    dto.setTotalRevenue(totalRevenueCell.getNumericCellValue());
                } else {
                    dto.setTotalRevenue(0.0);
                }

                    abcDtos.add(dto);

            }
            workbook.close();

        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException("Erro ao processar o arquivo Excel: " + e.getMessage());
        }
        return abcDtos;
    }


}