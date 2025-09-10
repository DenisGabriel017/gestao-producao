package br.com.dnsoftware.gestao_producao.repository;

import br.com.dnsoftware.gestao_producao.model.Production;
import br.com.dnsoftware.gestao_producao.projections.ProductionSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionRepository extends JpaRepository<Production, Long> {




    // 1. Filtro por Setor, Mês e Ano
    @Query(value = "SELECT p.product.sector AS sector, SUM(p.producedUnits) AS producedUnits " +
            "FROM Production p " +
            "WHERE p.product.sector = :sector AND FUNCTION('to_char', p.productionDate, 'YYYY-MM') = :month AND FUNCTION('to_char', p.productionDate, 'YYYY') = :year " +
            "GROUP BY p.product.sector")
    List<ProductionSummary> getProductionSummaryBySectorAndMonthAndYear(@Param("sector") String sector, @Param("month") String month, @Param("year") String year);

    // 2. Filtro por Mês e Ano (Todos os Setores)
    @Query(value = "SELECT p.product.sector AS sector, SUM(p.producedUnits) AS producedUnits " +
            "FROM Production p " +
            "WHERE FUNCTION('to_char', p.productionDate, 'YYYY-MM') = :month AND FUNCTION('to_char', p.productionDate, 'YYYY') = :year " +
            "GROUP BY p.product.sector")
    List<ProductionSummary> getProductionSummaryByMonthAndYear(@Param("month") String month, @Param("year") String year);

    // 3. Filtro por Setor e Ano
    @Query(value = "SELECT p.product.sector AS sector, SUM(p.producedUnits) AS producedUnits " +
            "FROM Production p " +
            "WHERE p.product.sector = :sector AND FUNCTION('to_char', p.productionDate, 'YYYY') = :year " +
            "GROUP BY p.product.sector")
    List<ProductionSummary> getProductionSummaryBySectorAndYear(@Param("sector") String sector, @Param("year") String year);

    // 4. Filtro por Setor e Mês
    @Query(value = "SELECT p.product.sector AS sector, SUM(p.producedUnits) AS producedUnits " +
            "FROM Production p " +
            "WHERE p.product.sector = :sector AND FUNCTION('to_char', p.productionDate, 'YYYY-MM') = :month " +
            "GROUP BY p.product.sector")
    List<ProductionSummary> getProductionSummaryBySectorAndMonth(@Param("sector") String sector, @Param("month") String month);

    // 5. Apenas por Ano
    @Query(value = "SELECT p.product.sector AS sector, SUM(p.producedUnits) AS producedUnits " +
            "FROM Production p " +
            "WHERE FUNCTION('to_char', p.productionDate, 'YYYY') = :year " +
            "GROUP BY p.product.sector")
    List<ProductionSummary> getProductionSummaryByYear(@Param("year") String year);

    // 6. Apenas por Mês
    @Query(value = "SELECT p.product.sector AS sector, SUM(p.producedUnits) AS producedUnits " +
            "FROM Production p " +
            "WHERE FUNCTION('to_char', p.productionDate, 'YYYY-MM') = :month " +
            "GROUP BY p.product.sector")
    List<ProductionSummary> getProductionSummaryByMonth(@Param("month") String month);

    // 7. Apenas por Setor
    @Query(value = "SELECT p.product.sector AS sector, SUM(p.producedUnits) AS producedUnits " +
            "FROM Production p " +
            "WHERE p.product.sector = :sector " +
            "GROUP BY p.product.sector")
    List<ProductionSummary> getProductionSummaryBySector(@Param("sector") String sector);

    // 8. Sem filtros (Todos)
    @Query(value = "SELECT p.product.sector AS sector, SUM(p.producedUnits) AS producedUnits " +
            "FROM Production p " +
            "GROUP BY p.product.sector")
    List<ProductionSummary> getProductionSummaryAll();

    @Query("SELECT DISTINCT FUNCTION('to_char', p.productionDate, 'YYYY') FROM Production p ORDER BY 1 DESC")
    List<String> findDistinctYears();
}