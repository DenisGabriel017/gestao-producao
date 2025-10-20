package br.com.dnsoftware.gestao_producao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class CommandHistoryDTO {
    private LocalDate consumptionDate;
    private Integer quantity;
    private Integer commandNumber;
}
