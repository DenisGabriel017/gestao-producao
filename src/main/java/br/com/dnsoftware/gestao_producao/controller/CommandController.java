package br.com.dnsoftware.gestao_producao.controller;

import br.com.dnsoftware.gestao_producao.model.Command;
import br.com.dnsoftware.gestao_producao.service.CommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/commands")
public class CommandController {

    @Autowired
    private CommandService commandService;

    @GetMapping
    public String listCommand(Model model){
        List<Command> commandList = commandService.findAll();
        model.addAttribute("commandList", commandList);
        return "command-list";
    }

    @PostMapping("/save")
    public String saveCommand(@RequestParam Long productId,
                              @RequestParam Integer quantity){

        Command command = new Command();
        command.setQuantity(quantity);
        command.setConsumptionDate(LocalDateTime.now());

        commandService.save(command);

        return "redirect:/commands";
    }

}
