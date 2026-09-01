import { Component, Inject, inject, signal } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LoginRequest } from '../../model/LoginRequest';
import { HttpClientService } from '../../service/http-client.service';
import { MatTabsModule } from '@angular/material/tabs';
import { MatSelectModule } from '@angular/material/select';
import { SignInRequest } from '../../model/SignInRequest';
import { HttpErrorResponse } from '@angular/common/http';
import { LoginResponse } from '../../model/interface/LoginResponse';
import { Role } from '../../model/interface/Role';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-login',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatSelectModule,
  ],
  templateUrl: './login.html',
})
export class Login {
  private snackBar = inject(MatSnackBar);

  hide = signal(true);

  public emailLogin = new FormControl();
  public passwordLogin = new FormControl();
  public email = new FormControl();
  public username = new FormControl();
  public password = new FormControl();
  public role = new FormControl();

  constructor(
    private httpClientService: HttpClientService,
    private router: Router,
    private authService: AuthService,
  ) {}

  public clickEvent(event: MouseEvent) {
    this.hide.set(!this.hide());
    event.stopPropagation();
  }

  public login() {
    if (
      !this.emailLogin.valid ||
      !this.passwordLogin.valid 
    ) {
      this.snackBar.open('Email o password non validi', 'OK');
    } else {
      const user = new LoginRequest(this.emailLogin.value, this.passwordLogin.value);
      this.httpClientService.login(user).subscribe({
        next: (res: LoginResponse) => {
          this.authService.login(res.token, res.id, this.emailLogin.value, res.username, res.role.toString());
          switch (res.role) {
            case Role.ADMIN:
              this.router.navigate(['/admin']);
              break;
            case Role.MEDIC:
              if(res.firstLogin){
                this.router.navigate(['/create-medic']);
              }else{
                this.router.navigate(['/home-medic']);
              }
              break;
            case Role.PATIENT:
              if(res.firstLogin){
                this.router.navigate(['/create-patient']);
              }else{
                this.router.navigate(['/home-patient']);
              }
              break;
          }
        },
        error: (err) => {
          if (err.status === 403){
            this.router.navigate(['/attesa-verifica']);
          }else if (err.status === 401) {
            this.snackBar.open("Email o password errati", "Ok");
          }else {
            this.snackBar.open("Errore del server, riprovare più tardi", "Ok");
          }
        },
      });
    }
  }

  public signIn() {
    if (
      !this.email.valid ||
      !this.password.valid ||
      !this.username.valid ||
      this.role.value == null
    ) {
      this.snackBar.open('Ci sono dei dati mancanti', 'OK');
    } else {
      const user = new SignInRequest(
        this.email.value,
        this.password.value,
        this.username.value,
        this.role.value,
      );
      this.httpClientService.signIn(user).subscribe({
        next: (res) => {
          this.snackBar.open('Registrazione effettata correttamente, andare al login', 'Ok');
        },
        error: (err) => {
          this.snackBar.open(this.getErrorMessage(err), 'Ok');
        },
      });
    }
  }

  private getErrorMessage(err: HttpErrorResponse): string {
    if (typeof err.error === 'string' && err.error.trim().length > 0) {
      return err.error;
    }

    switch (err.status) {
      case 400:
        return 'Dati non validi. Controlla i campi inseriti.';
      case 401:
        return 'Credenziali non valide.';
      case 403:
        return 'Non hai i permessi per questa operazione.';
      case 409:
        return 'Utente già esistente.';
      case 0:
        return 'Impossibile contattare il server. Controlla la connessione.';
      default:
        return 'Si è verificato un errore. Riprova più tardi.';
    }
  }
}
