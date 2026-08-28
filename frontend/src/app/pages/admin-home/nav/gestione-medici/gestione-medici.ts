import { Component } from '@angular/core';
import { Medic } from '../../../../model/Medic';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { AuthService } from '../../../../service/auth.service';
import { HttpClientService } from '../../../../service/http-client.service';

@Component({
  selector: 'app-gestione-medici',
  imports: [MatTableModule],
  templateUrl: './gestione-medici.html',
})
export class GestioneMedici {
  public dataMedic = new  MatTableDataSource<Medic>([]);

  public displayedColumns = ["actions", "id", "firstName", "lastName", "email"];

  constructor(
    private httpClientService: HttpClientService,
    private authService: AuthService,
  ) {
    this.httpClientService.getAllMedic().subscribe({
      next: (res) =>{
        this.dataMedic.data = res;
      }
    })
  }
}
