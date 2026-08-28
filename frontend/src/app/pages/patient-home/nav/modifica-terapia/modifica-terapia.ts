import { DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AggiungiTerapia } from '../aggiungi-terapia/aggiungi-terapia';
import { MedicineIntake, Therapy } from '../../../../model/MedicineIntake';
import { HttpClientService } from '../../../../service/http-client.service';
import { AuthService } from '../../../../service/auth.service';
import { MatOptionModule } from '@angular/material/core';

@Component({
  selector: 'app-modifica-terapia',
  imports: [
    MatDialogModule,
    MatButtonModule,
    FormsModule,
    MatFormFieldModule,
    MatCheckboxModule,
    ReactiveFormsModule,
    MatInputModule,
    MatOptionModule,
    DatePipe,
  ],
  templateUrl: './modifica-terapia.html',
  styles: ``,
})
export class ModificaTerapia implements OnInit {
  public snackbar = inject(MatSnackBar);

  readonly dialogRef = inject(MatDialogRef<ModificaTerapia>);
  public terapia = signal<MedicineIntake>(inject(MAT_DIALOG_DATA));

  public therapies = signal<Therapy[]>([]);
  public selectedTherapy = new FormControl<Therapy | null>(this.terapia().therapy, Validators.required);
  public quantity = new FormControl<number | null>(this.terapia().quantity, [Validators.required, Validators.min(1)]);
  public matchesTherapy = signal(this.terapia().matchesTherapy);

  constructor(private authService: AuthService, private httpClient: HttpClientService) {}
  
  ngOnInit(): void {
    this.httpClient.getTerapiePaziente(this.authService.getId!).subscribe({
      next: (res: any[]) => {
        const terapie = res.map((t) => new Therapy(t));
        this.therapies.set(terapie);

        // riallinea la selezione della lita Therapy
        const attuale = terapie.find((t) => t.id === this.terapia().therapy?.id);
        if(attuale) {
          this.selectedTherapy.setValue(attuale);
        }
      },
      error: (err) => {
        console.error('Errore nel recupero delle terapie:', err);
        this.snackbar.open('Impossibile recuperare le terapie attive', 'Ok');
      },
    });
  }

  aggiornaTerapia() {
    if(this.selectedTherapy.invalid || this.quantity.invalid){
      this.snackbar.open('Dati non validi', 'Ok');
      return;
    }

    const invariato = 
      this.selectedTherapy.value?.id === this.terapia().therapy?.id &&
      this.quantity.value === this.terapia().quantity &&
      this.matchesTherapy() === this.terapia().matchesTherapy;

    if(invariato){
      this.snackbar.open('Dati non modificati', 'Ok');
      return;
    }
    
    const terapiaAggiornata = new MedicineIntake({
      ...this.terapia(),
      therapy: this.selectedTherapy.value,
      quantity: this.quantity.value,
      matchesTherapy: this.matchesTherapy(),
    });

    this.httpClient.aggiornaAssunzioneFarmaco(terapiaAggiornata).subscribe({
      next: () => {
        this.dialogRef.close();
      },
      error : (err) => {
        console.error('Errore nella modifica dell\'assunzione:', 'Ok');
        this.snackbar.open('Errore nella modifica della rilevazione', 'Ok');
      },
    });
  }
}
