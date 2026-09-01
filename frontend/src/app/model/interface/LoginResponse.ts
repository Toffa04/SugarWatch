import { Role } from "./Role";

export interface LoginResponse{
    token: string;
    id: number;
    role: Role;
    firstLogin: boolean;
    username: string;
}