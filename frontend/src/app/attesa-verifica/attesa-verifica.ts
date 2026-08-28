import { Component } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-attesa-verifica',
  imports: [],
  templateUrl: './attesa-verifica.html',
})
export class AttesaVerifica {
  constructor(private router: Router,
    private authService : AuthService){}

  tornaAlLogin(): void {
    this.authService.logout();
    this.router.navigate(['']);
  }
}
