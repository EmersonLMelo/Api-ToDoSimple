package com.emersonmelo.todosimple.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.emersonmelo.todosimple.models.Task;
import com.emersonmelo.todosimple.models.projection.TaskProjection;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    //Fazer uma lista de tasks de um usuario, buscando ele pelo id, usando apenas o spring
    List<TaskProjection> findByUser_Id(Long id);

    //Query = exemplo para fazer uma busca em JPQL, nesse caso selecionando todas t(tasks) de Task onde t.user.id(o id do usuario) é iqual o id(id de busca)
    //Param("id") = para indentificar o nome do parametro buscado
    //@Query(value = "SELECT t FROM Task t WHERE t.user.id = : id")
    //List<Task> findByUser_Id(@Param ("id") Long id);

    //Query = exemplo para fazer uma busca em SQL
    //nativeQuery = para dizer que é uma consulta nativa em SQL
    //@Query(value = "SELECT * FROM task t WHERE t.user_id = : id", nativeQuery = true)
    //List<Task> findByUser_Id(@Param ("id") Long id);

}
