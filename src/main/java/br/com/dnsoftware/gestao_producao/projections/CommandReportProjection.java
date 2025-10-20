package br.com.dnsoftware.gestao_producao.projections;

public interface CommandReportProjection {
    String getProductName();
    Double getTotalIdaBuffet();
    Double getTotalVoltaBuffet();
    Double getTotalDesperdicio();
    Double getTotalOutrosUsosPessoais();
    Double getTotalEmpresa908();
    Double getTotalEmpresa909();
    Double getTotalTransformacao();
}
