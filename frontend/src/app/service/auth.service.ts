import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Role } from '../model/interface/Role';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private token = new BehaviorSubject<string | null>(localStorage.getItem('token'));
  token$ = this.token.asObservable();

  private id = new BehaviorSubject<number | null>(
    localStorage.getItem('id') ? Number(localStorage.getItem('id')) : null,
  );
  id$ = this.id.asObservable();

  private email = new BehaviorSubject<string | null>(localStorage.getItem('email'));
  email$ = this.email.asObservable();

  private username = new BehaviorSubject<string | null>(localStorage.getItem('username'));
  username$ = this.username.asObservable();

  private role = new BehaviorSubject<string | null>(localStorage.getItem('ruolo'));
  ruolo$ = this.role.asObservable();

  get getId(): number | null {
    return this.id.value;
  }

  get getEmail(): string | null {
    return this.email.value;
  }

  get getUsername(): string | null {
    return this.username.value;
  }

  get getRole(): string | null {
    return this.role.value;
  }

  get getToken(): string | null {
    return localStorage.getItem('token');
  }

  get isLogged(): boolean {
    return !!this.token.value;
  }

  login(token: string, id: number, email: string, /*username: string,*/ ruolo: string) {
    localStorage.setItem('token', token);
    localStorage.setItem('id', id.toString());
    localStorage.setItem('email', email);
    /*localStorage.setItem('username', username);*/
    localStorage.setItem('ruolo', ruolo);
    this.token.next(token);
    this.id.next(id);
    this.email.next(email);
    /*this.username.next(username);*/
    this.role.next(ruolo);
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('id');
    localStorage.removeItem('email');
    localStorage.removeItem('username');
    localStorage.removeItem('ruolo');
    this.token.next(null); 
    this.id.next(null);
    this.email.next(null);
    this.username.next(null);
    this.role.next(null);
  }
}
