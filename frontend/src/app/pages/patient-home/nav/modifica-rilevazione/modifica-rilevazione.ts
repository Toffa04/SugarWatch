import { Component, inject, signal } from '@angular/core';
import { HttpClientService } from '../../../../service/http-client.service';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AggiungiRilevazione } from '../aggiungi-rilevazione/aggiungi-rilevazione';
import { DatePipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { GlycemiaReading } from '../../../../model/GlycemiaReading';

@Component({
  selector: 'app-modifica-rilevazione',
  imports: [
    MatDialogModule,
    MatButtonModule,
    FormsModule,
    MatFormFieldModule,
    MatCheckboxModule,
    ReactiveFormsModule,
    MatInputModule,
    DatePipe,
  ],
  templateUrl: './modifica-rilevazione.html',
  styles: ``,
})
export class ModificaRilevazione {
  public snackbar = inject(MatSnackBar);

  readonly dialogRef = inject(MatDialogRef<AggiungiRilevazione>);
  public rilevazione = signal<GlycemiaReading>(inject(MAT_DIALOG_DATA));

  public glycemiaLevel = new FormControl(this.rilevazione().glycemiaLevel);
  public symptoms = new FormControl<string | null>(this.rilevazione().symptoms);
  public beforeMeal = signal(this.rilevazione().beforeMeal);

  constructor(private httpClient: HttpClientService) {}

  aggiornaRilevazione() {
    if (
      this.glycemiaLevel.value === this.rilevazione().glycemiaLevel &&
      this.symptoms.value === this.rilevazione().symptoms
    ) {
      this.snackbar.open('Dati non modificati', 'Ok');
    } else if (this.glycemiaLevel.value! < 0 || this.glycemiaLevel.value === null) {
      this.snackbar.open('Dati non validi', 'Ok');
    } else {
      const rilevazioneAggiornata = {
        ...this.rilevazione(),
        glycemiaLevel: this.glycemiaLevel.value!,
        symptoms: this.symptoms.value,
        beforeMeal: this.beforeMeal(),
      };
      this.httpClient.aggiornaRilevazione(rilevazioneAggiornata).subscribe({
        next: () => {
          this.dialogRef.close();
        },
        error: (err) => {
          this.snackbar.open('Errore nella modifca della rilevazione', 'Ok');
        },
      });
    }
  }
}
