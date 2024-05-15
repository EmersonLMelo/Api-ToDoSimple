package com.emersonmelo.todosimple.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//Entity para poder tratar, poder usar o CRUD
//Table para poder definir informações da tabela, como o nome
@Entity
@Table(name = User.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class User {
    public interface CreateUser {}
    public interface UpdateUser {}

    public static final String TABLE_NAME = "user";    

    //Id = indicando que o campo membro abaixo é a chave primária da entidade atual
    //GeneratedValue = serve para configurar a forma de incremento da coluna
    //GenerationType.IDENTITY = estamos dizendo ao provedor de persistência para deixar o banco de dados lidar com o incremento automático do id
    //Column = para poder definir informações da coluna, como o nome, ou dizer que a coluna é unica e não dublicar valor
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    //Column = lenght para definir o tamanho maximo de caracteres, nullable para definir que não pode ser nulo
    //NotNull e NotEmpty = é anotações de validação que o usuario não pode colocar Strings nulas ou vazias
    //Size = anotação de validação direta no sistema que defini o tamanho maximo e minimo de caracteres
    //groups = Os grupos de validação permitem aplicar regras de validação específicas a diferentes cenários
    //CreateUser e UpdateUser = Colocado uma interface para ligar as anotações tanto com o create quanto o update, pois o usuario pode atualizar o password mas nunca o username
    @Column(name = "username", length = 100, nullable = false, unique = true)
    @NotNull(groups = CreateUser.class)
    @NotEmpty(groups = CreateUser.class)
    @Size(groups = CreateUser.class, min = 2, max = 100)
    private String username;

    //@JsonProperty(access = Access.WRITE_ONLY) = para dizer que não deve retornar a senha no json, que a senha é de apenas escrita não leitura
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "password", length = 60, nullable = false)
    @NotNull(groups = {CreateUser.class, UpdateUser.class})
    @NotEmpty(groups = {CreateUser.class, UpdateUser.class})
    @Size(groups = {CreateUser.class, UpdateUser.class}, min = 8, max = 60)
    private String password;

    //OneToMany = Cada usuario vai ter uma lista de tarefas
    //mappedBy = "user" = De quem que é as task
    @OneToMany(mappedBy = "user")
    @JsonProperty(access = Access.WRITE_ONLY)
    private List<Task> tasks = new ArrayList<Task>();

   
}
