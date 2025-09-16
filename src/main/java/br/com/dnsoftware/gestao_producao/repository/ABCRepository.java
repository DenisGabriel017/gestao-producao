package br.com.dnsoftware.gestao_producao.repository;

import br.com.dnsoftware.gestao_producao.model.ABC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ABCRepository extends JpaRepository<ABC, Long> {

    @Modifying
    @Query("DELETE FROM ABC a WHERE a.saleDate >= :startDate AND a.saleDate < :endDatePlusOneDay")
    void deleteByDateRange(@Param("startDate") LocalDate startDate, @Param("endDatePlusOneDay") LocalDate endDatePlusOneDay);

}
