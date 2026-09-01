import { Component, inject } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Patient } from '../../../../model/Patient';
import { HttpClientService } from '../../../../service/http-client.service';

@Component({
  selector: 'app-modifica-paziente',
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatCheckboxModule,
    ReactiveFormsModule,
    FormsModule,
    MatInputModule
  ],
  templateUrl: './modifica-paziente.html',
  styles: ``,
})
export class ModificaPaziente {
  public snackbar = inject(MatSnackBar);
  readonly dialogRef = inject(MatDialogRef<ModificaPaziente>);
  public paziente = inject<Patient>(MAT_DIALOG_DATA);

  public isSmoker = { value: this.paziente.isSmoker ?? false };
  public isExSmoker = { value: this.paziente.isExSmoker ?? false };
  public hasAlcoholDependency = { value: this.paziente.hasAlcoholDependency ?? false };
  public hasObesity = { value: this.paziente.hasObesity ?? false };

  public medicalHistory = new FormControl<string | null>(this.paziente.medicalHistory ?? null);

  constructor(private httpClient: HttpClientService) {}

  conferma() {
    const payload = {
      isSmoker: this.isSmoker.value,
      isExSmoker: this.isExSmoker.value,
      hasAlcoholDependency: this.hasAlcoholDependency.value,
      hasObesity: this.hasObesity.value,
      medicalHistory: this.medicalHistory.value,
    };

    this.httpClient.aggiornaPaziente(this.paziente.id, payload).subscribe({
      next: () => {
        this.dialogRef.close(true);
      },
      error: (err: unknown) => {
        console.error('Errore nella modifica del paziente:', err);
        this.snackbar.open('Errore durante il salvataggio', 'Ok');
      },
    });
  }
}
