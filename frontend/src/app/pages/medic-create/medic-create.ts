import { Component, inject } from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../service/auth.service';
import { HttpClientService } from '../../service/http-client.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { isEmpty } from 'rxjs';
import { MedicRequest } from '../../model/MedicRequest';

@Component({
  selector: 'app-medic-create',
  imports: [MatFormFieldModule, MatInputModule, MatButtonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './medic-create.html',
  styles: ``,
})
export class MedicCreate {

  private snackBar = inject(MatSnackBar);

  public firstName = new FormControl();
  public lastName = new FormControl();

  constructor(
    private httpClientService: HttpClientService,
    private authService: AuthService,
  ) {}

  finish(){
    if(this.firstName.value === null || this.firstName.value === '' || 
       this.lastName.value === null ||  this.lastName.value === '' ){
      this.snackBar.open("Campi non validi.", "Ok");
    }else{
      const medic = new MedicRequest(this.authService.getId!, this.firstName.value, this.lastName.value);
      this.httpClientService.creaMedico(medic).subscribe({
        next: () => {
          this.snackBar.open("Creazione eseguita correttamente, aspettare l'approvazione di un admin.", "Ok");
        },
        error: (err) => {
          if (err.status === 201) {
            this.snackBar.open("Creato correttamente", "Ok");
          } else {
            this.snackBar.open("Errore del server, riprovare più tardi", "Ok");
          }
        },
      });
    }
  }

}
