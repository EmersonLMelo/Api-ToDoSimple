package com.emersonmelo.todosimple.controllers;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.emersonmelo.todosimple.models.User;
import com.emersonmelo.todosimple.models.dto.UserCreateDTO;
import com.emersonmelo.todosimple.models.dto.UserUpdateDTO;
import com.emersonmelo.todosimple.services.UserService;

//RestController = retorna o objeto e os dados do objeto são gravados diretamente na resposta HTTP como JSON ou XML
//RequestMapping("/user") = definir a rota base como /user
//Validated = vai garantir a validação pedidas das outras classes  
@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    
    @Autowired
    private UserService userService;

    //GetMapping("/{id}") = fala que é uma função get e define a rota pelo id do usuario
    //ResponseEntity<User> = uma resposta de entidade do tipo usuario, recomendado utilizar para tratar o retorno em Json
    //PathVariable = para lincar o id da função com o id da rota, nesse caso do GetMapping
    //ResponseEntity.ok().body(obj) = retorna o corpo da requisição, o ok é uma função do proprio ResponseEntity para garantir que não deu erro nenhum, caso de erro retorna 500
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id){
        User obj = this.userService.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    //PostMapping = fala que é uma função Post
    //RequestBody = para indicar ao Spring que um recurso não será enviado ou recebido por meio de uma página da Web
    //ServletUriComponentsBuilder... = ele fazer um build pegando o contesto da requisição atual(nesse caso o /user), depois vai pegar o path do path variable(caindo assim no /user/id)
    //buildAndExpand... = depois um build para transformar a variavel id do path no id do usuario, e assim transformando tudo em uri
    //URI = nada mais é que uma String com o endereço do local do servidor requisitado
    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody UserCreateDTO obj){
        User user = this.userService.fromDTO(obj);
        User newUser = this.userService.create(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newUser.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    //PutMapping = fala que é uma função Put
    //obj.setId(id) = encontrar o usuario pelo id e garatir que existe
    //noContent() = não estamos retornando nenhum dado, so atualizando
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@Valid @RequestBody UserUpdateDTO obj, @PathVariable Long id){
        obj.setId(id);
        User user = this.userService.fromDTO(obj);
        this.userService.update(user);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        this.userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
