import { DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MedicineIntake, Therapy } from '../../../../model/MedicineIntake';
import { AuthService } from '../../../../service/auth.service';
import { HttpClientService } from '../../../../service/http-client.service';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-aggiungi-terapia',
  imports: [
    MatDialogModule,
    MatButtonModule,
    FormsModule,
    MatFormFieldModule,
    MatCheckboxModule,
    ReactiveFormsModule,
    MatInputModule,
    MatOptionModule,
    MatSelectModule,
    DatePipe
  ],
  templateUrl: './aggiungi-terapia.html',
  styles: ``,
})
export class AggiungiTerapia implements OnInit {
   public snackbar = inject(MatSnackBar);

  readonly dialogRef = inject(MatDialogRef<AggiungiTerapia>);
  public terapia = signal<MedicineIntake>(inject(MAT_DIALOG_DATA));

  public therapies = signal<Therapy[]>([]);
  
  public selectedTherapy = new FormControl<Therapy | null>(null, Validators.required);
  public quantity = new FormControl<number | null>(null, [Validators.required, Validators.min(1)]);
  public dateTime = new FormControl<string | null>(null, Validators.required);

  constructor(
    private authService: AuthService,
    private httpClient: HttpClientService,
  ) {}

  ngOnInit(): void {
    this.httpClient.getTerapiePaziente(this.authService.getId!).subscribe({
      next: (res: any[]) => {
        this.therapies.set(res.map((t) => new Therapy(t)));
      },
      error: (err) => {
        console.error('Errore nel recupero delle terapie:', err);
        this.snackbar.open('Impossibile recuperare le terapie attive', 'Ok');
      },
    });
  }

  aggiungiTerapia() {
    if(this.selectedTherapy.invalid || this.quantity.invalid || this.dateTime.invalid){
      this.snackbar.open('Compila correttamente tutti i campi', 'Ok');
      return;
    }

    const therapyId = this.selectedTherapy.value!.id;
    const quantity = this.quantity.value!;
    const dateTime = new Date(this.dateTime.value!);

    this.httpClient.nuovaAssunzioneFarmaco(therapyId, quantity, dateTime).subscribe({
      next: () => {
        this.dialogRef.close();
      },
      error: (err) => {
        console.error('Errore aggiunta assunzione farmaco:', err);
        this.snackbar.open('Errore durante il salvataggio', 'Ok');
      },
    });
  }
}
