package br.com.dnsoftware.gestao_producao.service;

import br.com.dnsoftware.gestao_producao.model.Production;
import br.com.dnsoftware.gestao_producao.projections.ProductionSummary;
import br.com.dnsoftware.gestao_producao.repository.ProductionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductionService {

    @Autowired
    private ProductionRepository productionRepository;

    public List<Production> findAll() {
        return productionRepository.findAll();
    }

    public Optional<Production> findById(Long id) {
        return productionRepository.findById(id);
    }

    public Production save(Production production) {
        return productionRepository.save(production);
    }

    public void deleteById(Long id) {
        productionRepository.deleteById(id);
    }

    public List<ProductionSummary> getProductionSummaryFiltered(String sector, String month, String year) {
        List<ProductionSummary> result;
        if (sector != null && month != null && year != null) {
            result = productionRepository.getProductionSummaryBySectorAndMonthAndYear(sector, month, year);
        } else if (month != null && year != null) {
            result = productionRepository.getProductionSummaryByMonthAndYear(month, year);
        } else if (sector != null && year != null) {
            result = productionRepository.getProductionSummaryBySectorAndYear(sector, year);
        } else if (sector != null && month != null) {
            result = productionRepository.getProductionSummaryBySectorAndMonth(sector, month);
        } else if (year != null) {
            result = productionRepository.getProductionSummaryByYear(year);
        } else if (month != null) {
            result = productionRepository.getProductionSummaryByMonth(month);
        } else if (sector != null) {
            result = productionRepository.getProductionSummaryBySector(sector);
        } else {
            result = productionRepository.getProductionSummaryAll();
        }

        return result != null ? result : new ArrayList<>();
    }

    public List<String> findDistinctYears() {
        return productionRepository.findDistinctYears();
    }
}