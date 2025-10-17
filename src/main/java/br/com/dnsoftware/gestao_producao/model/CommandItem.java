

package br.com.dnsoftware.gestao_producao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "command_item") // Nome da tabela no banco
@Entity(name = "CommandItem") // Nome que o JPQL vai usar!
public class CommandItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "command_id", nullable = false)
    private Command command;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Double quantity;

}