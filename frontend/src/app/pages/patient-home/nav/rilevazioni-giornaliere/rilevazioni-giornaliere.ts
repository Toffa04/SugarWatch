import { Component, effect, inject, signal, viewChild } from '@angular/core';
import { AuthService } from '../../../../service/auth.service';
import { HttpClientService } from '../../../../service/http-client.service';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { GlycemiaReading } from '../../../../model/GlycemiaReading';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { GlycemiaReadingRequest } from '../../../../model/GlycemiaReadingRequest';
import { AggiungiRilevazione } from '../aggiungi-rilevazione/aggiungi-rilevazione';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { ModificaRilevazione } from '../modifica-rilevazione/modifica-rilevazione';

@Component({
  selector: 'app-rilevazioni-giornaliere',
  imports: [
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatSortModule,
    MatPaginatorModule,
  ],
  templateUrl: './rilevazioni-giornaliere.html',
  styles: ``,
})
export class RilevazioniGiornaliere {
  public dataRilevazioni = new MatTableDataSource<GlycemiaReading>([]);
  public displayedColumns = ['actions', 'glycemiaLevel', 'dateTime', 'beforeMeal', 'symptoms'];

  sort = viewChild<MatSort>(MatSort);
  paginator = viewChild<MatPaginator>(MatPaginator);
  paginatorSize = signal(50);

  readonly dialog = inject(MatDialog);

  constructor(
    private authService: AuthService,
    private httpClient: HttpClientService,
  ) {
    this.loadData();
    effect(() => {
      this.dataRilevazioni.sort = this.sort() ?? null;
    });
    effect(() => {
      this.dataRilevazioni.paginator = this.paginator() ?? null;
    });
  }

  loadData() {
    this.httpClient.getRilevazioniGiornaliere(this.authService.getId!).subscribe({
      next: (res) => {
        this.dataRilevazioni.data = res;
      },
    });
  }

  aggiungiRilevazione() {
    const dialogCongif = new MatDialogConfig();
    dialogCongif.disableClose = true;
    dialogCongif.width = '900px';
    dialogCongif.height = '425px';
    dialogCongif.data = new GlycemiaReadingRequest();
    const dialogRef = this.dialog.open(AggiungiRilevazione, dialogCongif);
    dialogRef.afterClosed().subscribe({
      next: () => {
        this.loadData();
      },
    });
  }

  modificaRilevazione(el: GlycemiaReading) {
    const dialogCongif = new MatDialogConfig();
    dialogCongif.disableClose = true;
    dialogCongif.width = '900px';
    dialogCongif.height = '425px';
    dialogCongif.data = el;
    const dialogRef = this.dialog.open(ModificaRilevazione, dialogCongif);
    dialogRef.afterClosed().subscribe({
      next: () => {
        this.loadData();
      },
    });
  }
}
