package it.univr.DiabetesLogger.config;

import it.univr.DiabetesLogger.service.CustomUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    // -> sa come caricare un utente dal DB

    @Autowired
    private JwtFilter jwtFilter;
    // -> filtro che intercessa ogni Http request

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http.csrf(customizer -> customizer.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(
                        request -> {

                            // AUTH
                            request
                                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                    .requestMatchers("/auth/login", "/auth/register").permitAll()
                                    .requestMatchers(HttpMethod.GET, "/auth/pending").hasAuthority("ADMIN")
                                    .requestMatchers(HttpMethod.POST, "/auth/verify").hasAuthority("ADMIN")
                                    .requestMatchers(HttpMethod.POST, "/auth/delete").hasAuthority("ADMIN")
                                    .requestMatchers(HttpMethod.GET, "/users/{id}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('ADMIN') or authentication.getPrincipal().checkId(#id)"))
                                    .requestMatchers(HttpMethod.PATCH, "/users/{id}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('ADMIN') or authentication.getPrincipal().checkId(#id)"));

// GLYCEMIA READING
                            request
                                    .requestMatchers(HttpMethod.GET,
                                            "/glycemia/patient/{patientId}",
                                            "/glycemia/patient/{patientId}/{id}",
                                            "/glycemia/patient/{patientId}/week",
                                            "/glycemia/patient/{patientId}/month",
                                            "/glycemia/patient/{patientId}/above-threshold")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAnyAuthority('MEDIC', 'ADMIN') or " +
                                                    "(hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId))"))

                                    .requestMatchers(HttpMethod.POST, "/glycemia/patient/{patientId}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId)"))

                                    .requestMatchers(HttpMethod.PUT, "/glycemia/patient/{patientId}/{id}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId)"))

                                    .requestMatchers(HttpMethod.DELETE, "/glycemia/patient/{patientId}/{id}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId)"));

// MEDICINE INTAKE
                            request
                                    .requestMatchers(HttpMethod.GET,
                                            "/medicine-intake/patient/{patientId}",
                                            "/medicine-intake/patient/{patientId}/{id}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAnyAuthority('MEDIC', 'ADMIN') or " +
                                                    "(hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId))"))

                                    .requestMatchers(HttpMethod.GET,
                                            "/medicine-intake/patient/{patientId}/inconsistent",
                                            "/medicine-intake/patient/{patientId}/missed")
                                    .hasAnyAuthority("MEDIC", "ADMIN")

                                    .requestMatchers(HttpMethod.POST, "/medicine-intake/patient/{patientId}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId)"))

                                    .requestMatchers(HttpMethod.PUT, "/medicine-intake/patient/{patientId}/{id}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId)"))

                                    .requestMatchers(HttpMethod.DELETE, "/medicine-intake/patient/{patientId}/{id}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId)"));

// SYMPTOM
                            request
                                    .requestMatchers(HttpMethod.GET,
                                            "/symptom/patient/{patientId}",
                                            "/symptom/patient/{patientId}/{id}",
                                            "/symptom/patient/{patientId}/active")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAnyAuthority('MEDIC', 'ADMIN') or " +
                                                    "(hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId))"))

                                    .requestMatchers(HttpMethod.POST, "/symptom/patient/{patientId}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId)"))

                                    .requestMatchers(HttpMethod.PUT, "/symptom/patient/{patientId}/{id}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId)"))

                                    .requestMatchers(HttpMethod.PATCH, "/symptom/{id}/close")
                                    .hasAnyAuthority("PATIENT", "MEDIC")

                                    .requestMatchers(HttpMethod.DELETE, "/symptom/patient/{patientId}/{id}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId)"));

// THERAPY
                            request
                                    .requestMatchers(HttpMethod.GET,
                                            "/therapy/patient/{patientId}",
                                            "/therapy/patient/{patientId}/active")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAnyAuthority('MEDIC', 'ADMIN') or " +
                                                    "(hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId))"))

                                    .requestMatchers(HttpMethod.GET,
                                            "/therapy/{id}",
                                            "/therapy/patient/{patientId}/suspended",
                                            "/therapy/patient/{patientId}/modified")
                                    .hasAnyAuthority("MEDIC", "ADMIN")

                                    .requestMatchers(HttpMethod.POST, "/therapy/patient/{patientId}/medic/{medicId}")
                                    .hasAuthority("MEDIC")

                                    .requestMatchers(HttpMethod.PUT, "/therapy/{id}/medic/{medicId}")
                                    .hasAuthority("MEDIC")

                                    .requestMatchers(HttpMethod.PATCH, "/therapy/{id}/suspend/medic/{medicId}")
                                    .hasAuthority("MEDIC")

                                    .requestMatchers(HttpMethod.DELETE, "/therapy/{id}")
                                    .hasAuthority("MEDIC");

