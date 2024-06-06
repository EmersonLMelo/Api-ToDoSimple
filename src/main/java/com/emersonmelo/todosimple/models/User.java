package com.emersonmelo.todosimple.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.emersonmelo.todosimple.models.enums.ProfileEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


//Entity para poder tratar, poder usar o CRUD
//Table para poder definir informações da tabela, como o nome
@Entity
@Table(name = User.TABLE_NAME)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

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
    @NotBlank
    @Size(min = 2, max = 100)
    private String username;

    //@JsonProperty(access = Access.WRITE_ONLY) = para dizer que não deve retornar a senha no json, que a senha é de apenas escrita não leitura
    @JsonProperty(access = Access.WRITE_ONLY)
    @Column(name = "password", length = 60, nullable = false)
    @NotBlank
    @Size(min = 8, max = 60)
    private String password;

    //OneToMany = Cada usuario vai ter uma lista de tarefas
    //mappedBy = "user" = De quem que é as task
    @OneToMany(mappedBy = "user")
    @JsonProperty(access = Access.WRITE_ONLY)
    private List<Task> tasks = new ArrayList<Task>();


    //@ElementCollection(fetch = FetchType.EAGER) = Indica que profiles é uma coleção de elementos, e que eles devem ser recuperados imediatamente (EAGER) quando a entidade for carregada
    //private Set<Integer> profiles = new HashSet<>() = define um campo que armazena os perfis de usuário como um conjunto de inteiros. Este conjunto contem os códigos dos perfis dos usuários
    @Column(name = "profile", nullable = false)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_profile")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Integer> profiles = new HashSet<>();

    //transforma os códigos de perfil (inteiros) em objetos ProfileEnum e os coleta em um conjunto
    public Set<ProfileEnum> getProfiles() {
        return this.profiles.stream().map(x -> ProfileEnum.toEnum(x)).collect(Collectors.toSet());
    }

    //adiciona um novo perfil ao conjunto profiles convertendo o enum para seu código correspondente.
    public void addProfile(ProfileEnum profileEnum) {
        this.profiles.add(profileEnum.getCode());
    }
}
