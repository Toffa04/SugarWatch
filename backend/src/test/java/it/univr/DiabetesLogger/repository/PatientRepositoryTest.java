package it.univr.DiabetesLogger.repository;

import it.univr.DiabetesLogger.model.Medic;
import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.model.User;
import it.univr.DiabetesLogger.model.enums.Role;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PatientRepositoryTest {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicRepository medicRepository;

    @Test
    @Order(1)
    @Rollback(value = false)
    public void createPatientTest(){
        User patientUser = userRepository.save(
                new User("testUsername", "testMail", "pass",  Role.PATIENT)
        );
        User medicUser = userRepository.save(
                new User("testMedicUsername", "testMedicMail", "medicPass", Role.MEDIC)
        );
        Medic medic = medicRepository.save(
                new Medic(medicUser, "testMedic", "lastName")
        );

        Patient patient = new Patient(
                patientUser,
                "testFirstName",
                "testLastName",
                LocalDate.of(200, 1, 1),
                medic
        );
        patientRepository.save(patient);

        System.out.println(patient);
        assertThat(patient.getId()).isGreaterThan(0);
    }

    @Test
    @Order(2)
    public void getPatientByIdTest(){
        Patient found = patientRepository.findById(1).get();

        System.out.println(found);
        assertThat(found.getId()).isEqualTo(1);
    }

    @Test
    @Order(3)
    public void getAllPatientsTest(){
        List<Patient> patients = patientRepository.findAll();

        System.out.println(patients);
        assertThat(patients.size()).isGreaterThan(0);
    }

    @Test
    @Order(4)
    @Rollback(value = true)
    public void updatePatientTest() {
        Patient patient = patientRepository.findById(1).get();
        patient.setFirstName("UpdateFirstName");
        patient.setLastName("UpdateLastName");
        Patient updated = patientRepository.save(patient);

        System.out.println(updated);
        assertThat(updated.getFirstName()).isEqualTo("UpdatedFirstName");
        assertThat(updated.getLastName()).isEqualTo("UpdatedLastName");
    }

    /*@Test
    @Order(5)
    public void deletePatientTest() {
        Patient patient = patientRepository.findById(1).get();
        patientRepository.delete(patient);

        Patient deleted = patientRepository.findById(1).orElse(null);
        assertThat(deleted).isNull();
    }*/
}
