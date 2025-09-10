package br.com.dnsoftware.gestao_producao.controller;

import br.com.dnsoftware.gestao_producao.model.CanceledWeighing;
import br.com.dnsoftware.gestao_producao.service.CanceledWeighingService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/cancellations")
public class CanceledWeighingController {

    @Autowired
    private CanceledWeighingService canceledWeighingService;

    @GetMapping
    public String listCancellations(Model model){
        List<CanceledWeighing> canceledWeighingList = canceledWeighingService.findAll();
        model.addAttribute("cancellationList", canceledWeighingList);
        return "cancellation-list";
    }

    @PostMapping("/save")
    public String saveCanceledWeighing(@RequestParam Long originalRecordId,
                                       @RequestParam Double canceledWeightKg,
                                       @RequestParam(required = false) String cancellationReason){

        CanceledWeighing canceledWeighing = new CanceledWeighing();
        canceledWeighing.setOriginalRecordId(originalRecordId);
        canceledWeighing.setCanceledWeightKg(canceledWeightKg);
        canceledWeighing.setCancellationReason(cancellationReason);
        canceledWeighing.setCancellationDate(LocalDateTime.now()); // Campo corrigido

        canceledWeighingService.save(canceledWeighing);

        return "redirect:/cancellations";
    }

    @GetMapping("/delete/{id}")
    public String deleteCanceledWeighing(@PathVariable("id") Long id){
        canceledWeighingService.deleteById(id);
        return "redirect:/cancellations";
    }

}