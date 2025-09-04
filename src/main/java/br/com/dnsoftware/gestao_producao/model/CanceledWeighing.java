package br.com.dnsoftware.gestao_producao.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "canceled_weighings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanceledWeighing{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_record_id")
    private Long originalRecordId;

    @Column(name = "canceled_weight_kg", nullable = false)
    private Double canceledWeightKg;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "cancellation_date", nullable = false)
    private LocalDateTime cancellationDate;

}
