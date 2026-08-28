import { Component, inject, OnInit, signal } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../../../service/auth.service';
import { HttpClientService } from '../../../../service/http-client.service';

interface GlycemiaTrendPoint{
  value: number;
  dateTime: Date;
}

@Component({
  selector: 'app-dashboard',
  imports: [MatIconModule],
  templateUrl: './dashboard.html',
  styles: ``,
})
export class Dashboard implements OnInit{
  private authService = inject(AuthService);
  private httpClient = inject(HttpClientService);

  public username = this.authService.getUsername;

  public greeting = signal(this.computeGreeting());

  public unreadCount = signal<number | null>(null);
  public farmaciPresiOggi = signal(0);
  public farmaciTotaliOggi = signal(0);

  public trendingPoints = signal<GlycemiaTrendPoint[]>([]);
  public ultimaGlicemia = signal<number | null>(null);
  public trendDirection = signal<'up' | 'down' | 'stable' | null>(null);

  ngOnInit(): void{
    const patientId = this.authService.getId!;

    this.loadNotifiche(patientId);
    this.loadFarmaciOggi(patientId);
    this.loadGlycemiaTrend(patientId);

  }

  private computeGreeting(): string{
    const ora = new Date().getHours();
    if (ora < 12) return 'Buongiorno';
    if (ora < 18) return 'Buon pomeriggio';
    return 'Buonasera';
  }

  private loadNotifiche(patientId: number){
    this.httpClient.getNotificheNonLette(patientId).subscribe({
      next: (res: any[]) => this.unreadCount.set(res.length),
      error: () => this.unreadCount.set(null),
    });
  }

  private loadFarmaciOggi(patientId: number){
    this.httpClient.getTerapieAttive(patientId).subscribe({
      next: (terapie: any[]) => {
        this.farmaciTotaliOggi.set(terapie.length);

        this.httpClient.getAssunzioneFarmaci(patientId).subscribe({
          next: (assunzioni: any[]) => {
            const oggi = new Date().toDateString();
            const presiOggi = assunzioni.filter(
              (a) => new Date(a.dateTime).toDateString() === oggi,
            );

            const idUnici = new Set(presiOggi.map((a) => a.therapy?.id));
            this.farmaciPresiOggi.set(idUnici.size);
          },
        });
      },
    });
  }

  private loadGlycemiaTrend(patientId: number){
    this.httpClient.getRilevazioni(patientId).subscribe({
      next: (res: any[]) => {
        const ordinate = res
          .map((r) => ({ value: r.glycemiaLevel, dateTime: new Date(r.dateTime) }))
          .sort((a, b) => a.dateTime.getTime() - b.dateTime.getTime());

        const ultimi = ordinate.slice(-7);
        this.trendingPoints.set(ultimi);

        if(ultimi.length > 0){
          this.ultimaGlicemia.set(ultimi[ultimi.length - 1].value);
        }
        
        if(ultimi.length >= 2){
          const diff = ultimi[ultimi.length - 1].value - ultimi[ultimi.length - 2].value;
          this.trendDirection.set(diff > 3 ? 'up' : diff < -3 ? 'down' : 'stable');
        }
      },
    });
  }

  get maxTrendValue(): number {
    const values = this.trendingPoints().map((p) => p.value);
    return values.length ? Math.max(...values) : 1;
  }

  barHeight(value: number): number{
    const max = this.maxTrendValue;
    return max > 0 ? Math.max((value / max) * 100, 8) : 8;
  }
}
