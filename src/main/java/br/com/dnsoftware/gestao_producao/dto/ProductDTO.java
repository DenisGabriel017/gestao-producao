package br.com.dnsoftware.gestao_producao.dto;


import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {

    @CsvBindByName(column = "codigo")
    private String code;

    @CsvBindByName(column = "nome")
    private String name;

    @CsvBindByName(column = "setor")
    private String setor;

    @CsvBindByName(column = "preco_venda")
    private Double salePrice;

    @CsvBindByName(column = "unidade")
    private String unit;

}
