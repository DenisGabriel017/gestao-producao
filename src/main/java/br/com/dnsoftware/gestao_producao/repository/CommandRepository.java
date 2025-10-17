package br.com.dnsoftware.gestao_producao.repository;

import br.com.dnsoftware.gestao_producao.dto.PerformanceReportDTO;
import br.com.dnsoftware.gestao_producao.model.Command;
import br.com.dnsoftware.gestao_producao.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {

    Optional<Command> findByProductAndConsumptionDate(Product product, LocalDate consuptionDate);

    @Query("SELECT new br.com.dnsoftware.gestao_producao.dto.PerformanceReportDTO(" +
            "  p.name, " +
            "  SUM(CASE WHEN c.commandType = 'PRODUCAO' THEN c.quantity * 1.0 ELSE 0.0 END), " +
            "  SUM(CASE WHEN c.commandType = 'IDA_BUFFET_914' THEN c.quantity * 1.0 ELSE 0.0 END), " +
            "  SUM(CASE WHEN c.commandType = 'VOLTA_BUFFET_915' THEN c.quantity * 1.0 ELSE 0.0 END), " +
            "  SUM(CASE WHEN c.commandType = 'DESPERDICIO_916' THEN c.quantity * 1.0 ELSE 0.0 END), " +
            "  SUM(CASE WHEN c.commandType = 'ETIQUETA_DESCARTADA' THEN c.quantity * 1.0 ELSE 0.0 END), " +
            "  SUM(CASE WHEN c.commandType IN ('USO_PESSOAL_901', 'USO_PESSOAL_902', 'USO_PESSOAL_903', " +
            "'USO_PESSOAL_904', 'USO_PESSOAL_905', 'USO_PESSOAL_906', 'USO_PESSOAL_907', 'USO_PESSOAL_910') THEN c.quantity * 1.0 ELSE 0.0 END), " +
            "  SUM(CASE WHEN c.commandType = 'CREPERIA_908' THEN c.quantity * 1.0 ELSE 0.0 END), " +
            "  SUM(CASE WHEN c.commandType = 'FAZENDA_909' THEN c.quantity * 1.0 ELSE 0.0 END) " +
            ") " +

            "FROM Command c JOIN c.product p " +

            "WHERE c.consumptionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY p.name " +
            "ORDER BY p.name")
    List<PerformanceReportDTO> findPerformanceReportByPeriod(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}



