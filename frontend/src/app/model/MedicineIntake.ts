export class MedicineIntake{
    public id!: number;
    public therapy!: Therapy;
    public quantity!: number;
    public matchesTherapy!: boolean;
    public dateTime!: Date;

    constructor(data?: any){
        if(data) {
            this.id = data.id;
            this.therapy = new Therapy(data.therapy);
            this.quantity = data.quantity;
            this.matchesTherapy = data.matchesTherapy;
            this.dateTime = new Date(data.dateTime);
        }
    }
}

export class Therapy{
    public id!: number;
    public medicine!: string;
    public dosesPerDay!: number;
    public quantity!: number;
    public notes!: string;
  patient: any;

    constructor(data: any){
        this.id = data.id;
        this.medicine = data.medicine;
        this.dosesPerDay = data.dosesPerDay;
        this.quantity = data.quantity;
        this.notes = data.notes;
    }
}