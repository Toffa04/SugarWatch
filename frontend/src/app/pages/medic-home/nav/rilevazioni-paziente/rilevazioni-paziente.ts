import { Component, effect, inject, signal, viewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { HttpClientService } from '../../../../service/http-client.service';
import { Patient } from '../../../../model/Patient';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { GlycemiaReading } from '../../../../model/GlycemiaReading';

@Component({
  selector: 'app-rilevazioni-paziente',
  imports: [MatDialogModule, MatButtonModule, MatTableModule, MatSortModule, MatPaginatorModule],
  templateUrl: './rilevazioni-paziente.html',
  styles: ``,
})
export class RilevazioniPaziente {
  public snackbar = inject(MatSnackBar);

  readonly dialogRef = inject(MatDialogRef<RilevazioniPaziente>);
  public paziente = signal<Patient>(inject(MAT_DIALOG_DATA));

  public dataRilevazioni = new MatTableDataSource<GlycemiaReading>([]);
  public displayedColumns = ['glycemiaLevel', 'dateTime', 'beforeMeal', 'symptoms'];

  sort = viewChild<MatSort>(MatSort);
  paginator = viewChild<MatPaginator>(MatPaginator);
  paginatorSize = signal(50);

  constructor(private httpClient: HttpClientService) {
    this.loadData();
    effect(() => {
      this.dataRilevazioni.sort = this.sort() ?? null;
    });
    effect(() => {
      this.dataRilevazioni.paginator = this.paginator() ?? null;
    });
  }

  loadData() {
    this.httpClient.getRilevazioniGiornaliere(this.paziente().id).subscribe({
      next: (res) => {
        this.dataRilevazioni.data = res;
      },
      error: (err) => {
        this.snackbar.open('Errore nel recuperare i dati', 'Ok');
      },
    });
  }
}