// PATIENT
                            request
                                    //.requestMatchers(HttpMethod.GET, "/patient", "/patient/{id}", "/patient/medic/{medicId}")
                                    //.hasAnyAuthority("MEDIC", "ADMIN")
                                    .requestMatchers(HttpMethod.GET, "/patient")
                                    .hasAnyAuthority("MEDIC", "ADMIN")

                                    .requestMatchers(HttpMethod.GET, "/patient/{id}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAnyAuthority('MEDIC', 'ADMIN') or " +
                                                    "(hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#id))"
                                    ))

                                    .requestMatchers(HttpMethod.GET, "/patient/medic/{medicId}")
                                    .hasAnyAuthority("MEDIC", "ADMIN")

                                    .requestMatchers(HttpMethod.POST, "/patient")
                                    .hasAnyAuthority("PATIENT","ADMIN")

                                    .requestMatchers(HttpMethod.PUT, "/patient/{id}")
                                    .hasAnyAuthority("MEDIC", "ADMIN")

                                    .requestMatchers(HttpMethod.PUT, "/patient/{id}/risk-factors")
                                    .hasAuthority("MEDIC")

                                    .requestMatchers(HttpMethod.PUT, "/patient/{patientId}/medic/{medicId}")
                                    .hasAuthority("ADMIN")

                                    .requestMatchers(HttpMethod.DELETE, "/patient/{id}")
                                    .hasAuthority("ADMIN");

                            // PATHOLOGY
                            request
                                    .requestMatchers(HttpMethod.GET,
                                            "/pathology/patient/{patientId}",
                                            "/pathology/patient/{patientId}/{id}",
                                            "/pathology/patient/{patientId}/active")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAnyAuthority('MEDIC', 'ADMIN') or " +
                                                    "(hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId))"))

                                    .requestMatchers(HttpMethod.POST, "/pathology/patient/{patientId}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId)"))

                                    .requestMatchers(HttpMethod.PUT, "/pathology/patient/{patientId}/{id}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId)"))

                                    .requestMatchers(HttpMethod.PATCH, "/pathology/{id}/close")
                                    .hasAnyAuthority("PATIENT", "MEDIC")

                                    .requestMatchers(HttpMethod.DELETE, "/pathology/patient/{patientId}/{id}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId)"));

                            // CONCOMITANT THERAPY
                            request
                                    .requestMatchers(HttpMethod.GET,
                                            "/concomitant-therapy/patient/{patientId}",
                                            "/concomitant-therapy/patient/{patientId}/{id}",
                                            "/concomitant-therapy/patient/{patientId}/active")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAnyAuthority('MEDIC', 'ADMIN') or " +
                                                    "(hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId))"))

                                    .requestMatchers(HttpMethod.POST, "/concomitant-therapy/patient/{patientId}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId)"))

                                    .requestMatchers(HttpMethod.PUT, "/concomitant-therapy/patient/{patientId}/{id}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId)"))

                                    .requestMatchers(HttpMethod.PATCH, "/concomitant-therapy/{id}/close")
                                    .hasAnyAuthority("PATIENT", "MEDIC")

                                    .requestMatchers(HttpMethod.DELETE, "/concomitant-therapy/patient/{patientId}/{id}")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('PATIENT') and authentication.getPrincipal().checkId(#patientId)"));

// MEDIC
                            request
                                    .requestMatchers(HttpMethod.GET, "/medic")
                                    .hasAnyAuthority("MEDIC", "ADMIN")

                                    .requestMatchers(HttpMethod.GET, "/medic/{id}", "/medic/{id}/patients",
                                            "/medic/{id}/patients/high-glycemia")
                                    .hasAnyAuthority("MEDIC", "ADMIN")

                                    .requestMatchers(HttpMethod.POST, "/medic")
                                    .hasAnyAuthority("MEDIC", "ADMIN")

                                    .requestMatchers(HttpMethod.PUT, "/medic/{id}")
                                    .hasAnyAuthority("MEDIC", "ADMIN")

                                    .requestMatchers(HttpMethod.DELETE, "/medic/{id}")
                                    .hasAuthority("ADMIN");

// NOTIFICATION
                            request
                                    .requestMatchers(HttpMethod.GET,
                                            "/notification/user/{userId}",
                                            "/notification/user/{userId}/unread")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('ADMIN') or " +
                                                    "(hasAnyAuthority('PATIENT', 'MEDIC') and authentication.getPrincipal().checkId(#userId))"))

                                    .requestMatchers(HttpMethod.GET, "/notification/{id}")
                                    .hasAnyAuthority("PATIENT", "MEDIC", "ADMIN")

                                    .requestMatchers(HttpMethod.PATCH,
                                            "/notification/{id}/seen",
                                            "/notification/user/{userId}/seen")
                                    .access(new WebExpressionAuthorizationManager(
                                            "hasAuthority('ADMIN') or " +
                                                    "(hasAnyAuthority('PATIENT', 'MEDIC') and authentication.getPrincipal().checkId(#userId))"))

                                    .requestMatchers(HttpMethod.DELETE, "/notification/{id}")
                                    .hasAnyAuthority("PATIENT", "MEDIC", "ADMIN");

                            request.anyRequest().hasAnyAuthority("ADMIN");
                            //request.anyRequest().permitAll();
                        })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);

        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}
