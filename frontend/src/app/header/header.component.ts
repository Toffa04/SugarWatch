import { Component, inject, OnInit } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';
import { HttpClientService } from '../service/http-client.service';
import { not } from 'rxjs/internal/util/not';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [MatToolbarModule, MatButtonModule, MatIconModule, MatMenuModule],
  templateUrl: './header.component.html',
})
export class HeaderComponent{
  private httpClient = inject(HttpClientService);

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  home(){
    switch(this.authService.getRole){
      case "ADMIN":
        this.router.navigate(['/admin']);
        break;
      case "MEDIC":
        this.router.navigate(['/home-medic']);
        break;
      case "PATIENT":
        this.router.navigate(['/home-patient']);
        break;
      default: break;
    }
  }

  public logout() {
    this.authService.logout();
    this.router.navigate(['']);
  }
}
