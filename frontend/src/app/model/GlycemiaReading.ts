export class GlycemiaReading{
    public id!: number;
    public glycemiaLevel!: number;
    public dateTime!: Date;
    public symptoms!: string | null;
    public beforeMeal!: boolean;
    public valueHigh!: boolean;
}