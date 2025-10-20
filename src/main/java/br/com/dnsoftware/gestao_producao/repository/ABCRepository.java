package br.com.dnsoftware.gestao_producao.repository;

import br.com.dnsoftware.gestao_producao.model.ABC;
import br.com.dnsoftware.gestao_producao.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ABCRepository extends JpaRepository<ABC, Long> {

    @Modifying
    @Query("DELETE FROM ABC a WHERE a.saleDate >= :startDate AND a.saleDate < :endDatePlusOneDay")
    void deleteByDateRange(@Param("startDate") LocalDate startDate, @Param("endDatePlusOneDay") LocalDate endDatePlusOneDay);

    @Query("SELECT abc FROM ABC abc WHERE " +
            "(:keyword IS NULL OR LOWER(abc.product.name) LIKE :keyword OR LOWER(abc.product.code) LIKE :keyword) " +
            "AND (:sectorName IS NULL OR abc.product.sector.name = :sectorName)")
    List<ABC> findFilteredAbcData(@Param("keyword") String keyword, @Param("sectorName") String sectorName);

    @Query("SELECT DISTINCT a.product.sector.name FROM ABC a")
    List<String> findDistinctSectors();

    Optional<ABC> findByProductAndSaleDate(Product product, LocalDate saleDate);

    @Query("SELECT SUM(a.soldUnits) FROM ABC a WHERE a.saleDate BETWEEN :startDate AND :endDate")
    Optional<Long> sumSoldUnitsByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
