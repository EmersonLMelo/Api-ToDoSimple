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

import java.util.Objects;

//Entity para poder tratar, poder usar o CRUD
//Table para poder definir informações da tabela, como o nome
@Entity
@Table(name = User.TABLE_NAME)
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
    private List<Task> tasks = new ArrayList<Task>();

    //Spring sempre utiliza um Construtor vazio
    public User() {
    }

    //Para a criação do usuario
    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public List<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }


    //Este método retorna verdadeiro se os objetos booleanos representam o mesmo valor, falso caso contrário
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof User))
            return false;
        User other = (User) obj;
        if (this.id == null)
            if (other.id != null)
                return false;
            else if (!this.id.equals(other.id))
                return false;
        return Objects.equals(this.id, other.id) && Objects.equals(this.username, other.username)
                && Objects.equals(this.password, other.password);
    }

    //Determina o código hash para um determinado número inteiro. Por padrão, este método retorna um número inteiro aleatório exclusivo para cada instância.
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }
}
