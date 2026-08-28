package it.univr.DiabetesLogger.service;

import it.univr.DiabetesLogger.model.CustomUserDetails;
import it.univr.DiabetesLogger.model.Medic;
import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.model.User;
import it.univr.DiabetesLogger.repository.MedicRepository;
import it.univr.DiabetesLogger.repository.PatientRepository;
import it.univr.DiabetesLogger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static it.univr.DiabetesLogger.model.enums.Role.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicRepository medicRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // 1. trova lo user dall'email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Utente non trovato: " + email));

        // blocca il login se non verificato
        /*
        if(!user.isVerified()){

        }
        */


        // 2. in base al ruolo cerca il profileId corretto
        Integer profileId = switch (user.getRole()){

            case PATIENT -> patientRepository.findByUser(user)
                    .map(Patient::getId)
                    .orElse(null);

            case MEDIC -> medicRepository.findByUser(user)
                    .map(Medic::getId)
                    .orElse(null);

            case ADMIN -> user.getId();

            default -> throw new UsernameNotFoundException("Ruolo non riconosciuto");
        };

        /*if(profileId != null && !user.isVerified()){
           throw new UsernameNotFoundException("Utente non ancora verificato");
        }*/

        return new CustomUserDetails(user, profileId);
    }
}
