package com.emersonmelo.todosimple.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//AllArgsConstructor = anotação do lombok para criar os constructors dos argumentos
//NoArgsConstructor = gera os constructrs vazios
//Getter e Setter = gera os getters e setters
//EqualsAndHashCode = gera as funçoes equals e hash code
@Entity
@Table(name = Task.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Task {
    public static final String TABLE_NAME = "task";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    //ManyToOne = Varias taferas são de um usuario, um usuario pode possuir varias tarefas
    //JoinColumn = para especificar a coluna que deve ser usada como chave estrangeira para o relacionamento
    //JoinColumn updatable = Especifica se a coluna de chave estrangeira deve ser incluída nas instruções SQL UPDATE geradas pelo JPA, um usuario nao pode mecher na tarefa de outro usuario
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "description", length = 255, nullable = false)
    @NotNull
    @NotEmpty
    @Size(min = 1, max = 255)
    private String description;


    
    
}
