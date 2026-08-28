export class Patient {
  id!: number;
  firstName!: string;
  lastName!: string;
  birthDate!: Date;
  isSmoker: boolean;
  isExSmoker: boolean;
  hasAlcoholDependency: boolean;
  hasObesity: boolean;
  medicalHistory: string;

  constructor(
    firstName: string,
    lastName: string,
    birthDate: Date,
    isSmoker: boolean,
    isExSmoker: boolean,
    hasAlcoholDependency: boolean,
    hasObesity: boolean,
    medicalHistory: string,
  ) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.birthDate = birthDate;
    this.isSmoker = isSmoker;
    this.isExSmoker = isExSmoker;
    this.hasAlcoholDependency = hasAlcoholDependency;
    this.hasObesity = hasObesity;
    this.medicalHistory = medicalHistory;
  }
}
