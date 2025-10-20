package br.com.dnsoftware.gestao_producao.repository;

import br.com.dnsoftware.gestao_producao.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByCode(String code);

    Optional<Product> findByName(String name);

    @Query("SELECT p FROM Product p LEFT JOIN p.sector s WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR lower(p.name) LIKE lower(concat('%', :keyword, '%'))) AND " +
            "(:sectorName IS NULL OR :sectorName = '' OR s.name = :sectorName) AND " +
            "(:unit IS NULL OR :unit = '' OR p.unit = :unit)")
    List<Product> findFilteredProducts(
            @Param("keyword") String keyword,
            @Param("sectorName") String sectorName,
            @Param("unit") String unit
    );

    @Query("SELECT DISTINCT p.unit FROM Product p WHERE p.unit IS NOT NULL AND p.unit != '' ORDER BY p.unit")
    List<String> findDistinctUnits();


}