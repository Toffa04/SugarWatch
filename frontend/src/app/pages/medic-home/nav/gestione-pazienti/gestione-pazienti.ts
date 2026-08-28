import { Component, effect, inject, signal, viewChild } from '@angular/core';
import { HttpClientService } from '../../../../service/http-client.service';
import { Patient } from '../../../../model/Patient';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { RilevazioniPaziente } from '../rilevazioni-paziente/rilevazioni-paziente';
import { ModificaPaziente } from '../modifica-paziente/modifica-paziente';

@Component({
  selector: 'app-gestione-pazienti',
  imports: [
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatSortModule,
    MatPaginatorModule,
  ],
  templateUrl: './gestione-pazienti.html',
  styles: ``,
})
export class GestionePazientiMedic {
  public snackBar = inject(MatSnackBar);

  readonly dialog = inject(MatDialog);

  public dataPazienti = new MatTableDataSource<Patient>([]);
  public displayedColumns = ['actions', 'firstName', 'lastName', 'birthDate', 'medicalHistory'];

  sort = viewChild<MatSort>(MatSort);
  paginator = viewChild<MatPaginator>(MatPaginator);
  paginatorSize = signal(50);

  constructor(private httpClient: HttpClientService) {
    this.loadData();
    effect(() => {
      this.dataPazienti.sort = this.sort() ?? null;
    });
    effect(() => {
      this.dataPazienti.paginator = this.paginator() ?? null;
    });
  }

  loadData() {
    this.httpClient.getPazienti().subscribe({
      next: (res) => {
        this.dataPazienti.data = res;
      },
      error: (err) => {
        this.snackBar.open('Errore nel trovare i pazienti', 'Ok');
      },
    });
  }

  visualizzaRilevazioni(el: Patient) {
    const dialogCongif = new MatDialogConfig();
    dialogCongif.disableClose = true;
    dialogCongif.maxWidth = 1900;
    dialogCongif.data = el;
    const dialogRef = this.dialog.open(RilevazioniPaziente, dialogCongif);
    dialogRef.afterClosed().subscribe({
      next: () => {
        this.loadData();
      },
    });
  }

  modificaDati(el: Patient) {
    const dialogCongif = new MatDialogConfig();
    dialogCongif.disableClose = true;
    dialogCongif.width = '900px';
    dialogCongif.height = '425px';
    dialogCongif.data = el;
    const dialogRef = this.dialog.open(ModificaPaziente, dialogCongif);
    dialogRef.afterClosed().subscribe({
      next: () => {
        this.loadData();
      },
    });
  }
}
