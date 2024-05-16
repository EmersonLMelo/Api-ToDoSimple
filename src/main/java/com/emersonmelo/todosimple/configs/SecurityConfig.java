package com.emersonmelo.todosimple.configs;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


//Configuration =  indica que a classe anotada contém métodos de configuração que devem ser processados pelo contêiner Spring durante a inicialização do aplicativo
//EnableWebSecurity = está ativando a segurança da web no seu aplicativo, permitindo que você configure regras de segurança, autenticação e autorização para proteger suas APIs e páginas da web
//EnableGlobalMethodSecurity(prePostEnabled = true) = regras de autorização mais granulares em métodos específicos do seu código, além das configurações de segurança globais que podem ser definidas com @EnableWebSecurity.
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    //Rotas publicas que todos podem acessar
    private static final String[] PUBLIC_MATCHERS = {
        "/"
    };

    //Rotas publicas de post
    private static final String[] PUBLIC_MATCHERS_POST = {
        "/user",
        "/login"
    };

    //Bean = para indicar que um método é responsável por criar e configurar um bean gerenciado pelo contêiner Spring
    //Funçaõ usada para configurar as regras de segurança para diferentes rotas (endpoints) da aplicação.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable();

        http.authorizeRequests()
            .antMatchers(HttpMethod.POST, PUBLIC_MATCHERS_POST).permitAll()
            .antMatchers(PUBLIC_MATCHERS).permitAll()
            .anyRequest().authenticated();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

    //É responsável por configurar as políticas de CORS (Cross-Origin Resource Sharing) para sua aplicação
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    //Para codificar senhas usando o algoritmo de hashing BCrypt
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
