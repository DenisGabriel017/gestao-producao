package br.com.dnsoftware.gestao_producao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "abc_sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ABC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "sold_units", nullable = false)
    private Integer soldUnits;

    @Column(name = "sale_date", nullable = false)
    private LocalDate saleDate;
}
