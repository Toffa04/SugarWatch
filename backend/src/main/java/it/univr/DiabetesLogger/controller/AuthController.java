package it.univr.DiabetesLogger.controller;

import it.univr.DiabetesLogger.model.User;
import it.univr.DiabetesLogger.model.enums.Role;
import it.univr.DiabetesLogger.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){

        // check campi obbligatori
        if(request.getEmail() == null || request.getEmail().isBlank()){
            return ResponseEntity.badRequest().body("Campo email obbligatorio");
        }
        if(request.getPassword() == null || request.getPassword().isBlank()){
            return ResponseEntity.badRequest().body("Campo password obbligatorio");
        }

        // check formato email
        if(!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")){
            return ResponseEntity.badRequest().body("Formato email non valido");
        }

        try{
            return ResponseEntity.ok(authService.login(request.getEmail(), request.getPassword()));
        } catch(DisabledException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN) // implementazione logica di attesa della verifica
                    .body("Utente non ancora verificato dall'admin");
        } catch(BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenziali non valide");
        } catch(UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante il login");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){

        // check campi obbligatori
        if(request.getUsername() == null || request.getUsername().isBlank()){
            return ResponseEntity.badRequest().body("Campo username obbligatorio");
        }
        if(request.getEmail() == null || request.getEmail().isBlank()){
            return ResponseEntity.badRequest().body("Campo email obbligatorio");
        }
        if(request.getPassword() == null || request.getPassword().isBlank()){
            return ResponseEntity.badRequest().body("Campo password obbligatorio");
        }
        if(request.getRole() == null){
            return ResponseEntity.badRequest().body("Campo ruolo obbligatorio");
        }

        // check lunghezza username
        if(request.getUsername().length() < 3 || request.getUsername().length() > 20){
            return ResponseEntity.badRequest()
                    .body("Username deve essere tra 3 e 20 caratteri");
        }

        // check lunghezza password
        if(request.getPassword().length() < 8){
            return ResponseEntity.badRequest()
                    .body("La password deve essere di almeno 8 caratteri");
        }

        try{
            User user = authService.registerUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getRole()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante la registrazione");
        }
    }

    // solo ADMIN vede gli utenti in attesa
    @GetMapping("/user")
    public ResponseEntity<?> getUsers(){
        try{
            List<User> users = authService.getAllUsers();
            return ResponseEntity.ok(users);
        }  catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore nel recupero degli utenti pendenti");
        }
    }

    // solo ADMIN verifica un utente
    @PostMapping("verify")
    public ResponseEntity<?> verifyUser(@RequestBody Integer userId){
        try{
            User user = authService.verifyUser(userId);
            return ResponseEntity.ok(user);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante la verifica");
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody User user){
        try{
            authService.deleteUser(user);
            return ResponseEntity.ok(HttpStatus.ACCEPTED);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Errore durante l'eliminazione dell'utente");
        }
    }

    public static class LoginRequest{
        private String email;
        private String password;

        public String getEmail(){ return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest{
        private String username;
        private String email;
        private String password;
        private Role role;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassoword(String password) { this.password = password; }

        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
    }

    public static class LoginResponse{
        private String token;
        private Integer id;
        private Role role;
        private boolean firstLogin;
        private String username;

        public LoginResponse(String token, Integer id, Role role, boolean firstLogin, String username){
            this.token = token;
            this.id = id;
            this.role = role;
            this.firstLogin = firstLogin;
            this.username = username;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public Integer getId(){ return this.id; }
        public void setId(Integer id){ this.id = id; }

        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }

        public boolean isFirstLogin() { return firstLogin; }
        public void setFirstLogin(boolean firstLogin) { this.firstLogin = firstLogin; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
}