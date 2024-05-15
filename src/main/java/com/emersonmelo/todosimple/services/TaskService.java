package com.emersonmelo.todosimple.services;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emersonmelo.todosimple.models.Task;
import com.emersonmelo.todosimple.models.User;
import com.emersonmelo.todosimple.repositories.TaskRepository;
import com.emersonmelo.todosimple.services.exceptions.DataBindingViolationException;
import com.emersonmelo.todosimple.services.exceptions.ObjectNotFoundException;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id) {
        Optional<Task> task = this.taskRepository.findById(id);
        return task.orElseThrow(() -> new ObjectNotFoundException(
            "Tarefa não encontrada! Id: " + id + ", Tipo: " + Task.class.getName() ));
    }

    //Função para buscar uma lista de tasks pelo id de um usuario
    public List<Task> findAllByUserId(Long userId){
        List<Task> tasks = this.taskRepository.findByUser_Id(userId);
        return tasks;
    }

    //obj.setUser(user) = garantir que os dados do usuario sao os que estao no banco
    @Transactional
    public Task create(Task obj){
        User user = this.userService.findById(obj.getUser().getId());
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
}
