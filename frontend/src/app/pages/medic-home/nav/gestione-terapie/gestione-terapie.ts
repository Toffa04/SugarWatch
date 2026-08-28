import { Component, effect, inject, signal, viewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Patient } from '../../../../model/Patient';
import { Therapy } from '../../../../model/MedicineIntake';
import { catchError, forkJoin, of } from 'rxjs';
import { HttpClientService } from '../../../../service/http-client.service';
import { ModificaTerapia } from '../../../patient-home/nav/modifica-terapia/modifica-terapia';
import el from '@angular/common/locales/el';

@Component({
  selector: 'app-gestione-terapie',
  imports: [
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatSortModule,
    MatPaginatorModule,
    MatChipsModule
  ],
  templateUrl: './gestione-terapie.html',
  styles: ``,
})
export class GestioneTerapie {
  public snackBar = inject(MatSnackBar);
  readonly dialog = inject(MatDialog);

  public dataTerapie = new MatTableDataSource<Therapy>([]);
  public displayedColumns = [
    'actions',
    'paziente',
    'medicine',
    'dosesPerDay',
    'quantity',
    'notes',
    'status',
  ];

  public loading = signal(false);
  private pazienti: Patient[] = [];

  sort = viewChild<MatSort>(MatSort);
  paginator = viewChild<MatPaginator>(MatPaginator);
  paginatorSize = signal(50);

  constructor(private httpClient: HttpClientService) {
    this.loadData();
    effect(() => {
      this.dataTerapie.sort = this.sort() ?? null;
    });
    effect(() => {
      this.dataTerapie.paginator = this.paginator() ?? null;
    });
  }

  loadData() {
    this.loading.set(true);
    
    this.httpClient.getPazienti().subscribe({
      next: (pazienti) => {
        this.pazienti = pazienti ?? [];

        if(this.pazienti.length === 0){
          this.dataTerapie.data = [];
          this.loading.set(false);
          return;
        }

        const richieste = this.pazienti.map((p) => 
          this.httpClient.getTerapieByPatient(p.id).pipe(
            catchError(() => of([] as Therapy[])),
          ),
        );

      forkJoin(richieste).subscribe({
        next: (risultati) => {
          this.dataTerapie.data = risultati.flat();
          this.loading.set(false);
        },
        error: () => {
          this.snackBar.open('Errore nel recupero delle terapie', 'Ok');
          this.loading.set(false);
        },
      });
    },
    error: () => {
      this.snackBar.open('Errore nel trovare i pazienti', 'Ok');
      this.loading.set(false);
    },
  });
}

nomeCompleto(el: Therapy): string {
  if(!el.patient) return '-';
  return `${el.patient.firstName} ${el.patient.lastName}`;
}

nuovaTerapia() {
  const dialogConfig = new MatDialogConfig<ModificaTerpiaData>();
  dialogConfig.disableClose = true;
  dialogConfig.width = '500p';
  dialogConfig.data = { mode: 'create', pazienti: this.pazienti };

  const dialogRef = this.dialog.open(ModificaTerapia, dialogConfig);
  dialogRef.afterClosed().subscribe({
    next: (confermato) => {
      if (confermato) this.loadData();
    },
  });
}

modificaTerapia(el: Therapy) {
  const dialogConfig = new MatDialogConfig<ModificaTerpiaData>();
  dialogConfig.disableClose = true;
  dialogConfig.width = '500px';
  dialogConfig.data = { mode: 'edit', terapia: el };

  const dialogRef = this.dialog.open(ModificaTerapia, dialogConfig);
  dialogRef.afterClosed().subscribe({
    next: (confermato) => {
      if (confermato) this.loadData();
    },
  });
}

sospendiTerapia(el: Therapy) {
  if(!confirm('Sospendere la terapia con ${el.medicine per ${this.nomeCompleto(el)}?')) {
    return;
  }

  this.httpClient.sospendiTerapia(el.id).subscribe({
    next: () => {
      this.snackBar.open('Terapia sospesa con successo', 'Ok');
      this.loadData();
    },
    error: () => {
      this.snackBar.open('Errore nel sospendere la terapia', 'Ok');
    },
  });
}
}