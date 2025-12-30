import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { DossierMedical, DossierDTO } from '../models/dossier.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class DossierService {
  private apiUrl = `${environment.apiUrl}${environment.endpoints.dossiers}`;

  constructor(private http: HttpClient) {}

  /**
   * Créer un nouveau dossier médical
   */
  createDossier(dossier: DossierDTO): Observable<DossierMedical> {
    return this.http.post<ApiResponse<DossierMedical>>(this.apiUrl, dossier)
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer un dossier par ID
   */
  getDossierById(id: number): Observable<DossierMedical> {
    return this.http.get<ApiResponse<DossierMedical>>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer tous les dossiers d'un patient
   */
  getDossiersByPatientId(patientId: number): Observable<DossierMedical[]> {
    return this.http.get<ApiResponse<DossierMedical[]>>(`${this.apiUrl}/patient/${patientId}`)
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer les derniers N dossiers d'un patient
   */
  getLastDossiersByPatient(patientId: number, limit: number = 10): Observable<DossierMedical[]> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.http.get<ApiResponse<DossierMedical[]>>(
      `${this.apiUrl}/patient/${patientId}/last`,
      { params }
    ).pipe(map(response => response.data));
  }

  /**
   * Mettre à jour un dossier
   */
  updateDossier(id: number, dossier: DossierDTO): Observable<DossierMedical> {
    return this.http.put<ApiResponse<DossierMedical>>(`${this.apiUrl}/${id}`, dossier)
      .pipe(map(response => response.data));
  }

  /**
   * Supprimer un dossier
   */
  deleteDossier(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.data));
  }

  /**
   * Formater la date de consultation
   */
  formatConsultationDate(date: Date | string): string {
    const d = new Date(date);
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    return `${months[d.getMonth()]} ${d.getDate()}, ${d.getFullYear()}`;
  }

  /**
   * Formater l'heure de consultation
   */
  formatConsultationTime(date: Date | string): string {
    const d = new Date(date);
    const hours = d.getHours().toString().padStart(2, '0');
    const minutes = d.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  /**
   * Obtenir l'icône selon le type de consultation
   */
  getConsultationIcon(type: string): string {
    const icons: { [key: string]: string } = {
      'Cardiology': 'cardiology',
      'Dermatology': 'dermatology',
      'Pediatrics': 'child_care',
      'General': 'stethoscope'
    };
    return icons[type] || 'medical_services';
  }

  /**
   * Obtenir la couleur selon le type de consultation
   */
  getConsultationColor(type: string): string {
    const colors: { [key: string]: string } = {
      'Cardiology': 'red',
      'Dermatology': 'purple',
      'Pediatrics': 'blue',
      'General': 'slate'
    };
    return colors[type] || 'slate';
  }
}
