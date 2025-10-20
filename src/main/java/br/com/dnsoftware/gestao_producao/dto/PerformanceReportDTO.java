package br.com.dnsoftware.gestao_producao.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PerformanceReportDTO {

    private String productName;
    private Double totalProduced = 0.0;
    private Double totalIdaBuffet = 0.0;
    private Double totalVoltaBuffet = 0.0;
    private Double totalDesperdicio = 0.0;
    private Double totalEtiquetasDescartadas = 0.0; // Tipo de comanda não existe, será sempre 0
    private Double totalOutrosUsosPessoais = 0.0;
    private Double totalEmpresa908 = 0.0;
    private Double totalEmpresa909 = 0.0;
    private Double totalTransformacao = 0.0;

    private Double gapBuffet = 0.0;
    private Double totalSaidas = 0.0;
    private Double gapFinal = 0.0;


    public PerformanceReportDTO(String productName) {
        this.productName = productName;
    }
}