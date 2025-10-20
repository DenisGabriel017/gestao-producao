package br.com.dnsoftware.gestao_producao.repository;

import br.com.dnsoftware.gestao_producao.model.CanceledWeighing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CanceledWeighingRepository extends JpaRepository<CanceledWeighing, Long> {

    @Query("SELECT SUM(c.canceledWeightKg) FROM CanceledWeighing c WHERE c.cancellationDate BETWEEN :startDate AND :endDate")
    Optional<Double> sumCanceledWeightKgByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
