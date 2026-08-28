export class MedicRequest{
    userId: Number;
    firstName: String;
    lastName: String;

    constructor(id: Number, firstName: string, lastName: string){
            this.userId = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }
}
