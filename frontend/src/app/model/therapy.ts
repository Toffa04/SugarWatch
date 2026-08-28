import { Medic } from "./Medic";
import { Patient } from "./Patient";

export type TherapyStatus = 'ACTIVE' | 'SUSPENDED' | 'MODIFIED';

export class Therapy {
    id!: number;
    patient?: Patient;
    medic?: Medic;
    medicine!: string;
    dosesPerDay!: number;
    quantity!: number;
    notes?: string;
    startDate?: string;
    endDate?: string;
    status!: TherapyStatus;
    lastModifiedBy?: Medic;
    lastModifiedAt?: string;
}

export interface TherapyRequest {
    medicine: string;
    dosesPerDay: number;
    quantity: number;
    notes?: string;
}
