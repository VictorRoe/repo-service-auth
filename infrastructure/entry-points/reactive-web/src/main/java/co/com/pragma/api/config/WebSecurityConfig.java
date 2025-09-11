package co.com.pragma.api.config;

import co.com.pragma.api.security.SecurityContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final SecurityContextRepository securityContextRepository;


    @Bean
    @Order(1)
    public SecurityWebFilterChain publicEndpointsFilterChain(ServerHttpSecurity http) {
        return http
                .securityMatcher(new OrServerWebExchangeMatcher(
                        new PathPatternParserServerWebExchangeMatcher("/api/v1/login", HttpMethod.POST),
                        new PathPatternParserServerWebExchangeMatcher("/api/v1/usuarios/**", HttpMethod.POST),
                        new PathPatternParserServerWebExchangeMatcher("/swagger-ui/**", HttpMethod.GET),
                        new PathPatternParserServerWebExchangeMatcher("/v3/api-docs/**", HttpMethod.GET)
                ))
                .authorizeExchange(exchange -> exchange.anyExchange().permitAll())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .build();
    }

    @Bean
    @Order(2)
    public SecurityWebFilterChain protectedEndpointsFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchange -> exchange.anyExchange().authenticated())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((swe, e) ->
                                Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                        )
                        .accessDeniedHandler((swe, e) ->
                                Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                        )
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .securityContextRepository(securityContextRepository)
                .build();
    }


//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        return http
//                .exceptionHandling(exceptionHandling -> exceptionHandling
//                        .authenticationEntryPoint((swe, e) ->
//                                Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
//                        )
//                        .accessDeniedHandler((swe, e) ->
//                                Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
//                        )
//                )
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
//                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
////                .securityContextRepository(securityContextRepository)
//                .authorizeExchange(exchange -> exchange
//                        .pathMatchers(HttpMethod.POST, "/api/v1/usuarios").permitAll()
//                        .pathMatchers(HttpMethod.POST, "/api/v1/login").permitAll()
//                        .pathMatchers(HttpMethod.GET, "/swagger-ui/index.html").permitAll()
//                        .pathMatchers(HttpMethod.GET, "/webjars/swagger-ui/index.html").permitAll()
//                        .anyExchange().authenticated()
//                )
//                .build();
//    }
}
