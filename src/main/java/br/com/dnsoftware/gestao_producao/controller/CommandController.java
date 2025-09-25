package br.com.dnsoftware.gestao_producao.controller;

import br.com.dnsoftware.gestao_producao.model.Command;
import br.com.dnsoftware.gestao_producao.model.Product;
import br.com.dnsoftware.gestao_producao.model.User;
import br.com.dnsoftware.gestao_producao.service.CommandService;
import br.com.dnsoftware.gestao_producao.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/comandas")
public class CommandController {

    @Autowired
    private CommandService commandService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String listCommands(Model model) {
        List<Command> commandList = commandService.findAll();
        List<Product> productList = productService.findAll();
        model.addAttribute("commandList", commandList);
        model.addAttribute("productList", productList);
        model.addAttribute("command", new Command());
        return "command-list";
    }

    @PostMapping("/save")
    public String saveCommand(@RequestParam Long productId,
                              @RequestParam Integer quantity,
                              @RequestParam Integer commandNumber) {

        Optional<Product> optionalProduct = productService.findById(productId);
        if (optionalProduct.isEmpty()) {
            return "redirect:/comandas";
        }

        Command command = new Command();
        command.setProduct(optionalProduct.get());
        command.setQuantity(quantity);
        command.setConsumptionDate(LocalDate.now());
        command.setCommandNumber(commandNumber);

        commandService.save(command);

        return "redirect:/comandas";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Optional<Command> optionalCommand = commandService.findById(id);
        if (optionalCommand.isEmpty()) {
            return "redirect:/comandas";
        }
        model.addAttribute("command", optionalCommand.get());
        model.addAttribute("productList", productService.findAll());
        return "edit-command";
    }

    @GetMapping("/delete/{id}")
    public String deleteCommand(@PathVariable("id") Long id) {
        commandService.deleteById(id);
        return "redirect:/comandas";
    }

    @PostMapping("/update")
    public String updateCommand(@RequestParam Long id,
                                @RequestParam Long productId,
                                @RequestParam Integer quantity,
                                @RequestParam Integer commandNumber){
        Optional<Command> optionalCommand = commandService.findById(id);
        Optional<Product> optionalProduct = productService.findById(productId);

        if (optionalCommand.isPresent() && optionalProduct.isPresent()){
            Command command = optionalCommand.get();
            command.setProduct(optionalProduct.get());
            command.setQuantity(quantity);
            command.setCommandNumber(commandNumber);
            commandService.save(command);
        }
        return "redirect:/comandas";
    }

    @PostMapping("/upload/comandas")
    public String uploadComandas(@RequestParam("file") MultipartFile file,
                                 @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                 @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                 RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Por favor, selecione um arquivo para upload.");
            return "redirect:/comandas";
        }
        try {

            List<String> errorMessages = commandService.importComandasData(file, startDate, endDate);

            if (errorMessages.isEmpty()) {
                redirectAttributes.addFlashAttribute("successMessage", "Dados de comandas importados com sucesso!");
            } else {
                String fullErrorMessage = "A importação foi concluída, mas com os seguintes erros:<br>" +
                        String.join("<br>", errorMessages);
                redirectAttributes.addFlashAttribute("errorMessage", fullErrorMessage);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao importar o arquivo de comandas: " + e.getMessage());
        }
        return "redirect:/comandas";
    }
}