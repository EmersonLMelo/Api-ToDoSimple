package com.emersonmelo.todosimple.services;

import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emersonmelo.todosimple.models.Task;
import com.emersonmelo.todosimple.models.User;
import com.emersonmelo.todosimple.models.enums.ProfileEnum;
import com.emersonmelo.todosimple.repositories.TaskRepository;
import com.emersonmelo.todosimple.security.UserSpringSecurity;
import com.emersonmelo.todosimple.services.exceptions.AuthorizationException;
import com.emersonmelo.todosimple.services.exceptions.DataBindingViolationException;
import com.emersonmelo.todosimple.services.exceptions.ObjectNotFoundException;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id) {
        Task task = this.taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
            "Tarefa não encontrada! Id: " + id + ", Tipo: " + Task.class.getName() ));

        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if(Objects.isNull(userSpringSecurity) || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !userHasTask(userSpringSecurity, task))
            throw new AuthorizationException("Acesso negado");

        return task;
    }

    //Função para buscar uma lista de tasks pelo id de um usuario
    public List<Task> findAllByUser(){
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if(Objects.isNull(userSpringSecurity))
            throw new AuthorizationException("Acesso negado");
        List<Task> tasks = this.taskRepository.findByUser_Id(userSpringSecurity.getId());
        return tasks;
    }

    //obj.setUser(user) = garantir que os dados do usuario sao os que estao no banco
    @Transactional
    public Task create(Task obj){
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if(Objects.isNull(userSpringSecurity)){
           throw new AuthorizationException("Acesso negado");
        }
        User user = this.userService.findById(userSpringSecurity.getId());
        obj.setId(null);
        obj.setUser(user);
        obj = this.taskRepository.save(obj);
        return obj;    
    }

    //newObj.setDescription(obj.getDescription()) = atualizar a descrição da task
    @Transactional
    public Task update(Task obj){
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return this.taskRepository.save(newObj);
    }

    public void delete(Long id){
        findById(id);
        try {
            this.taskRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possivel excluir pois há entidades relacionadas");
        }
    }

    private Boolean userHasTask(UserSpringSecurity userSpringSecurity, Task task){
        return task.getUser().getId().equals(userSpringSecurity.getId());
    }
}
