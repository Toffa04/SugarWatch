import { Role } from "./interface/Role";

export class User{
    id!: number;
    username!: string;
    email!: string;
    role!: Role;
    verified!: boolean;
}