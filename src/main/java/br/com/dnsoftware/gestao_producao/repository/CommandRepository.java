package br.com.dnsoftware.gestao_producao.repository;

import br.com.dnsoftware.gestao_producao.dto.CommandHistoryDTO;
import br.com.dnsoftware.gestao_producao.model.Command;
import br.com.dnsoftware.gestao_producao.model.CommandType;
import br.com.dnsoftware.gestao_producao.model.Product;
import br.com.dnsoftware.gestao_producao.projections.CommandReportProjection;
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

    @Query("SELECT new br.com.dnsoftware.gestao_producao.dto.CommandHistoryDTO(c.consumptionDate, c.quantity, c.commandNumber) FROM Command c WHERE c.product.id = :productId ORDER BY c.consumptionDate DESC")
    List<CommandHistoryDTO> findCommandHistoryByProductId(@Param("productId") Long productId);

    @Query("SELECT " +
            "  p.name as productName, " +
            "  SUM(CASE WHEN c.commandType = 'IDA_BUFFET_914' THEN c.quantity ELSE 0.0 END) as totalIdaBuffet, " +
            "  SUM(CASE WHEN c.commandType = 'VOLTA_BUFFET_915' THEN c.quantity ELSE 0.0 END) as totalVoltaBuffet, " +
            "  SUM(CASE WHEN c.commandType = 'DESPERDICIO_916' THEN c.quantity ELSE 0.0 END) as totalDesperdicio, " +
            "  SUM(CASE WHEN c.commandType IN ('USO_PESSOAL_901', 'USO_PESSOAL_902', 'USO_PESSOAL_903', " +
            "'USO_PESSOAL_904', 'USO_PESSOAL_905', 'USO_PESSOAL_906', 'USO_PESSOAL_907', 'USO_PESSOAL_910') THEN c.quantity ELSE 0.0 END) as totalOutrosUsosPessoais, " +
            "  SUM(CASE WHEN c.commandType = 'CREPERIA_908' THEN c.quantity ELSE 0.0 END) as totalEmpresa908, " +
            "  SUM(CASE WHEN c.commandType = 'FAZENDA_909' THEN c.quantity ELSE 0.0 END) as totalEmpresa909, " +
            "  SUM(CASE WHEN c.commandType = 'TRANSFORMACAO_921' THEN c.quantity ELSE 0.0 END) as totalTransformacao " +
            "FROM Command c JOIN c.product p " +
            "WHERE c.consumptionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY p.name " +
            "ORDER BY p.name")
    List<CommandReportProjection> findCommandReportByPeriod(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(c.quantity) FROM Command c WHERE c.commandType = :commandType AND c.consumptionDate BETWEEN :startDate AND :endDate")
    Optional<Double> sumQuantityByCommandTypeAndPeriod(@Param("commandType") CommandType commandType, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(c.quantity) FROM Command c WHERE c.commandType NOT IN (:excludedTypes) AND c.consumptionDate BETWEEN :startDate AND :endDate")
    Optional<Double> sumQuantityByCommandTypeNotInAndPeriod(@Param("excludedTypes") List<CommandType> excludedTypes, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
