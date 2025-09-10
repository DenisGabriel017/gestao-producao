package br.com.dnsoftware.gestao_producao.controller;

import br.com.dnsoftware.gestao_producao.model.Command;
import br.com.dnsoftware.gestao_producao.model.Product;
import br.com.dnsoftware.gestao_producao.model.User;
import br.com.dnsoftware.gestao_producao.service.CommandService;
import br.com.dnsoftware.gestao_producao.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/commands")
public class CommandController {

    @Autowired
    private CommandService commandService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public String listCommands(Model model){
        List<Command> commandList = commandService.findAll();
        List<Product> productList = productService.findAll();
        model.addAttribute("commandList", commandList);
        model.addAttribute("productList", productList);
        return "command-list";
    }

    @PostMapping("/save")
    public String saveCommand(@RequestParam Long productId,
                              @RequestParam Integer quantity){

        Optional<Product> optionalProduct = productService.findById(productId);
        if (optionalProduct.isEmpty()){
            return "redirect:/commands";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Command command = new Command();
        command.setProduct(optionalProduct.get());
        command.setQuantity(quantity);
        command.setConsumptionDate(LocalDateTime.now());

        commandService.save(command);

        return "redirect:/commands";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model){
        Optional<Command> optionalCommand = commandService.findById(id);
        if (optionalCommand.isEmpty()){
            return "redirect:/commands";
        }
        model.addAttribute("command", optionalCommand.get());
        model.addAttribute("productList", productService.findAll());
        return "edit-command";
    }

    @GetMapping("/delete/{id}")
    public String deleteCommand(@PathVariable("id") Long id){
        commandService.deleteById(id);
        return "redirect:/commands";
    }
}