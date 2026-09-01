import { Component, inject, OnInit, signal } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';
import { HttpClientService } from '../service/http-client.service';
import { not } from 'rxjs/internal/util/not';
import { MatBadgeModule } from '@angular/material/badge';
import { DatePipe } from '@angular/common';
import { AppNotification } from '../model/interface/AppNotification';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    MatToolbarModule, 
    MatButtonModule, 
    MatIconModule, 
    MatMenuModule,
    MatBadgeModule,
    DatePipe,
  ],
  templateUrl: './header.component.html',
})
export class HeaderComponent implements OnInit {
  private httpClient = inject(HttpClientService);

  public notifiche = signal<AppNotification[]>([]);

  get nonLette(): AppNotification[] {
    return this.notifiche().filter((n) => !n.seen);
  }

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadNotifiche();
  }

  loadNotifiche() {
    const userId = this.authService.getId;
    if(!userId) return;

    this.httpClient.getNotifiche(userId).subscribe({
      next: (res: any[]) => {
          const mappate = res
            .map((n) => new AppNotification(n))
            .sort((a, b) => b.time.getTime() - a.time.getTime());
          this.notifiche.set(mappate);
      },
    });
  }

  segnaComeLetta(notifica: AppNotification) {
    if(notifica.seen) return;

    this.httpClient.segnaComeLetta(notifica.id).subscribe({
      next: () => {
        notifica.seen = true;
        this.notifiche.set([...this.notifiche()]);
      },
    });
  }

  segnaTutteComeLette() {
    const userId = this.authService.getId;
    if(!userId) return;

    this.httpClient.segnaTutteComeLette(userId).subscribe({
      next: () => this.loadNotifiche(),
    });
  }

  iconaPerTipo(tipo: string): string {
    switch (tipo) {
      case 'HIGH_GLYCEMIA_ALERT':
        return 'error';
      case 'HIGH_GLYCEMIA_WARNING':
        return 'warning';
      case 'THERAPY_NOT_FOLLOWED':
        return 'report_problem';
      case 'MISSED_MEDICINE':
        return 'medication';
      default:
        return 'notifications';
    }
  }

  colorePerTipo(tipo: string): string {
    switch (tipo) {
      case 'HIGH_GLYCEMIA_ALERT':
        return 'text-red-600';
      case 'HIGH_GLYCEMIA_WARNING':
      case 'THERAPY_NOT_FOLLOWED':
        return 'text-amber-500';
      default:
        return 'text-blue-500';
    }
  }

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
