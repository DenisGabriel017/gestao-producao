package br.com.dnsoftware.gestao_producao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GestaoProducaoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestaoProducaoApplication.class, args);
    }
}