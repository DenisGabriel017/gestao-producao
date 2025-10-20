package br.com.dnsoftware.gestao_producao.repository;

import br.com.dnsoftware.gestao_producao.model.Product;
import br.com.dnsoftware.gestao_producao.model.Production;
import br.com.dnsoftware.gestao_producao.projections.ProductionReportProjection;
import br.com.dnsoftware.gestao_producao.projections.ProductionSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductionRepository extends JpaRepository<Production, Long> {

    @Query("SELECT p.product.sector.name AS sector, SUM(p.producedUnits) AS producedUnits " +
            "FROM Production p " +
            "WHERE (:sector IS NULL OR p.product.sector.name = :sector) " +
            "AND (:month IS NULL OR FUNCTION('to_char', p.productionDate, 'YYYY-MM') = :month) " +
            "AND (:year IS NULL OR FUNCTION('to_char', p.productionDate, 'YYYY') = :year) " +
            "GROUP BY p.product.sector.name")
    List<ProductionSummary> getProductionSummaryFiltered(String sector, String month, String year);

    @Query("SELECT DISTINCT FUNCTION('to_char', p.productionDate, 'YYYY') FROM Production p ORDER BY 1 DESC")
    List<String> findDistinctYears();

    Optional<Production> findByProductAndProductionDate(Product product, LocalDate productionDate);

    @Query("SELECT p.product.name as productName, SUM(p.producedUnits) as totalProduced " +
           "FROM Production p " +
           "WHERE p.productionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY p.product.name")
    List<ProductionReportProjection> findProductionReportByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(p.totalWeightKg) FROM Production p WHERE p.productionDate BETWEEN :startDate AND :endDate")
    Optional<Double> sumTotalWeightKgByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}