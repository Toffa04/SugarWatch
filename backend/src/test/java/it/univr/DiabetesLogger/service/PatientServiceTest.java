package it.univr.DiabetesLogger.service;

import it.univr.DiabetesLogger.model.Medic;
import it.univr.DiabetesLogger.model.Patient;
import it.univr.DiabetesLogger.model.User;
import it.univr.DiabetesLogger.model.enums.Role;
import it.univr.DiabetesLogger.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static io.jsonwebtoken.impl.security.EdwardsCurve.findById;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PatientServiceTest {
    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    public void setup(){
        patient = new Patient(new User("testUsername", "testMail", "testPass", Role.PATIENT), "test", "test",
                LocalDate.of(2000, 1, 1),
                new Medic(new User("testMedicUsername", "testMedicMail", "testMedicPass", Role.MEDIC),
                        "testMedic", "testMedic"));
    }

    @Test
    @Order(1)
    public void createPatientTest() {
        given(patientRepository.save(patient)).willReturn(patient);

        Patient savedPatient = patientService.create(patient);

        System.out.println(savedPatient);
        assertThat(savedPatient).isNotNull();
    }

    @Test
    @Order(2)
    public void getPatientById() {
        given(patientRepository.findById(patient.getId())).willReturn(Optional.of(patient));

        Patient existingPatient = patientService.getById(patient.getId()).get();

        System.out.println(existingPatient);
        assertThat(existingPatient).isNotNull();
    }

    @Test
    @Order(3)
    public void getAllPatients() {
        given(patientRepository.findAll()).willReturn(List.of(patient));

        Iterable<Patient> existingPatient = patientService.getAll();

        System.out.println(existingPatient);
        assertThat(existingPatient).isNotNull();
    }

    @Test
    @Order(4)
    public void updatePatient() {
        given(patientRepository.findById(patient.getId())).willReturn(Optional.of(patient));
        patient.setFirstName("UpdatedFirst");
        patient.setLastName("UpdatedLast");
        given(patientRepository.save(patient)).willReturn(patient);

        Patient updatedPatient = patientService.update(patient.getId(), patient);

        System.out.println(updatedPatient);
        assertThat(updatedPatient.getFirstName()).isEqualTo("UpdatedFirst");
        assertThat(updatedPatient.getLastName()).isEqualTo("UpdatedLast");
    }

    /*@Test
    @Order(5)
    public void deletePatient(){
        given(patientRepository.findById(patient.getId())).willReturn(Optional.of(patient));

        Patient deletedPatient = patientService.delete(patient.getId());

        System.out.println(deletedPatient);
        assertThat(deletedPatient).isEqualTo(patient);
    }*/
}
