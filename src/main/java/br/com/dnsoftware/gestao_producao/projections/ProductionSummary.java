package br.com.dnsoftware.gestao_producao.projections;

public interface ProductionSummary {
    String getMonth();
    String getSector();
    Long getProducedUnits();


}
