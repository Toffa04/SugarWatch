import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-patient-home',
  imports: [
    MatToolbarModule, 
    MatButtonModule, 
    MatIconModule, 
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
  ],
  templateUrl: './patient-home.html',
  styles: ``,
})
export class PatientHome {

  public authService = inject(AuthService);
  constructor(
    private router: Router
  ){

  }

  rilevazioniGiornaliere(){
    this.router.navigate(['/home-patient/rilevazioni-giornaliere']);
  }

  assunzioneFarmaci(){
    this.router.navigate(['/home-patient/assunzione-farmaci']);
  }

  condizioniConcomitanti(){
    this.router.navigate(['/home-patient/condizioni-concomitanti']);
  }
}
