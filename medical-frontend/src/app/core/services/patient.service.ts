import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Patient, PatientDTO } from '../models/patient.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private apiUrl = `${environment.apiUrl}${environment.endpoints.patients}`;

  constructor(private http: HttpClient) {}

  /**
   * Créer un nouveau patient
   */
  createPatient(patient: PatientDTO): Observable<Patient> {
    return this.http.post<ApiResponse<Patient>>(this.apiUrl, patient)
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer un patient par ID
   */
  getPatientById(id: number): Observable<Patient> {
    return this.http.get<ApiResponse<Patient>>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer tous les patients
   */
  getAllPatients(): Observable<Patient[]> {
    return this.http.get<ApiResponse<Patient[]>>(this.apiUrl)
      .pipe(map(response => response.data));
  }

  /**
   * Rechercher un patient par numéro de sécurité sociale
   */
  getPatientByNumeroSecu(numeroSecu: string): Observable<Patient> {
    return this.http.get<ApiResponse<Patient>>(`${this.apiUrl}/secu/${numeroSecu}`)
      .pipe(map(response => response.data));
  }

  /**
   * Rechercher des patients par nom
   */
  searchPatients(nom: string): Observable<Patient[]> {
    const params = new HttpParams().set('nom', nom);
    return this.http.get<ApiResponse<Patient[]>>(`${this.apiUrl}/search`, { params })
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer les patients avec allergies
   */
  getPatientsWithAllergies(): Observable<Patient[]> {
    return this.http.get<ApiResponse<Patient[]>>(`${this.apiUrl}/with-allergies`)
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer les patients avec maladies chroniques
   */
  getPatientsWithChronicDiseases(): Observable<Patient[]> {
    return this.http.get<ApiResponse<Patient[]>>(`${this.apiUrl}/with-chronic-diseases`)
      .pipe(map(response => response.data));
  }

  /**
   * Mettre à jour un patient
   */
  updatePatient(id: number, patient: PatientDTO): Observable<Patient> {
    return this.http.put<ApiResponse<Patient>>(`${this.apiUrl}/${id}`, patient)
      .pipe(map(response => response.data));
  }

  /**
   * Supprimer un patient
   */
  deletePatient(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.data));
  }

  /**
   * Calculer l'âge à partir de la date de naissance
   */
  calculateAge(dateNaissance: Date | string): number {
    const today = new Date();
    const birthDate = new Date(dateNaissance);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();

    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }

    return age;
  }

  /**
   * Formater la date de naissance
   */
  formatDateOfBirth(date: Date | string): string {
    const d = new Date(date);
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    return `${months[d.getMonth()]} ${d.getDate().toString().padStart(2, '0')}, ${d.getFullYear()}`;
  }
}








