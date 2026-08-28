import { Component, inject, signal } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerInputEvent, MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../service/auth.service';
import { HttpClientService } from '../../service/http-client.service';
import { PatientRequest } from '../../model/PatienRequest';
import { Patient } from '../../model/Patient';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';

@Component({
  selector: 'app-patient-create',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    FormsModule,
    ReactiveFormsModule,
    MatIconModule,
    MatButtonModule,
    MatCheckboxModule,
  ],
  templateUrl: './patient-create.html',
  styles: ``,
})
export class PatientCreate {
  private snackBar = inject(MatSnackBar);

  public firstName = new FormControl();
  public lastName = new FormControl();
  public birthDate: Date | undefined;

  public isSmoker = signal(false);
  public exSmoker = signal(false);
  public hasAlcoholDependency = signal(false);
  public hasObesity = signal(false);
  public medicalHistory = new FormControl();

  constructor(
    private httpClientService: HttpClientService,
    private router: Router,
    private authService: AuthService,
  ) {}

  selezioneDataNascita(event: MatDatepickerInputEvent<Date>) {
    this.birthDate = event.value!;
  }

  finish() {
    if (
      this.firstName.value === null ||
      this.firstName.value === '' ||
      this.lastName.value === null ||
      this.lastName.value === '' ||
      this.medicalHistory.value === null ||
      this.birthDate === null
    ) {
      this.snackBar.open('Nome, cognome o data di nascita non completi.', 'Ok');
    } else {
      const paziente = new PatientRequest(
        this.authService.getId!,
        this.firstName.value,
        this.lastName.value,
        this.birthDate!,
        this.isSmoker(),
        this.exSmoker(),
        this.hasAlcoholDependency(),
        this.hasObesity(),
        this.medicalHistory.value ?? '',
      );
      this.httpClientService.creaPaziente(paziente).subscribe({
        next: () => {
          this.snackBar.open('Paziente creato correttamente, rieseguire il login', 'Ok');
          this.authService.logout();
          this.router.navigate(['']);
        },
        error: (err) => {
          this.snackBar.open(err, "Ok");
        }
      });
    }
  }
}
