import { Component } from '@angular/core';
import { User } from '../../../../model/User';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { AuthService } from '../../../../service/auth.service';
import { HttpClientService } from '../../../../service/http-client.service';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

@Component({
  selector: 'app-gestione-utenti',
  imports: [MatTableModule, MatButtonModule, MatIconModule, MatTooltipModule],
  templateUrl: './gestione-utenti.html',
})
export class GestioneUtenti {
  public dataPendingUser = new MatTableDataSource<User>([]);

  public displayedColumns = ['actions', 'id', 'username', 'email', 'role', 'verified'];

  constructor(
    private httpClientService: HttpClientService,
    private authService: AuthService,
  ) {
    this.loadData();
  }

  loadData(){
    this.httpClientService.getAllUser().subscribe({
      next: (res) => {
        this.dataPendingUser.data = res;
      },
    });
  }

  verifica(el: User) {
    if (confirm("Sei di voler verificare l'utente?")) {
      this.httpClientService.verifyUser(el.id).subscribe({
        next: () => {
          this.loadData();
        }
      });
    }
  }

  modifica(el: User) {}

  elimina(el: User) {
    if (confirm("Sei di voler eliminare l'utente?")) {
      this.httpClientService.deleteUser(el).subscribe({
        next: () => {
          this.loadData();
        }
      });
    }
  }
}
