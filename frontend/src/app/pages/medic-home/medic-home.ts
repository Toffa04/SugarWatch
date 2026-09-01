import { Component, inject } from '@angular/core';
import { HttpClientService } from '../../service/http-client.service';
import { AuthService } from '../../service/auth.service';
import { Router, RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogRef } from '@angular/material/dialog';


@Component({
  selector: 'app-medic-home',
  imports: [
    MatIconModule,
    RouterModule,
  ],
  templateUrl: './medic-home.html',
  styles: ``,
})
export class MedicHome {
  public authService = inject(AuthService);
  
  constructor(
    private router: Router,
  ) {}

  gestionePazienti(){
    this.router.navigate(['/medic/gestione-pazienti']);
  }

  gestioneTerapie(){
    this.router.navigate(['/medic/gestione-terapie']);
  }

  rilevazioniPaziente(){
    this.router.navigate(['/medic/rilevazioni-paziente']);
  }
}
