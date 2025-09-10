package br.com.dnsoftware.gestao_producao.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandDTO {

    @CsvBindByName(column = "CODIGO_PRODUTO")
    private String productCode;

    @CsvBindByName(column = "QUANTIDADE_COMANDAS")
    private Integer commandQuantity;

    @CsvBindByName(column = "NOME_PRODUTO")
    private String productName;

    @CsvBindByName(column = "NOME_USUARIO")
    private String userName;
}