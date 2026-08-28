import { Component, inject } from '@angular/core';
import { ConcomitantTherapy, ConditionType, Pathology, Symptom } from '../../../../model/ConditionType';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../../../service/auth.service';
import { HttpClientService } from '../../../../service/http-client.service';
import { Q } from '@angular/cdk/keycodes';

export interface AggiungiCondizioneData {
  tipo: ConditionType;
  elemento?: Symptom | Pathology | ConcomitantTherapy;
}
@Component({
  selector: 'app-aggiungi-condizione',
  imports: [
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatInputModule,
  ],
  templateUrl: './aggiungi-condizione.html',
  styles: ``,
})
export class AggiungiCondizione {
  public snackbar = inject(MatSnackBar);
  readonly dialogRef = inject(MatDialogRef<AggiungiCondizione>);
  public data = inject<AggiungiCondizioneData>(MAT_DIALOG_DATA);

  public isConcomitantTherapy = this.data.tipo === 'concomitationTherapy';
  public isModifica = !!this.data.elemento;

  public description = new FormControl<string | null>(
    (this.data.elemento as Symptom | Pathology)?.description ?? null,
    this.isConcomitantTherapy ? [] : [Validators.required],
  );

  public medicine = new FormControl<string | null>(
    (this.data.elemento as ConcomitantTherapy)?.medicine ?? null,
    this.isConcomitantTherapy ? [Validators.required] : [],
  );

  public reason = new FormControl<string | null>((this.data.elemento as ConcomitantTherapy)?.reason ?? null);

  public startDate = new FormControl<string | null>(
    this.toDateInputValue(this.data.elemento?.startDate) ?? null,
    Validators.required,
  );

  public endDate = new FormControl<string | null>(this.toDateInputValue(this.data.elemento?.endDate));
  public notes = new FormControl<string | null>(this.data.elemento?.notes ?? null);

  constructor(
    private authService: AuthService,
    private httpClient: HttpClientService,
  ) {}

  private toDateInputValue(date?: Date | null): string | null {
    if(!date) return null;
    return new Date(date).toISOString().substring(0, 10);
  }

  conferma() {
    if(this.startDate.invalid || (this.isConcomitantTherapy ? this.medicine.invalid : this.description.invalid)) {
      this.snackbar.open('Compila correttamente tutti i campi obbligatori', 'Ok');
      return;
    }

    const payload: any = {
      startDate: this.startDate.value,
      endDate: this.endDate.value || null,
      notes: this.notes.value,
    };

    if(this.isConcomitantTherapy) {
      payload.medicine = this.medicine.value;
      payload.reason = this.reason.value;
    } else {
      payload.description = this.description.value;
    }

    const patientId = this.authService.getId!;

    const request$ = this.isModifica
      ? this.httpClient.aggiornaCondizione(this.data.tipo, patientId, this.data.elemento!.id!, payload)
      : this.httpClient.nuovaCondizione(this.data.tipo, patientId, payload);

    request$.subscribe({
      next: () => {
        this.dialogRef.close();
      },
      error: (err) => {
        console.error('Errore nel salvataggio:', err);
        this,this.snackbar.open('Errore durante il salvataggio', 'Ok');
      },
    });
  }
}
