import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Patient, PatientDTO } from '../models/patient.model';
import { ApiResponse } from '../models/api-response.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PatientService {
  private endpoint = environment.endpoints.patients;

  constructor(private api: ApiService) {}

  // Créer un nouveau patient
  createPatient(patient: PatientDTO): Observable<ApiResponse<Patient>> {
    return this.api.post<ApiResponse<Patient>>(this.endpoint, patient);
  }

  // Récupérer un patient par ID
  getPatientById(id: number): Observable<ApiResponse<Patient>> {
    return this.api.get<ApiResponse<Patient>>(`${this.endpoint}/${id}`);
  }

  // Récupérer tous les patients
  getAllPatients(): Observable<ApiResponse<Patient[]>> {
    return this.api.get<ApiResponse<Patient[]>>(this.endpoint);
  }

  // Chercher par numéro de sécurité sociale
  getPatientByNumeroSecu(numeroSecu: string): Observable<ApiResponse<Patient>> {
    return this.api.get<ApiResponse<Patient>>(`${this.endpoint}/secu/${numeroSecu}`);
  }

  // Rechercher par nom
  searchPatientsByNom(nom: string): Observable<ApiResponse<Patient[]>> {
    return this.api.get<ApiResponse<Patient[]>>(`${this.endpoint}/search?nom=${nom}`);
  }

  // Patients avec allergies
  getPatientsWithAllergies(): Observable<ApiResponse<Patient[]>> {
    return this.api.get<ApiResponse<Patient[]>>(`${this.endpoint}/with-allergies`);
  }

  // Patients avec maladies chroniques
  getPatientsWithChronicDiseases(): Observable<ApiResponse<Patient[]>> {
    return this.api.get<ApiResponse<Patient[]>>(`${this.endpoint}/with-chronic-diseases`);
  }

  // Mettre à jour un patient
  updatePatient(id: number, patient: PatientDTO): Observable<ApiResponse<Patient>> {
    return this.api.put<ApiResponse<Patient>>(`${this.endpoint}/${id}`, patient);
  }

  // Supprimer un patient
  deletePatient(id: number): Observable<ApiResponse<void>> {
    return this.api.delete<ApiResponse<void>>(`${this.endpoint}/${id}`);
  }
}
