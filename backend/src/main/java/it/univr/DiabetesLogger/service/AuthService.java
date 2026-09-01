package it.univr.DiabetesLogger.service;

import it.univr.DiabetesLogger.controller.AuthController;
import it.univr.DiabetesLogger.model.*;
import it.univr.DiabetesLogger.model.enums.Role;
import it.univr.DiabetesLogger.repository.MedicRepository;
import it.univr.DiabetesLogger.repository.PatientRepository;
import it.univr.DiabetesLogger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicRepository medicRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public AuthController.LoginResponse login(String email, String password){

        // 1. autentica -> usa CustomUserDetailsService + BCrypt
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // 2. carica l'utente
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        // 3. controlla se è il primo login
        boolean firstLogin = switch (user.getRole()) {
            case PATIENT -> patientRepository.findByUser(user).isEmpty();
            case MEDIC -> medicRepository.findByUser(user).isEmpty();
            case ADMIN -> false;
        };

        // 3. genera e ritorna il token
        return new AuthController.LoginResponse(jwtService.generationToken(email, user.getRole()), user.getId(), user.getRole(), firstLogin, user.getUsername()) ;
    }

    public User registerUser(String username, String email, String password, Role role){

        // 1. controlla duplicati
        if(userRepository.findByEmail(email).isPresent()){
            throw new RuntimeException("Email gia' in uso");
        }
        if(userRepository.findByUsername(username).isPresent()){
            throw new RuntimeException("Username gia' in uso");
        }

        // 2. hash della password
        String hashedPassword = passwordEncoder.encode(password);

        // 3. salva e ritorna
        User user = new User(username, email, hashedPassword, role);
        user.setVerified(false);
        return userRepository.save(user);
    }


    // lista utenti in attesa di verifica
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    // lista utenti in attesa di verifica
    /*public List<User> getPendingUsers(){
        return userRepository.findByVerified(false);
    }*/

    // verifica un utente
    public User verifyUser(Integer userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        if(user.isVerified()){
            throw new RuntimeException("Utente gia' verificato");
        }

        user.setVerified(true);
        return userRepository.save(user);
    }

    public void deleteUser(User user){
        userRepository.delete(user);
    }

    //ritorna il ruolo dello user per caricare i dati corretti
    public Role getRole(String email){
        return userRepository.findByEmail(email).get().getRole();
    }
}
