package com.emersonmelo.todosimple.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emersonmelo.todosimple.models.User;

//@Repository = utilizada para indicar que uma classe é responsável por acessar e manipular dados no banco de dados ou em outra fonte de dados externa
//extends JpaRepository<User, Long> = para trazer todas as funçoes nescessaria para consulta
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
}
