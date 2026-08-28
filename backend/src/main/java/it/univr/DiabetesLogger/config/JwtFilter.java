package it.univr.DiabetesLogger.config;

import it.univr.DiabetesLogger.service.CustomUserDetailsService;
import it.univr.DiabetesLogger.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{

        // 1. Legge l'header Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. Se non ce passa avanti senza fare nulla
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Estrae il token togliendo Bearer
        String token = authHeader.substring(7);

        // 4. Estrae l'email dal token
        String email = jwtService.extractEmail(token);

        // 5. Se c'e un email e l'utente non e gia autenticato in questa request
        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){

            // 6. Carica l'utente dal DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 7. valida il token
            if(jwtService.validateToken(token, userDetails)){

                // 8. crea il toekn di autenticazione per Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // 9. aggiunge i dettagli della request HTTP
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 10. setta il SecurityContext, quindi da qui spring sa chi sei
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 11. passa al prossimo filtro della catena
        filterChain.doFilter(request, response);
    }
}

