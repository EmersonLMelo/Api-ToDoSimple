package com.emersonmelo.todosimple.services;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.emersonmelo.todosimple.models.User;
import com.emersonmelo.todosimple.models.enums.ProfileEnum;
import com.emersonmelo.todosimple.repositories.UserRepository;
import com.emersonmelo.todosimple.security.UserSpringSecurity;
import com.emersonmelo.todosimple.services.exceptions.AuthorizationException;
import com.emersonmelo.todosimple.services.exceptions.DataBindingViolationException;
import com.emersonmelo.todosimple.services.exceptions.ObjectNotFoundException;

//Service = para indicar que eles mantêm a lógica de service
@Service
public class UserService {
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //Autowired = indica um ponto aonde a injeção automática deve ser aplicada, sem usar construtores ou getters and setters
    @Autowired
    private UserRepository userRepository;

    //Optional = para evitar null pointer exception, por que vc pode receber ou não um usuario
    //userRepository.findById = vem direto do extends jpaRepository
    //user.orElseThrow = Retorno o usuario se ele tiver prenchido, se tiver vazio eu faço um throw exception
    //ObjectNotFoundException = caso o usuario não exista é lançada a exceção da classe
    public User findById(Long id){
        UserSpringSecurity userSpringSecurity = authenticated();
        if(!Objects.nonNull(userSpringSecurity) || userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !id.equals(userSpringSecurity.getId()))
            throw new AuthorizationException("Acesso negado!");

        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new ObjectNotFoundException(
            "Usuário não encontrado! " + id + ", Tipo: " + User.class.getName()
        ));
    }

    //Transactional = Muito aspectos melhoram quando usamos este recurso, como por exemplo a legibilidade do código, e até a possibilidade de chamar outros métodos que já estejam em transação(recomendado apenas em inserções de dado como create e update)
    //userRepository.save = salva todo o obj mandando pelo usuario
    //obj.setId(null) = garante que na hora de criação o usuario não passe nenhum id, pois o id deve ser gerado automaticamente
    //obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword())) = incriptação da senha
    @Transactional
    public User create(User obj){
        obj.setId(null);
        obj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword())); 
        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
        obj = this.userRepository.save(obj);
        return obj;
    }

    //User newObj = findById(obj.getId()) = para garantir que o usuario existe e possa ser atualizado, é feita uma busca pelo id
    //newObj.setPassword(obj.getPassword()) = para atualizar a senha
    //userRepository.save(newObj) = salvando a nova senha do usuario
    @Transactional
    public User update(User obj){
        User newObj = findById(obj.getId());
        newObj.setPassword(obj.getPassword());
        newObj.setPassword(this.bCryptPasswordEncoder.encode(obj.getPassword()));
        return this.userRepository.save(newObj);
    }

    //findById(id) = busca o usuario pelo id para saber se ele realmente existe antes de deletar
    //Try Catch = é colocado na função delete para garantir que o usuario não tem relacionamento com mais nenhum outro usuario antes de ser deletado
    //DataBindingViolationException = caso o usuario não possa ser deletado é lançado a exceção da classe
    public void delete(Long id){
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new DataBindingViolationException("Não é possivel excluir pois há entidades relacionadas!");
        }
    }

    //este método tem o propósito de fornecer acesso fácil ao usuário atualmente autenticado em um contexto de Spring Security
    public static UserSpringSecurity authenticated(){
        try {
            return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }

}
