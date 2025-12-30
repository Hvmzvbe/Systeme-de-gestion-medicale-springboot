import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import {
  Doctor,
  DoctorDTO,
  Specialty,
  DoctorStatus,
  DoctorSearchFilters
} from '../models/doctor.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class DoctorService {
  private apiUrl = `${environment.apiUrl}${environment.endpoints.doctors}`;

  constructor(private http: HttpClient) {}

  /**
   * Créer un nouveau médecin
   */
  createDoctor(doctor: DoctorDTO): Observable<Doctor> {
    return this.http.post<ApiResponse<Doctor>>(this.apiUrl, doctor)
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer un médecin par ID
   */
  getDoctorById(id: number): Observable<Doctor> {
    return this.http.get<ApiResponse<Doctor>>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer tous les médecins
   */
  getAllDoctors(): Observable<Doctor[]> {
    return this.http.get<ApiResponse<Doctor[]>>(this.apiUrl)
      .pipe(map(response => response.data));
  }

  /**
   * Rechercher des médecins avec filtres
   */
  searchDoctors(filters: DoctorSearchFilters): Observable<Doctor[]> {
    let params = new HttpParams();

    if (filters.specialty) {
      params = params.set('specialty', filters.specialty);
    }
    if (filters.status) {
      params = params.set('status', filters.status);
    }
    if (filters.searchTerm) {
      params = params.set('search', filters.searchTerm);
    }
    if (filters.sortBy) {
      params = params.set('sortBy', filters.sortBy);
    }
    if (filters.minRating) {
      params = params.set('minRating', filters.minRating.toString());
    }

    return this.http.get<ApiResponse<Doctor[]>>(`${this.apiUrl}/search`, { params })
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer les médecins par spécialité
   */
  getDoctorsBySpecialty(specialty: Specialty): Observable<Doctor[]> {
    return this.http.get<ApiResponse<Doctor[]>>(`${this.apiUrl}/specialty/${specialty}`)
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer les médecins disponibles
   */
  getAvailableDoctors(): Observable<Doctor[]> {
    return this.http.get<ApiResponse<Doctor[]>>(`${this.apiUrl}/available`)
      .pipe(map(response => response.data));
  }

  /**
   * Mettre à jour un médecin
   */
  updateDoctor(id: number, doctor: DoctorDTO): Observable<Doctor> {
    return this.http.put<ApiResponse<Doctor>>(`${this.apiUrl}/${id}`, doctor)
      .pipe(map(response => response.data));
  }

  /**
   * Mettre à jour le statut d'un médecin
   */
  updateDoctorStatus(id: number, status: DoctorStatus): Observable<Doctor> {
    return this.http.patch<ApiResponse<Doctor>>(`${this.apiUrl}/${id}/status`, { status })
      .pipe(map(response => response.data));
  }

  /**
   * Supprimer un médecin
   */
  deleteDoctor(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.data));
  }

  /**
   * Utilitaires
   */

  getFullName(doctor: Doctor): string {
    return `Dr. ${doctor.firstName} ${doctor.lastName}`;
  }

  getSpecialtyColor(specialty: Specialty): string {
    const colors: { [key in Specialty]: string } = {
      [Specialty.CARDIOLOGY]: 'blue',
      [Specialty.NEUROLOGY]: 'purple',
      [Specialty.PEDIATRICS]: 'emerald',
      [Specialty.DERMATOLOGY]: 'amber',
      [Specialty.GYNECOLOGY]: 'pink',
      [Specialty.ORTHOPEDIC]: 'orange',
      [Specialty.GENERAL]: 'slate',
      [Specialty.PSYCHIATRY]: 'indigo',
      [Specialty.OPHTHALMOLOGY]: 'teal'
    };
    return colors[specialty] || 'slate';
  }

  getStatusBadgeClass(status: DoctorStatus): string {
    const classes: { [key in DoctorStatus]: string } = {
      AVAILABLE: 'bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400',
      BUSY: 'bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400',
      OFFLINE: 'bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400',
      ON_LEAVE: 'bg-amber-100 dark:bg-amber-900/30 text-amber-700 dark:text-amber-400'
    };
    return classes[status] || classes.OFFLINE;
  }

  getStatusLabel(status: DoctorStatus): string {
    const labels: { [key in DoctorStatus]: string } = {
      AVAILABLE: 'Available',
      BUSY: 'Busy',
      OFFLINE: 'Offline',
      ON_LEAVE: 'On Leave'
    };
    return labels[status] || 'Unknown';
  }

  getRatingStars(rating: number): { full: number; half: number; empty: number } {
    const full = Math.floor(rating);
    const half = rating % 1 >= 0.5 ? 1 : 0;
    const empty = 5 - full - half;
    return { full, half, empty };
  }

  formatPhoneNumber(phone: string): string {
    // Format: +1 (555) 123-4567
    const cleaned = phone.replace(/\D/g, '');
    const match = cleaned.match(/^(\d{1})(\d{3})(\d{3})(\d{4})$/);
    if (match) {
      return `+${match[1]} (${match[2]}) ${match[3]}-${match[4]}`;
    }
    return phone;
  }

  getTodayAvailability(doctor: Doctor): string {
    const today = new Date().toLocaleDateString('en-US', { weekday: 'lowercase' });
    const availability = doctor.availability[today as keyof typeof doctor.availability];

    if (!availability) {
      return 'Not available today';
    }

    return `${availability.start} - ${availability.end}`;
  }

  isAvailableToday(doctor: Doctor): boolean {
    const today = new Date().toLocaleDateString('en-US', { weekday: 'lowercase' });
    return !!doctor.availability[today as keyof typeof doctor.availability];
  }
}
