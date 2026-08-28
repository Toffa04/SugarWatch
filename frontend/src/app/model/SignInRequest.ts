import { Role } from "./interface/Role";

export class SignInRequest{
    email: string;
    password: string;
    username: string;
    role: Role;

    constructor(email: string, password: string, username: string, role: Role){
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
    }
}