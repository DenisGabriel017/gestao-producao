package br.com.dnsoftware.gestao_producao.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ABC_DTO {

    @CsvBindByName(column = "CODIGO")
    private String productCode;

    @CsvBindByName(column = "NOME")
    private String productName;

    @CsvBindByName(column = "TOTAL_PRODUZIDO")
    private Double totalProduced;

    @CsvBindByName(column = "VALOR_VENDA")
    private Double salePrice;

    @CsvBindByName(column = "VALOR_TOTAL_FATURADO")
    private Double totalRevenue;
}