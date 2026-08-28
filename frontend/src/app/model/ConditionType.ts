export type ConditionType = 'symptom' | 'pathology' | 'concomitationTherapy';

export class Symptom{
    public id?: number;
    public description!: string;
    public startDate!: Date;
    public endDate?: Date | null;
    public notes?: string;

    constructor(data?: any){
        if(data){
            this.id = data.id;
            this.description = data.description;
            this.startDate = new Date(data.startDate);
            this.endDate = data.endDate ? new Date(data.endDate) : null;
            this.notes = data.notes;
        }
    }

    get isActive(): boolean{
        return !this.endDate;
    }
}

export class Pathology{
    public id?: number;
    public description!: string;
    public startDate!: Date;
    public endDate?: Date | null;
    public notes?: string;

    constructor(data?: any){
        if(data){
            this.id = data.id;
            this.description = data.description;
            this.startDate = new Date(data.startDate);
            this.endDate = data.endDate ? new Date(data.endDate) : null;
            this.notes = data.notes;
        }
    }

    get isActive(): boolean {
        return !this.endDate;
    }
}

export class ConcomitantTherapy{
    public id?: number;
    public medicine!: string;
    public reason?: string;
    public startDate!: Date;
    public endDate?: Date | null;
    public notes?: string;

    constructor(data?: any){
        if(data){
            this.id = data.id;
            this.medicine = data.medicine;
            this.reason = data.reason;
            this.startDate = data.startDate;
            this.endDate = data.endDate ? new Date(data.endDate) : null;
            this.notes = data.notes;
        }
    }

    get isActive(): boolean {
        return !this.endDate;
    }
}