package br.com.dnsoftware.gestao_producao.service;

import br.com.dnsoftware.gestao_producao.model.ABC;
import br.com.dnsoftware.gestao_producao.repository.ABCRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ABCService {

    @Autowired
    private ABCRepository abcRepository;

    public List<ABC> findALL(){
        return  abcRepository.findAll();
    }

    public Optional<ABC> findById(Long id){
        return abcRepository.findById(id);
    }

    public ABC save(ABC abc){
        return abcRepository.save(abc);
    }

    public List<ABC> saveAll(List<ABC> abcList) {
        return abcRepository.saveAll(abcList);
    }

    public void deleteById(Long id){
        abcRepository.deleteById(id);
    }

    @Transactional
    public void deleteByDateRange(LocalDate startDate, LocalDate endDate){
        abcRepository.deleteBydateRange(startDate, endDate);
    }
}