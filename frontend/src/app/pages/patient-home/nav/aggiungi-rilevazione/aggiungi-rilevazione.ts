import { Component, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { AuthService } from '../../../../service/auth.service';
import { HttpClientService } from '../../../../service/http-client.service';
import { MatButtonModule } from '@angular/material/button';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatInputModule } from '@angular/material/input';
import { DatePipe } from '@angular/common';
import { GlycemiaReadingRequest } from '../../../../model/GlycemiaReadingRequest';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-aggiungi-rilevazione',
  imports: [
    MatDialogModule,
    MatButtonModule,
    FormsModule,
    MatFormFieldModule,
    MatCheckboxModule,
    ReactiveFormsModule,
    MatInputModule,
    DatePipe
  ],
  templateUrl: './aggiungi-rilevazione.html',
  styles: ``,
})
export class AggiungiRilevazione {

  public snackbar = inject(MatSnackBar);

  readonly dialogRef = inject(MatDialogRef<AggiungiRilevazione>);
  public rilevazione = signal<GlycemiaReadingRequest>(inject(MAT_DIALOG_DATA));

  public glycemiaLevel = new FormControl();
  public symptoms = new FormControl();
  public beforeMeal = signal(true);

  constructor(
    private authService: AuthService,
    private httpClient: HttpClientService,
  ) {}

  aggiungiRilevazione() {
    if(this.glycemiaLevel.value > 0){
      this.rilevazione().glycemiaLevel = this.glycemiaLevel.value;
      this.rilevazione().beforeMeal = this.beforeMeal();
      this.rilevazione().symptoms = this.symptoms.value;
      this.httpClient.nuovaRilevazione(this.rilevazione()).subscribe({
        next: () => {
          this.dialogRef.close();
        }
      });
    }else{
      this.snackbar.open("Livello glicemia non valido", "Ok");
    }
  }
}
