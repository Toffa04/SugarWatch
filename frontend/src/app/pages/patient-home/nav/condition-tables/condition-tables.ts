import { DatePipe } from '@angular/common';
import { Component, inject, Input, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ConcomitantTherapy, ConditionType, Pathology, Symptom } from '../../../../model/ConditionType';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AuthService } from '../../../../service/auth.service';
import { HttpClientService } from '../../../../service/http-client.service';
import { AggiungiCondizione, AggiungiCondizioneData } from '../aggiungi-condizione/aggiungi-condizione';

type Condizione = Symptom | Pathology | ConcomitantTherapy;
@Component({
  selector: 'app-condition-tables',
  imports: [
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    DatePipe
  ],
  templateUrl: './condition-tables.html',
  styles: ``,
})
export class ConditionTables implements OnInit{
  @Input({ required: true }) tipo!: ConditionType;

  public dataSource = new MatTableDataSource<Condizione>([]);

  get displayedColumns(): string[] {
    return this.tipo === 'concomitationTherapy'
      ? ['actions', 'medicine', 'reason', 'startDate', 'endDate', 'notes']
      : ['actions', 'description', 'startDate', 'endDate', 'notes'];
  }

  readonly dialog = inject(MatDialog);

  constructor(
    private authService: AuthService,
    private httpClient: HttpClientService,
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData() {
    this.httpClient.getCondizioni(this.tipo, this.authService.getId!).subscribe({
      next: (res: any[]) => {
        console.log('RAW dal backend:', res[0]?.endDate, typeof res[0]?.endDate);
        //this.dataSource.data 
        const mapped = res.map((item) => this.mapItem(item));
        console.log('MAPPATO:', mapped[0]?.endDate);
        this.dataSource.data = mapped;
      },
    });
  }

  private mapItem(item: any): Condizione{
    switch (this.tipo) {
      case 'symptom':
        return new Symptom(item);
      case 'pathology':
        return new Pathology(item);
      case 'concomitationTherapy':
        return new ConcomitantTherapy(item);
    }
  }

  aggiungi() {
    const dialogConfig = new MatDialogConfig<AggiungiCondizioneData>();
    dialogConfig.disableClose = true;
    dialogConfig.width = '500px';
    dialogConfig.data = { tipo: this.tipo };

    const dialogRef = this.dialog.open(AggiungiCondizione, dialogConfig);
    dialogRef.afterClosed().subscribe({
      next: () => this.loadData(),
    });
  }

  modifica(elemento: Condizione) {
    const dialogConfig = new MatDialogConfig<AggiungiCondizioneData>();
    dialogConfig.disableClose = true;
    dialogConfig.width = '500px';
    dialogConfig.data = { tipo: this.tipo, elemento };

    const dialogRef = this.dialog.open(AggiungiCondizione, dialogConfig);
    dialogRef.afterClosed().subscribe({
      next: () => this.loadData(),
    })
  }
}
