import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { Therapy } from "../../../../model/MedicineIntake";
import { Patient } from "../../../../model/Patient";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { FormControl, ReactiveFormsModule, Validators } from "@angular/forms";
import { MatInputModule } from "@angular/material/input";
import { MatSelect, MatSelectModule } from "@angular/material/select";
import { MatOptionModule } from "@angular/material/core";
import { Component, inject } from "@angular/core";
import { MatSnackBar } from "@angular/material/snack-bar";
import { AuthService } from "../../../../service/auth.service";
import { HttpClientService } from "../../../../service/http-client.service";

export interface TerapiaMedicDialogData {
    mode: 'create' | 'edit';
    pazienti?: Patient[]; // usato in modalita' create, per scegliere il paziente
    terapia?: Therapy; // usato in modalita edit
}

@Component({
    selector: 'app-aggiorna-terapia-medic',
    imports: [
        MatDialogModule,
        MatButtonModule,
        MatFormFieldModule,
        ReactiveFormsModule,
        MatInputModule,
        MatSelectModule,
        MatOptionModule
    ],
    templateUrl: './aggiorna-terapia-medic.html',
    styles: '',
})

export class AggiornaTerapiaMedic {
    public snackbar = inject(MatSnackBar);
    readonly dialogRef = inject(MatDialogRef<AggiornaTerapiaMedic>);
    public data = inject<TerapiaMedicDialogData>(MAT_DIALOG_DATA);

    public isCreate = this.data.mode === 'create';

    public selectedPatient = new FormControl<Patient | null>(
        null,
        this.isCreate ? Validators.required : [],
    );
    public medicine = new FormControl<string | null>(
        this.data.terapia?.medicine ?? null,
        Validators.required,
    );
    public dosesPerDay = new FormControl<number | null>(
        this.data.terapia?.dosesPerDay ?? null,
        [Validators.required, Validators.min(1)],
    );
    public quantity = new FormControl<number | null>(
        this.data.terapia?.quantity ?? null,
        [Validators.required, Validators.min(0.1),]
    );
    public notes = new FormControl<string | null>(this.data.terapia?.notes ?? null);

    constructor(
        private authService: AuthService,
        private httpClient: HttpClientService,
    ) {}

    conferma() {
        if(
            this.medicine.invalid ||
            this.dosesPerDay.invalid ||
            this.quantity.invalid ||
            (this.isCreate && this.selectedPatient.invalid)
        ) {
            this.snackbar.open('Compila correttamente tutti i campi obbligatori', 'Ok');
            return;
        }

        const medicId = this.authService.getId!;

        const payload = {
            medicine: this.medicine.value,
            dosesPerDay: this.dosesPerDay.value,
            quantity: this.quantity.value,
            notes: this.notes.value,
        };

        if(this.isCreate) {
            const patientId = this.selectedPatient.value!.id;
            this.httpClient.nuovaTerapiaMedic(patientId, medicId, payload).subscribe({
                next: () => this.dialogRef.close(true),
                error: (err: unknown) => {
                    console.error('Errore creazione terapia:', err);
                    this.snackbar.open('Errore durante la creazione della terapia', 'Ok');
                },
            });
        } else {
            const terapiaId = this.data.terapia!.id;
            this.httpClient.aggiornaTerapiaMedic(terapiaId, medicId, payload).subscribe({
                next: () => this.dialogRef.close(true),
                error: (err: unknown) => {
                    console.error('Erroer modifica terapia:', err);
                    this.snackbar.open('Errore durante la modifica della terapia', 'Ok');
                },
            });
        }
    }
}