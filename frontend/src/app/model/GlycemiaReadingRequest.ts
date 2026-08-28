export class GlycemiaReadingRequest{
    public glycemiaLevel: number;
    public dateTime: Date;
    public symptoms: string;
    public beforeMeal?: boolean;

    constructor(){
        this.glycemiaLevel = 0;
        this.symptoms = "";
        this.dateTime = new Date();
    }
}