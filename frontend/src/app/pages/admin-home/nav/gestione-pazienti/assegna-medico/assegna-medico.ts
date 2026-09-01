import { Component, inject } from '@angular/core';
import { Patient } from '../../../../../model/Patient';
import { Medic } from '../../../../../model/Medic';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormField, MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HttpClientService } from '../../../../../service/http-client.service';

export interface AssegnaMedicoData {
  paziente: Patient;
  medici: Medic[];
}

@Component({
  selector: 'app-assegna-medico',
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatSelectModule,
    MatOptionModule,
    ReactiveFormsModule
  ],
  templateUrl: './assegna-medico.html',
  styles: '',
})
export class AssegnaMedicoDialog {

  readonly dialogRef = inject(MatDialogRef<AssegnaMedicoDialog>);
  public snackbar = inject(MatSnackBar);
  public data = inject<AssegnaMedicoData>(MAT_DIALOG_DATA);

  public selectedMedic = new FormControl<Medic | null>(null, Validators.required);

  constructor(private httpClient: HttpClientService) {}

  conferma() {
    if(this.selectedMedic.invalid) {
      this.snackbar.open('Seleziona un medico', 'Ok');
      return;
    }
    const medicId = this.selectedMedic.value!.id;
    this.httpClient.assegnaMedico(this.data.paziente.id, medicId).subscribe({
      next: () => this.dialogRef.close(true),
      error: () => this.snackbar.open('Errore durante l\'assegnazione', 'Ok'),
    });
  }
}
