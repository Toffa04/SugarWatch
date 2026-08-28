import { Component, effect, inject, signal, viewChild } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MedicineIntake } from '../../../../model/MedicineIntake';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { HttpClientService } from '../../../../service/http-client.service';
import { AuthService } from '../../../../service/auth.service';
import { AggiungiTerapia } from '../aggiungi-terapia/aggiungi-terapia';
import { ModificaTerapia } from '../modifica-terapia/modifica-terapia';
import { MatSelectModule } from '@angular/material/select';
import { DatePipe } from '@angular/common';


@Component({
  selector: 'app-assunzione-farmaci',
  imports: [
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    MatSortModule,
    MatPaginatorModule,
    MatSelectModule,
    DatePipe,
  ],
  templateUrl: './assunzione-farmaci.html',
  styles: ``,
})
export class AssunzioneFarmaci {
  public dataAssunzioni = new MatTableDataSource<MedicineIntake>([]);

  public displayColumns = ['actions', 'therapy', 'quantity', 'matchesTherapy', 'dateTime'];

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
      this.dataAssunzioni.sort = this.sort() ?? null;
    });
    effect(() => {
      this.dataAssunzioni.paginator = this.paginator() ?? null;
    });
  }

  loadData() {
    this.httpClient.getAssunzioneFarmaci(this.authService.getId!).subscribe({
      next: (res) => {
        this.dataAssunzioni.data = res;
      },
    });
  }

  aggiungiTerapia() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.width = '900px';
    dialogConfig.height = '425px';
    dialogConfig.data = new MedicineIntake();
    const dialogRef = this.dialog.open(AggiungiTerapia, dialogConfig);
    dialogRef.afterClosed().subscribe({
      next: () => {
        this.loadData();
      },
    });
  }

  modificaTerapia(el: MedicineIntake) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.width = '900px';
    dialogConfig.height = '425px';
    dialogConfig.data = el;
    const dialogRef = this.dialog.open(ModificaTerapia, dialogConfig);
    dialogRef.afterClosed().subscribe({
      next: () => {
        this.loadData();
      },
    });
  }
}
