package br.com.dnsoftware.gestao_producao.service;

import br.com.dnsoftware.gestao_producao.model.CanceledWeighing;
import br.com.dnsoftware.gestao_producao.repository.CanceledWeighingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CanceledWeighingService {

    @Autowired
    private CanceledWeighingRepository canceledWeighingRepository;

    public List<CanceledWeighing> findAll() {
        return canceledWeighingRepository.findAll();
    }

    public Optional<CanceledWeighing> findById(Long id) {
        return canceledWeighingRepository.findById(id);
    }

    public CanceledWeighing save(CanceledWeighing canceledWeighing) {
        return canceledWeighingRepository.save(canceledWeighing);
    }

    public void deleteById(Long id) {
        canceledWeighingRepository.deleteById(id);
    }
}
