import { Component } from '@angular/core';
import { HttpClientService } from '../../../../service/http-client.service';
import { AuthService } from '../../../../service/auth.service';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { Patient } from '../../../../model/Patient';

@Component({
  selector: 'app-gestione-pazienti',
  imports: [MatTableModule],
  templateUrl: './gestione-pazienti.html',
})
export class GestionePazienti {

  public dataPatient = new  MatTableDataSource<Patient>([]);

  public displayedColumns = ["actions", "id", "firstName", "lastName", "birthDate"];

  constructor(
    private httpClientService: HttpClientService,
    private authService: AuthService,
  ) {
    this.httpClientService.gestionePazienti().subscribe({
      next: (res) =>{
        this.dataPatient.data = res;
      }
    })
  }
}
