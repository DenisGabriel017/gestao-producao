package br.com.dnsoftware.gestao_producao.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionDTO {

    @CsvBindByName(column = "PRODUTO")
    private String productName;

    @CsvBindByName(column = "PRODUZIDO_UNIDADES")
    private Integer producedUnits;

    @CsvBindByName(column = "CONSUMIDO_UNIDADES")
    private Integer consumedUnits;

    @CsvBindByName(column = "DESPERDICIO_UNIDADES")
    private Integer wasteUnits;

    @CsvBindByName(column = "PESO_TOTAL_KG")
    private Double totalWeightKg;
}