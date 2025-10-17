package br.com.dnsoftware.gestao_producao.repository;

import br.com.dnsoftware.gestao_producao.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByCode(String code);

    Optional<Product> findByName(String name);

    @Query("SELECT DISTINCT p FROM Product p JOIN Command c ON p.id = c.product.id WHERE " +
            "(:name IS NULL OR :name = '' OR lower(p.name) LIKE lower(concat('%', :name, '%'))) AND " +
            "(:day IS NULL OR DAY(c.consumptionDate) = :day) AND " +
            "(:month IS NULL OR MONTH(c.consumptionDate) = :month) AND " +
            "(:year IS NULL OR YEAR(c.consumptionDate) = :year)")
    List<Product> findFilteredProducts(
            @Param("keyword") String keyword,
            @Param("sector") String sector,
            @Param("day") Integer day,
            @Param("month") Integer month,
            @Param("year") Integer year
    );


}