import { DatePipe } from "@angular/common";
import { Component, ElementRef, inject, OnInit, signal, ViewChild } from "@angular/core";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { MatOptionModule } from "@angular/material/core";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatSelectModule } from "@angular/material/select";
import { HttpClientService } from "../../../../service/http-client.service";
import { AuthService } from "../../../../service/auth.service";
import { Patient } from "../../../../model/Patient";
import { AppNotification } from "../../../../model/interface/AppNotification";
import { catchError, forkJoin, map, of } from "rxjs";
import { Chart, registerables } from 'chart.js';
import { MatIconModule } from '@angular/material/icon';

Chart.register(...registerables);

@Component({
    selector: 'app-medic-dashboard',
    standalone: true,
    imports: [
        MatIconModule,
        MatFormFieldModule,
        MatSelectModule,
        MatOptionModule,
        ReactiveFormsModule,
        DatePipe
    ],
    templateUrl: './medic-dashboard.html',
})
export class MedicDashboard implements OnInit {
    private httpClient = inject(HttpClientService);
    private authService = inject(AuthService);

    public pazienti = signal<Patient[]>([]);
    public pazientiSopraSoglia = signal<{ patient: Patient; count: number }[]>([]);
    public notifiche = signal<AppNotification[]>([]);
    public selectedPatient = new FormControl<Patient | null>(null);

    @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;
    private chart: Chart | null = null;
    
    ngOnInit(): void {
        this.loadPazienti();
        this.loadNotifiche();
    }
    
    loadPazienti() {
        this.httpClient.getListaPazienti().subscribe({
            next: (pazienti) => {
                this.pazienti.set(pazienti);
                this.loadAlertGlicemia(pazienti);
            },
        });
    }

    // Pazienti fuori soglia di glicemia
    loadAlertGlicemia(pazienti: Patient[]) {
        const richieste = pazienti.map((p) => 
            this.httpClient.getRilevazioniSopraSoglia(p.id).pipe(
                map((readings) => ({ patient: p, count: readings.length })),
                catchError(() => of({ patient: p, count: 0 })),
            ),
        );

        forkJoin(richieste).subscribe({
            next: (risultati) => {
                this.pazientiSopraSoglia.set(risultati.filter((r) => r.count > 0));
            },
        });
    }

    // Notifiche non lette
    loadNotifiche() {
        const userId = this.authService.getId;
        if(!userId) return;

        this.httpClient.getNotificheNonLette(userId).subscribe({
            next: (res: any[]) => {
                this.notifiche.set(res.map((n) => new AppNotification(n)));
            },
        });
    }

    // Andamento glicemia paziente selezionato
    onPatientChange(patient: Patient | null) {
        if(!patient) return;

        this.httpClient.getRilevazioni(patient.id).subscribe({
            next: (readings: any[]) => this.renderChart(readings),
        });
    }

    private renderChart(readings: any[]) {
        const gruppi = new Map<string, number[]>();
        readings.forEach((r) => {
            const d = new Date(r.dateTime);
            const settimana = this.getWeekKey(d);
            if(!gruppi.has(settimana)) gruppi.set(settimana, []);
            gruppi.get(settimana)!.push(r.glycemiaLevel);
        });

        const labels = Array.from(gruppi.keys()).sort();
        const medie = labels.map((k) => {
            const valori = gruppi.get(k)!;
            return valori.reduce((a, b) => a + b, 0) / valori.length;
        });

        if(this.chart) this.chart.destroy();

        this.chart = new Chart(this.chartCanvas.nativeElement, {
            type: 'line',
            data: {
                labels,
                datasets: [
                    {
                        label: 'Glicemia media (mg/dL)',
                        data: medie,
                        borderColor: '#3b82f6',
                        tension: 0.3,
                    },
                ],
            },
            options: {
                responsive: true,
                scales: { y: { beginAtZero: false } },
            },
        });
    }

    private getWeekKey(date: Date): string {
        const year = date.getFullYear();
        const firstJan = new Date(year, 0, 1);
        const days = Math.floor((date.getTime() - firstJan.getTime()) / 86400000);
        const week = Math.ceil((days + firstJan.getDay() + 1) / 7);
        return `${year}-W${week}`;
    }
}