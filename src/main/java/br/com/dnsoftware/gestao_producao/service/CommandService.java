package br.com.dnsoftware.gestao_producao.service;

import br.com.dnsoftware.gestao_producao.dto.CommandHistoryDTO;
import br.com.dnsoftware.gestao_producao.model.Command;
import br.com.dnsoftware.gestao_producao.model.Product;
import br.com.dnsoftware.gestao_producao.model.User;
import br.com.dnsoftware.gestao_producao.repository.CommandRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommandService {

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    public List<Command> findAll() {
        return commandRepository.findAll();
    }

    public Optional<Command> findById(Long id) {
        return commandRepository.findById(id);
    }

    @Transactional
    public Command save(Command command) {
        return commandRepository.save(command);
    }

    public void deleteById(Long id) {
        commandRepository.deleteById(id);
    }

    public List<CommandHistoryDTO> findCommandHistoryByProductId(Long productId) {
        return commandRepository.findCommandHistoryByProductId(productId);
    }

    @Transactional
    public List<String> importComandasData(MultipartFile file, LocalDate startDate, LocalDate endDate) throws IOException {
        List<String> errorMessages = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User currentUser = userService.findByUsername(username).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }

                String code = dataFormatter.formatCellValue(row.getCell(0));
                if (code.contains(",")) {
                    code = code.replace(",", "");
                }

                String quantityStr = dataFormatter.formatCellValue(row.getCell(1));
                String commandNumberStr = dataFormatter.formatCellValue(row.getCell(2));

                if (!StringUtils.hasText(code) || !StringUtils.hasText(quantityStr) || !StringUtils.hasText(commandNumberStr)) {
                    errorMessages.add("Linha " + (row.getRowNum() + 1) + ": Dados essenciais (código, quantidade, comanda) estão vazios e foram ignorados.");
                    continue;
                }

                Optional<Product> optionalProduct = productService.findByCode(code.trim());
                if (optionalProduct.isEmpty()) {
                    errorMessages.add("Linha " + (row.getRowNum() + 1) + ": Produto com código '" + code + "' não encontrado. Registro ignorado.");
                    continue;
                }

                try {
                    Product product = optionalProduct.get();
                    Integer quantity = Integer.parseInt(quantityStr.trim().replace(",", "."));
                    Integer commandNumber = Integer.parseInt(commandNumberStr.trim());


                    LocalDate consumptionDate = endDate;

                    Optional<Command> existingCommand = commandRepository.findByProductAndConsumptionDate(product, consumptionDate);

                    if (existingCommand.isPresent()) {
                        Command command = existingCommand.get();
                        command.setQuantity(quantity);
                        command.setCommandNumber(commandNumber);
                        commandRepository.save(command);
                    } else {
                        Command command = new Command();
                        command.setProduct(product);
                        command.setQuantity(quantity);
                        command.setConsumptionDate(consumptionDate);
                        command.setUser(currentUser);
                        command.setCommandNumber(commandNumber);
                        commandRepository.save(command);
                    }
                } catch (NumberFormatException e) {
                    errorMessages.add("Linha " + (row.getRowNum() + 1) + ": Erro ao processar a quantidade ou o número da comanda. Verifique se o formato dos dados está correto.");
                }
            }
        }
        return errorMessages;
    }
    
    @Transactional
    public void deleteAllCommands(){
        commandRepository.deleteAll();
    }
}