package br.com.dnsoftware.gestao_producao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.time.LocalDate;

@Entity
@Table(name = "production")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Production {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "production_date", nullable = false)
    private LocalDate productionDate;

    @Column(name = "produced_units", nullable = false)
    private Double producedUnits;

    @Column(name = "real_outlets")
    private Integer realOutlets;

    @Column(name = "total_weight_kg")
    private Double totalWeightKg;

    public Production(Long id, Product product, LocalDate productionDate, Double producedUnits, Double totalWeightKg) {
        this.product = product;
        this.productionDate = productionDate;
        this.producedUnits = producedUnits;
        this.totalWeightKg = totalWeightKg;
    }

}
