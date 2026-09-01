import { Component, inject } from '@angular/core';
import { HttpClientService } from '../../../../service/http-client.service';
import { AuthService } from '../../../../service/auth.service';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { Patient } from '../../../../model/Patient';
import { Medic } from '../../../../model/Medic';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { AssegnaMedicoDialog } from './assegna-medico/assegna-medico';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-gestione-pazienti',
  imports: [
    MatTableModule,
    MatIconModule,
  ],
  templateUrl: './gestione-pazienti.html',
})
export class GestionePazienti {
  public dataPatient = new  MatTableDataSource<Patient>([]);
  public medici: Medic[] = [];

  public displayedColumns = ["actions", "id", "firstName", "lastName", "birthDate", "medico"];

  readonly dialog = inject(MatDialog);
  public snackbar = inject(MatSnackBar);

  constructor(
    private httpClientService: HttpClientService,
    private authService: AuthService,
  ) {
    this.loadData();
    this.httpClientService.getAllMedic().subscribe({
      next: (res) => (this.medici = res),
    });
  }

  loadData() {
    this.httpClientService.gestionePazienti().subscribe({
      next: (res) => {
        this.dataPatient.data = res;
      },
    });
  }

  assegnaMedico(el: Patient) {
    const dialogRef = this.dialog.open(AssegnaMedicoDialog, {
      width: '400px',
      data: { paziente: el, medici: this.medici },
    });

    dialogRef.afterClosed().subscribe({
      next: (confermato) => {
        if (confermato) this.loadData();
      },
    });
  }
}
