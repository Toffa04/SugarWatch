import { Component } from '@angular/core';
import { HttpClientService } from '../../service/http-client.service';
import { AuthService } from '../../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-medic-home',
  imports: [],
  templateUrl: './medic-home.html',
  styles: ``,
})
export class MedicHome {
  constructor(
    private router: Router,
  ) {}

  gestionePazienti(){
    this.router.navigate(['/medic/gestione-pazienti']);
  }

  gestioneTerapie(){
    this.router.navigate(['/medic/gestione-terapie']);
  }
}
