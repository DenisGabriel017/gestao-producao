package br.com.dnsoftware.gestao_producao.controller;

import br.com.dnsoftware.gestao_producao.model.CanceledWeighing;
import br.com.dnsoftware.gestao_producao.service.CanceledWeighingService;
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
@RequestMapping("/cancellations")
public class CanceledWeighinController {

    @Autowired
    private CanceledWeighingService canceledWeighingService;

    @GetMapping
    public String listCancellations(Model model){
        List<CanceledWeighing> canceledWeighingList = canceledWeighingService.findAll();
        model.addAttribute("cancellationList", canceledWeighingList);

        return "cancellations-list";
    }

    @PostMapping("/save")
    public String saveCanceledWeighing(@RequestParam Long originalRecordId,
                                       @RequestParam Double canceledWeightKg,
                                       @RequestParam String cancellationReason){

        CanceledWeighing canceledWeighing = new CanceledWeighing();
        canceledWeighing.setOriginalRecordId(originalRecordId);
        canceledWeighing.setCanceledWeightKg(canceledWeightKg);
        canceledWeighing.setCancellationReason(cancellationReason);
        canceledWeighing.setCancellationDate(LocalDateTime.now());

        canceledWeighingService.save(canceledWeighing);

        return "redirect:/cancellations";
    }

}
