package com.emersonmelo.todosimple.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emersonmelo.todosimple.models.User;
import com.emersonmelo.todosimple.repositories.TaskRepository;
import com.emersonmelo.todosimple.repositories.UserRepository;

//Service = para indicar que eles mantêm a lógica de service
@Service
public class UserService {
    
    //Autowired = indica um ponto aonde a injeção automática deve ser aplicada, sem usar construtores ou getters and setters
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    //Optional = para evitar null pointer exception, por que vc pode receber ou não um usuario
    //userRepository.findById = vem direto do extends jpaRepository
    //user.orElseThrow = Retorno o usuario se ele tiver prenchido, se tiver vazio eu faço um throw exception
    //RuntimeException = para permitir que o programa continue rodando mesmo se um usuario fazer uma busca vazia, e exibe uma mensagem
    public User findById(Long id){
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new RuntimeException(
            "Usuário não encontrado! " + id + ", Tipo: " + User.class.getName()
        ));
    }

    //Transactional = Muito aspectos melhoram quando usamos este recurso, como por exemplo a legibilidade do código, e até a possibilidade de chamar outros métodos que já estejam em transação(recomendado apenas em inserções de dado como create e update)
    //userRepository.save = salva todo o obj mandando pelo usuario e taskRepository.saveAll salva todas as taks caso o usuario crie um usuario com tasks ja
    //obj.setId(null) = garante que na hora de criação o usuario não passe nenhum id, pois o id deve ser gerado automaticamente
    @Transactional
    public User create(User obj){
        obj.setId(null);
        obj = this.userRepository.save(obj);
        this.taskRepository.saveAll(obj.getTasks());
        return obj;
    }

    //User newObj = findById(obj.getId()) = para garantir que o usuario existe e possa ser atualizado, é feita uma busca pelo id
    //newObj.setPassword(obj.getPassword()) = para atualizar a senha
    //userRepository.save(newObj) = salvando a nova senha do usuario
    @Transactional
    public User update(User obj){
        User newObj = findById(obj.getId());
        newObj.setPassword(obj.getPassword());
        return this.userRepository.save(newObj);
    }

    //findById(id) = busca o usuario pelo id para saber se ele realmente existe antes de deletar
    //Try Catch = é colocado na função delete para garantir que o usuario não tem relacionamento com mais nenhum outro usuario antes de ser deletado
    public void delete(Long id){
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não é possivel excluir pois há entidades relacionadas!");
        }
    }

}
