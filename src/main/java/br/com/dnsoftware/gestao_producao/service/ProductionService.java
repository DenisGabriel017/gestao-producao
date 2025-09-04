package br.com.dnsoftware.gestao_producao.service;

import br.com.dnsoftware.gestao_producao.model.Production;
import br.com.dnsoftware.gestao_producao.repository.ProductionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
