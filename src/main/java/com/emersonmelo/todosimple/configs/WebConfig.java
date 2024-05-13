package com.emersonmelo.todosimple.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//WebMvcCongigurer = já tras alguns metodos do spring para a classe
//Configuration = já declara para o spring que essa é uma classe de configuração
//EnableWebMvc = Ela já habilita que o Spring carregue em seu container uma série de objetos que habilitam recursos como integração com bean validation, suporte geração de RSS, suporte a serialização de objetos para JSON, XML, etc.
//addCorsMapping = Este método é usado para definir mapeamentos CORS. Registry.addMapping("/**") permite solicitações CORS de qualquer origem
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    
    public void addCorsMapping(CorsRegistry registry){
        registry.addMapping("/**");
    }
}
