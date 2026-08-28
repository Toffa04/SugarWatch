import { Component } from '@angular/core';
import { AuthService } from '../../service/auth.service';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
@Component({
  selector: 'app-admin-home',
  imports: [
    MatButtonModule, 
    MatIconModule,
    MatToolbarModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
  ],
  templateUrl: './admin-home.html',
})
export class AdminHome {
  public email: string | null;

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {
    this.email = authService.getEmail;
  }

  gestioneUtenti() {
    this.router.navigate(['/admin/gestione-utenti']);
  }

  gestionePazienti() {
    this.router.navigate(['/admin/gestione-pazienti']);
  }

  gestioneMedici() {
    this.router.navigate(['/admin/gestione-medici']);
  }
}
