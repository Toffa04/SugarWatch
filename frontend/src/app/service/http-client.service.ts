import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoginRequest } from '../model/LoginRequest';
import { Observable } from 'rxjs';
import { SignInRequest } from '../model/SignInRequest';
import { LoginResponse } from '../model/interface/LoginResponse';
import { MedicRequest } from '../model/MedicRequest';
import { PatientRequest } from '../model/PatienRequest';
import { Patient } from '../model/Patient';
import { User } from '../model/User';
import { Medic } from '../model/Medic';
import { GlycemiaReading } from '../model/GlycemiaReading';
import { GlycemiaReadingRequest } from '../model/GlycemiaReadingRequest';
import { AuthService } from './auth.service';
import { MedicineIntake, Therapy } from '../model/MedicineIntake';
import { ConditionType } from '../model/ConditionType';
import { TherapyRequest } from '../model/therapy';

@Injectable({
  providedIn: 'root',
})
export class HttpClientService {
  private RESTdiabetesLogger: string = 'http://localhost:8080';

  httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  };

  constructor(
    private httpClient: HttpClient,
    private authService: AuthService,
  ) {}

  /* LOGIN REQUEST */
  public login(user: LoginRequest): Observable<LoginResponse> {
    const url = `${this.RESTdiabetesLogger}/auth/login`;
    return this.httpClient.post<LoginResponse>(url, user);
  }

  /* SIGN IN REQUEST */
  public signIn(user: SignInRequest) {
    const url = `${this.RESTdiabetesLogger}/auth/register`;
    return this.httpClient.post(url, user);
  }

  /**
   *  ADMIN
   */
  getAllUser(): Observable<User[]> {
    const url = `${this.RESTdiabetesLogger}/auth/user`;
    return this.httpClient.get<User[]>(url);
  }

  verifyUser(id: number) {
    const url = `${this.RESTdiabetesLogger}/auth/verify`;
    return this.httpClient.post(url, id);
  }

  deleteUser(user: User) {
    const url = `${this.RESTdiabetesLogger}/auth/delete`;
    return this.httpClient.post(url, user);
  }

  gestionePazienti(): Observable<Patient[]> {
    const url = `${this.RESTdiabetesLogger}/patient`;
    return this.httpClient.get<Patient[]>(url);
  }

  getAllMedic(): Observable<Medic[]> {
    const url = `${this.RESTdiabetesLogger}/medic`;
    return this.httpClient.get<Medic[]>(url);
  }

  /**
   *  MEDIC
   */
  creaMedico(medic: MedicRequest) {
    const url = `${this.RESTdiabetesLogger}/medic`;
    return this.httpClient.post(url, medic);
  }

  getPazienti(): Observable<Patient[]>{
    const url = `${this.RESTdiabetesLogger}/medic/${this.authService.getId}/patients`;
    return this.httpClient.get<Patient[]>(url);
  }

  /**
   *  PATIENT
   */
  creaPaziente(patient: PatientRequest) {
    const url = `${this.RESTdiabetesLogger}/patient`;
    return this.httpClient.post(url, patient);
  }

  getRilevazioniGiornaliere(patientId: number): Observable<GlycemiaReading[]> {
    const url = `${this.RESTdiabetesLogger}/glycemia/patient/${patientId}`;
    return this.httpClient.get<GlycemiaReading[]>(url);
  }

  aggiornaRilevazione(rilevazione: GlycemiaReading){
    const url = `${this.RESTdiabetesLogger}/glycemia/patient/${this.authService.getId}/${rilevazione.id}`;
    return this.httpClient.put(url, rilevazione);
  }

  nuovaRilevazione(request: GlycemiaReadingRequest) {
    const url = `${this.RESTdiabetesLogger}/glycemia/patient/${this.authService.getId!}`;
    return this.httpClient.post(url, request);
  }

  getAssunzioneFarmaci(patientId: number): Observable<MedicineIntake[]> {
    const url = `${this.RESTdiabetesLogger}/medicine-intake/patient/${patientId}`;
    return this.httpClient.get<MedicineIntake[]>(url);
  }

  getTerapiePaziente(patientId: number){
    const url = `${this.RESTdiabetesLogger}/therapy/patient/${patientId}/active`;
    return this.httpClient.get<any[]>(url);
  }

  nuovaAssunzioneFarmaco(request: MedicineIntake){
    const url = `${this.RESTdiabetesLogger}/medicine-intake/patient/${this.authService.getId!}`;
    return this.httpClient.post(url, request);
  }

  aggiornaAssunzioneFarmaco(intake: MedicineIntake){
    const url = `${this.RESTdiabetesLogger}/medicine-intake/patient/${this.authService.getId}/${intake.id}`;
    return this.httpClient.put(url, intake);
  }

  private endpointFor(tipo: ConditionType): string {
    switch (tipo) {
      case 'symptom':
        return 'symptom';
      case 'pathology':
        return 'pathology';
      case 'concomitationTherapy':
        return 'concomitant-therapy';
    }
  }

  getCondizioni(tipo: ConditionType, patientId: number){
    const url = `${this.RESTdiabetesLogger}/${this.endpointFor(tipo)}/patient/${patientId}`;
    return this.httpClient.get<any[]>(url);
  }

  nuovaCondizione(tipo: ConditionType, patientId: number, payload: any){
    const url = `${this.RESTdiabetesLogger}/${this.endpointFor(tipo)}/patient/${patientId}`;
    return this.httpClient.post(url, payload);
  }

  aggiornaCondizione(tipo: ConditionType, patientId: number, id: number, payload: any){
    const url = `${this.RESTdiabetesLogger}/${this.endpointFor(tipo)}/patient/${patientId}/${id}`;
    return this.httpClient.put(url, payload);
  }

  chiudiCondizione(tipo: ConditionType, id: number){
    const url = `${this.RESTdiabetesLogger}/${this.endpointFor(tipo)}/${id}/close`;
    return this.httpClient.patch(url, {});
  }

  /*segnaNotificaLetta(id: number){
    const url = `${this.RESTdiabetesLogger}/notification/{id}/seen`;
    return this.httpClient.patch(url, {});
  }*/

  /*segnaTutteNotificheLette(userId: number){
    const url = `${this.RESTdiabetesLogger}/notification/user/{userId}/seen`;
    return this.httpClient.patch(url, {});
  }*/

  getNotificheNonLette(userId: number){
    const url = `${this.RESTdiabetesLogger}/notification/user/${userId}/unread`;
    return this.httpClient.get<any[]>(url);
  }

  getTerapieAttive(patientId: number){
    const url = `${this.RESTdiabetesLogger}/therapy/patient/${patientId}/active`;
    return this.httpClient.get<any[]>(url);
  }

  // tutte le terapie di un paziente (storico completo: attive, sospese, modificate)
  getTerapieByPatient(patientId: number){
    const url = `${this.RESTdiabetesLogger}/therapy/patient/${patientId}`;
    return this.httpClient.get<Therapy[]>(url);
  }
  
  // crea una nuova terapia per un paziente, prescritta dal medico loggato
  createTerapia(patientId: number, therapy: TherapyRequest): Observable<Therapy>{
    const url = `${this.RESTdiabetesLogger}/therapy/patient/${patientId}/medic/$this.authService.getId`;
    return this.httpClient.post<Therapy>(url, therapy);
  }

  // modifica una terapia esistente (nel backend marcata come MODIFIED)
  aggiornaTerapia(id: number, therapy: TherapyRequest): Observable<Therapy>{
    const url = `${this.RESTdiabetesLogger}/therapy/${id}/medic/${this.authService.getId}`;
    return this.httpClient.put<Therapy>(url, therapy);
  }

  // sospende una terapia 
  sospendiTerapia(id: number): Observable<Therapy>{
    const url = `${this.RESTdiabetesLogger}/therapy/${id}/suspend/medic/${this.authService.getId}`;
    return this.httpClient.patch<Therapy>(url, {});
  }
  
  getRilevazioni(patientId: number){
    const url = `${this.RESTdiabetesLogger}/glycemia/patient/${patientId}`;
    return this.httpClient.get<any[]>(url);
  }

  getListaPazienti(){
    const url = `${this.RESTdiabetesLogger}/patient`;
    return this.httpClient.get<any[]>(url);
  }

  getListaMedici(){
    const url = `${this.RESTdiabetesLogger}/medic`;
    return this.httpClient.get<any[]>(url);
  }

  getUtentiInAttesa(){
    const url = `${this.RESTdiabetesLogger}/auth/pending`;
    return this.httpClient.get<any[]>(url);
  }

  getNotifiche(userId: number) {
    const url = `${this.RESTdiabetesLogger}/notification/user/${userId}`;
    return this.httpClient.get<any[]>(url);
  }

  segnaComeLetta(id: number) {
    const url = `${this.RESTdiabetesLogger}/notification/${id}/seen`;
    return this.httpClient.patch(url, {});
  }

  segnaTutteComeLette(userId: number) {
    const url = `${this.RESTdiabetesLogger}/notification/user/${userId}/seen`;
    return this.httpClient.patch(url, {});
  }

  eliminaNotifica(id: number) {
    const url = `${this.RESTdiabetesLogger}/notification/${id}`;
    return this.httpClient.delete(url);
  }
}
