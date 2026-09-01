import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { PatientHome } from './pages/patient-home/patient-home';
import { MedicHome } from './pages/medic-home/medic-home';
import { AdminHome } from './pages/admin-home/admin-home';
import { MainLayoutComponent } from './main-layout/main-layout.component';
import { PatientCreate } from './pages/patient-create/patient-create';
import { MedicCreate } from './pages/medic-create/medic-create';
import { AttesaVerifica } from './attesa-verifica/attesa-verifica';
import { Overview } from './pages/admin-home/nav/overview/overview';
import { GestioneUtenti } from './pages/admin-home/nav/gestione-utenti/gestione-utenti';
import { GestionePazienti } from './pages/admin-home/nav/gestione-pazienti/gestione-pazienti';
import { GestioneMedici } from './pages/admin-home/nav/gestione-medici/gestione-medici';
import { RilevazioniGiornaliere } from './pages/patient-home/nav/rilevazioni-giornaliere/rilevazioni-giornaliere';
import { AssunzioneFarmaci } from './pages/patient-home/nav/assunzione-farmaci/assunzione-farmaci';
import { CondizioniConcomitanti } from './pages/patient-home/nav/condizioni-concomitanti/condizioni-concomitanti';
import { GestionePazientiMedic } from './pages/medic-home/nav/gestione-pazienti/gestione-pazienti';
import { GestioneTerapie } from './pages/medic-home/nav/gestione-terapie/gestione-terapie';
import { Dashboard } from './pages/patient-home/nav/dashboard/dashboard';
import { RilevazioniPaziente } from './pages/medic-home/nav/rilevazioni-paziente/rilevazioni-paziente';
import { MedicDashboard } from './pages/medic-home/nav/MedicDashboard/medic-dashboard';


/*export const routes: Routes = [
  {
    path: '',
    component: Login,
    pathMatch: 'full',
  },
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: 'home-patient',
        component: PatientHome,
        children: [
          {
            path: '',
            component: Dashboard,
            pathMatch: 'full',
          },
          {
            path: 'rilevazioni-giornaliere',
            component: RilevazioniGiornaliere,
            pathMatch: 'full',
          },
          {
            path: 'assunzioni-farmaci',
            component: AssunzioneFarmaci,
            pathMatch: 'full',
          },
          {
            path: 'condizioni-concomitanti',
            component: CondizioniConcomitanti,
            pathMatch: 'full',
          },
        ],
      },
      {
        path: 'home-medic',
        component: MedicHome,
        pathMatch: 'full',
      },
      {
        path: 'admin',
        component: AdminHome,
        children: [
          {
            path: '',
            component: Overview,
            pathMatch: 'full',
          },
          {
            path: 'gestione-utenti',
            component: GestioneUtenti,
            pathMatch: 'full',
          },
          {
            path: 'gestione-pazienti',
            component: GestionePazienti,
            pathMatch: 'full',
          },
          {
            path: 'gestione-medici',
            component: GestioneMedici,
            pathMatch: 'full',
          },
        ],
      },
      {
        path: 'create-patient',
        component: PatientCreate,
        pathMatch: 'full',
      },
      {
        path: 'create-medic',
        component: MedicCreate,
        pathMatch: 'full',
      },
      {
        path: 'attesa-verifica',
        component: AttesaVerifica,
        pathMatch: 'full',
      },
      {
        path: 'admin/gestione-utenti',
        component: GestioneUtenti,
        pathMatch: 'full',
      },
      {
        path: 'admin/gestione-pazienti',
        component: GestionePazienti,
        pathMatch: 'full',
      },
      {
        path: 'admin/gestione-medici',
        component: GestioneMedici,
        pathMatch: 'full',
      },
      {
        path: 'patient/rilevazioni-giornaliere',
        component: RilevazioniGiornaliere,
        pathMatch: 'full',
      },
      {
        path: 'patient/assunzione-farmaci',
        component: AssunzioneFarmaci,
        pathMatch: 'full',
      },
      {
        path: 'patient/condizioni-concomitanti',
        component: CondizioniConcomitanti,
        pathMatch: 'full',
      },
      {
        path: 'medic/gestione-pazienti',
        component: GestionePazientiMedic,
        pathMatch: 'full',
      },
      {
        path: 'medic/gestione-terapie',
        component: GestioneTerapie,
        pathMatch: 'full',
      },
      {
        path: 'home-medic',
        component: MedicHome,
        children: [
          {
            path: '',
            component: Dashboard,
            pathMatch: 'full',
          },
          {
            path: 'gestione-pazienti',
            component: GestionePazienti,
            pathMatch: 'full',
          },
          {
            path: 'gestione-terapie',
            component: GestioneTerapie,
            pathMatch: 'full',
          },
          {
            path: 'rilevazioni-paziente',
            component: RilevazioniPaziente,
            pathMatch: 'full',
          },
        ],
      },
    ],
  },
];*/
export const routes: Routes = [
  {
    path: '',
    component: Login,
    pathMatch: 'full',
  },
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: 'home-patient',
        component: PatientHome,
        children: [
          { path: '', component: Dashboard, pathMatch: 'full' },
          { path: 'rilevazioni-giornaliere', component: RilevazioniGiornaliere, pathMatch: 'full' },
          { path: 'assunzioni-farmaci', component: AssunzioneFarmaci, pathMatch: 'full' },
          { path: 'condizioni-concomitanti', component: CondizioniConcomitanti, pathMatch: 'full' },
        ],
      },
      {
        path: 'home-medic',
        component: MedicHome,
        children: [
          { path: '', component: MedicDashboard, pathMatch: 'full' },
          { path: 'gestione-pazienti', component: GestionePazientiMedic, pathMatch: 'full' },  // <-- CORRETTO
          { path: 'gestione-terapie', component: GestioneTerapie, pathMatch: 'full' },
          // rimossa { path: 'rilevazioni-paziente', component: RilevazioniPaziente, pathMatch: 'full' },
        ],
      },
      {
        path: 'admin',
        component: AdminHome,
        children: [
          { path: '', component: Overview, pathMatch: 'full' },
          { path: 'gestione-utenti', component: GestioneUtenti, pathMatch: 'full' },
          { path: 'gestione-pazienti', component: GestionePazienti, pathMatch: 'full' },
          { path: 'gestione-medici', component: GestioneMedici, pathMatch: 'full' },
        ],
      },
      {
        path: 'create-patient',
        component: PatientCreate,
        pathMatch: 'full',
      },
      {
        path: 'create-medic',
        component: MedicCreate,
        pathMatch: 'full',
      },
      {
        path: 'attesa-verifica',
        component: AttesaVerifica,
        pathMatch: 'full',
      },
      {
        path: 'admin/gestione-utenti',
        component: GestioneUtenti,
        pathMatch: 'full',
      },
      {
        path: 'admin/gestione-pazienti',
        component: GestionePazienti,
        pathMatch: 'full',
      },
      {
        path: 'admin/gestione-medici',
        component: GestioneMedici,
        pathMatch: 'full',
      },
    ],
  },
];
