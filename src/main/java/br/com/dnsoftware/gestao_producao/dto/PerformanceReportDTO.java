package br.com.dnsoftware.gestao_producao.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformanceReportDTO {

    private String productName;
    private Double totalProduced;
    private Double totalIdaBuffet;
    private Double totalVoltaBuffet;
    private Double totalDesperdicio;
    private Double totalEtiquetasDescartadas;
    private Double totalOutrosUsosPessoais;
    private Double totalEmpresa908;
    private Double totalEmpresa909;

    private Double gapBuffet;
    private Double totalSaidas;
    private Double gapFinal;


    public PerformanceReportDTO(String productName, Double totalProduced, Double totalIdaBuffet,
                                Double totalVoltaBuffet, Double totalDesperdicio,
                                Double totalEtiquetasDescartadas, Double totalOutrosUsosPessoais,
                                Double totalEmpresa908, Double totalEmpresa909) {

        this.productName = productName;
        this.totalProduced = totalProduced;
        this.totalIdaBuffet = totalIdaBuffet;
        this.totalVoltaBuffet = totalVoltaBuffet;
        this.totalDesperdicio = totalDesperdicio;
        this.totalEtiquetasDescartadas = totalEtiquetasDescartadas;
        this.totalOutrosUsosPessoais = totalOutrosUsosPessoais;
        this.totalEmpresa908 = totalEmpresa908;
        this.totalEmpresa909 = totalEmpresa909;
    }
}