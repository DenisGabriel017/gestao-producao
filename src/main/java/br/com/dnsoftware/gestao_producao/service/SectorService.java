package br.com.dnsoftware.gestao_producao.service;


import br.com.dnsoftware.gestao_producao.model.Sector;
import br.com.dnsoftware.gestao_producao.repository.SectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SectorService {

    @Autowired
    private SectorRepository sectorRepository;

    public List<Sector> findAll(){
        return sectorRepository.findAll();
    }

    public Sector save(Sector sector){
        return sectorRepository.save(sector);
    }

    public Sector findOrCreateByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        Optional<Sector> existingSector = sectorRepository.findByName(name.trim());
        if (existingSector.isPresent()) {
            return existingSector.get();
        } else {
            Sector newSector = new Sector();
            newSector.setName(name.trim());
            return sectorRepository.save(newSector);
        }
    }
}
