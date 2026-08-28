import { DATE_PIPE_DEFAULT_OPTIONS, DatePipe } from "@angular/common";
import { MatBadgeModule } from "@angular/material/badge";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatMenuModule } from "@angular/material/menu";
import { HttpClientService } from "../../service/http-client.service";
import { AppNotification } from "../../model/interface/AppNotification";
import { Component, inject, Input, OnInit, signal } from "@angular/core";

@Component({
    selector: 'app-notification-bell',
    imports: [
        MatIconModule,
        MatMenuModule,
        MatBadgeModule,
        MatButtonModule,
        DatePipe
    ],
    templateUrl: './notification-bell.html',
    styles: '',
})

export class NotificationBell implements OnInit {
    @Input({ required: true }) userId!: number;

    private httpClient = inject(HttpClientService);
    public notifiche = signal<AppNotification[]>([]);

    get nonLette(): AppNotification[] {
        return this.notifiche().filter((n) => !n.seen);
    }

    ngOnInit(): void {
        this.loadData();
    }

    loadData() {
        this.httpClient.getNotifiche(this.userId).subscribe({
            next: (res: any[]) => {
                const mappate = res
                    .map((n) => new AppNotification(n))
                    .sort((a, b) => b.time.getTime() - a.time.getTime());
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
        this.httpClient.segnaTutteComeLette(this.userId).subscribe({
            next: () => this.loadData(),
        });
    }

    iconaPerTipo(tipo: string): string {
        switch (tipo) {
            case 'HIGH_GLYCEMIA_ALERT':
                return 'error';
            case 'HIGH_GLYCEMIA_WARNING':
                return 'warning';
            case 'THERAPY_NOT_FOLLOWED':
                return 'report_problem'
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
}