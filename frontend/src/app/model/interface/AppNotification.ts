export type NotificationType =
    | 'MISSED_MEDICINE'
    | 'THERAPY_NOT_FOLLOWED'
    | 'HIGH_GLYCEMIA_WARNING'
    | 'HIGH_GLYCEMIA_ALERT';

export class AppNotification {
    public id!: number;
    public message!: string;
    public seen!: boolean;
    public time!: Date;
    public type!: NotificationType;

    constructor(data?: any) {
        if(data) {
            this.id = data.id;
            this.message = data.message;
            this.seen = data.seen;
            this.time = new Date(data.time);
            this.type = data.type;
        }
    }

    get isCritical(): boolean {
        return this.type === 'HIGH_GLYCEMIA_ALERT';
    }

    get isWarning(): boolean {
        return this.type === 'HIGH_GLYCEMIA_WARNING' || this.type === 'THERAPY_NOT_FOLLOWED';
    }
}