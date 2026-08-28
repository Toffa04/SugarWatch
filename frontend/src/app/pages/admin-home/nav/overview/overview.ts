import { Component, inject, OnInit, signal } from '@angular/core';
import { HttpClientService } from '../../../../service/http-client.service';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';

@Component({
  selector: 'app-overview',
  imports: [MatIconModule],
  templateUrl: './overview.html',
})
export class Overview implements OnInit{

  private httpClient = inject(HttpClientService);
  private router = inject(Router);

  public numeroPazienti = signal<number | null>(null);
  public numeroMedici = signal<number | null>(null);
  public numeroInAttesa = signal<number | null>(null);


  ngOnInit(): void {
    this.httpClient.getListaPazienti().subscribe({
      next: (res: any[]) => this.numeroPazienti.set(res.length),
      error: () => this.numeroPazienti.set(null),
    });

    this.httpClient.getListaMedici().subscribe({
      next: (res: any[]) => this.numeroMedici.set(res.length),
      error: () => this.numeroMedici.set(null),
    });

    this.httpClient.getUtentiInAttesa().subscribe({
      next: (res: any[]) => this.numeroInAttesa.set(res.length),
      error: () => this.numeroInAttesa.set(null),
    })
  }
}
