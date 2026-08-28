export class PatientRequest{
    userId: number;
    firstName: string;
    lastName: string;
    birthDate: Date;
    isSmoker: boolean;
    isExSmoker: boolean;
    hasAlcoholDependency: boolean;
    hasObesity: boolean;
    medicalHistory: string;


    constructor(userId: number, firstName: string, lastName: string, birthDate: Date, isSmoker: boolean, isExSmoker: boolean, hasAlcoholDependency: boolean, hasObesity: boolean, medicalHistory: string){
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName
        this.birthDate = birthDate;
        this.isSmoker = isSmoker;
        this.isExSmoker = isExSmoker;
        this.hasAlcoholDependency = hasAlcoholDependency;
        this.hasObesity = hasObesity;
        this.medicalHistory = medicalHistory;
    }
}