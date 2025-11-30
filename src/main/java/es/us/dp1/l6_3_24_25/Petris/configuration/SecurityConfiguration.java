package es.us.dp1.l6_3_24_25.Petris.configuration;

import static org.springframework.security.config.Customizer.withDefaults;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import es.us.dp1.l6_3_24_25.Petris.configuration.jwt.AuthEntryPointJwt;
import es.us.dp1.l6_3_24_25.Petris.configuration.jwt.AuthTokenFilter;
import es.us.dp1.l6_3_24_25.Petris.configuration.services.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Autowired
	DataSource dataSource;

	private static final String ADMIN = "ADMIN";
	@SuppressWarnings("unused")
    private static final String PLAYER = "PLAYER";


	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

		http
            .cors(withDefaults())
            // Si usas H2, conviene ignorar su CSRF:
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))

            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos comunes (css, js, images, webjars…) públicos
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers("/uploads/**").permitAll()
                // H2 Console accesible
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                // Raíz / páginas públicas
                .requestMatchers("/", "/oups").permitAll()

                // Swagger / OpenAPI accesible
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/swagger-resources/**"
                ).permitAll()

                // API pública
                .requestMatchers("/api/v1/profile").permitAll()
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/users/authorities").permitAll()
                .requestMatchers("/api/v1/plans/**").permitAll()
                .requestMatchers("/api/v1/plan").permitAll()
                .requestMatchers("/api/v1/matches/**").permitAll()
                .requestMatchers("/api/v1/players/**").permitAll()
                .requestMatchers("/api/v1/achievements").permitAll()
                .requestMatchers("/api/v1/users/**").permitAll()
                .requestMatchers("/api/salas").permitAll()
                .requestMatchers("/api/salas/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/app/**").permitAll()
                .requestMatchers("/topic/**").permitAll()
                .requestMatchers("/api/v1/statistics/achievements").permitAll()
                .requestMatchers("/api/v1/statistics/ranking").permitAll()
                .requestMatchers("/api/v1/friends/**").authenticated()
                .requestMatchers("/api/v1/achievements/**").permitAll()

                // API restringida para administradores
                .requestMatchers("/api/v1/users").hasAuthority(ADMIN)

                // El resto denegado
                .anyRequest().denyAll()
            )


			.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}



}
