import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import {
  Appointment,
  AppointmentDTO,
  AppointmentStatus,
  TimeSlot,
  DaySchedule,
  AppointmentStats
} from '../models/appointment.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  private apiUrl = `${environment.apiUrl}${environment.endpoints.appointments}`;

  constructor(private http: HttpClient) {}

  /**
   * Créer un nouveau rendez-vous
   */
  createAppointment(appointment: AppointmentDTO): Observable<Appointment> {
    return this.http.post<ApiResponse<Appointment>>(this.apiUrl, appointment)
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer un rendez-vous par ID
   */
  getAppointmentById(id: number): Observable<Appointment> {
    return this.http.get<ApiResponse<Appointment>>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer tous les rendez-vous
   */
  getAllAppointments(): Observable<Appointment[]> {
    return this.http.get<ApiResponse<Appointment[]>>(this.apiUrl)
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer les rendez-vous par patient
   */
  getAppointmentsByPatient(patientId: number): Observable<Appointment[]> {
    return this.http.get<ApiResponse<Appointment[]>>(`${this.apiUrl}/patient/${patientId}`)
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer les rendez-vous par médecin
   */
  getAppointmentsByDoctor(doctorId: number): Observable<Appointment[]> {
    return this.http.get<ApiResponse<Appointment[]>>(`${this.apiUrl}/doctor/${doctorId}`)
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer les rendez-vous par date
   */
  getAppointmentsByDate(startDate: string, endDate?: string): Observable<Appointment[]> {
    let params = new HttpParams().set('startDate', startDate);
    if (endDate) {
      params = params.set('endDate', endDate);
    }
    return this.http.get<ApiResponse<Appointment[]>>(`${this.apiUrl}/date`, { params })
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer les rendez-vous de la semaine
   */
  getWeekAppointments(weekStart: Date): Observable<Appointment[]> {
    const startDate = this.formatDate(weekStart);
    const endDate = this.formatDate(this.addDays(weekStart, 6));
    return this.getAppointmentsByDate(startDate, endDate);
  }

  /**
   * Récupérer les rendez-vous par statut
   */
  getAppointmentsByStatus(status: AppointmentStatus): Observable<Appointment[]> {
    return this.http.get<ApiResponse<Appointment[]>>(`${this.apiUrl}/status/${status}`)
      .pipe(map(response => response.data));
  }

  /**
   * Mettre à jour un rendez-vous
   */
  updateAppointment(id: number, appointment: AppointmentDTO): Observable<Appointment> {
    return this.http.put<ApiResponse<Appointment>>(`${this.apiUrl}/${id}`, appointment)
      .pipe(map(response => response.data));
  }

  /**
   * Annuler un rendez-vous
   */
  cancelAppointment(id: number, reason?: string): Observable<Appointment> {
    const body = { reason };
    return this.http.put<ApiResponse<Appointment>>(`${this.apiUrl}/${id}/cancel`, body)
      .pipe(map(response => response.data));
  }

  /**
   * Confirmer un rendez-vous
   */
  confirmAppointment(id: number): Observable<Appointment> {
    return this.http.put<ApiResponse<Appointment>>(`${this.apiUrl}/${id}/confirm`, {})
      .pipe(map(response => response.data));
  }

  /**
   * Marquer comme terminé
   */
  completeAppointment(id: number): Observable<Appointment> {
    return this.http.put<ApiResponse<Appointment>>(`${this.apiUrl}/${id}/complete`, {})
      .pipe(map(response => response.data));
  }

  /**
   * Supprimer un rendez-vous
   */
  deleteAppointment(id: number): Observable<void> {
    return this.http.delete<ApiResponse<void>>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.data));
  }

  /**
   * Récupérer les créneaux disponibles pour un médecin
   */
  getAvailableSlots(doctorId: number, date: Date): Observable<TimeSlot[]> {
    const dateStr = this.formatDate(date);
    return this.http.get<ApiResponse<TimeSlot[]>>(
      `${this.apiUrl}/slots/doctor/${doctorId}/date/${dateStr}`
    ).pipe(map(response => response.data));
  }

  /**
   * Récupérer les statistiques
   */
  getAppointmentStats(): Observable<AppointmentStats> {
    return this.http.get<ApiResponse<AppointmentStats>>(`${this.apiUrl}/stats`)
      .pipe(map(response => response.data));
  }

  /**
   * Utilitaires
   */

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  formatDateTime(date: Date): string {
    const dateStr = this.formatDate(date);
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${dateStr}T${hours}:${minutes}:00`;
  }

  formatTime(date: Date | string): string {
    const d = new Date(date);
    const hours = d.getHours();
    const minutes = d.getMinutes();
    const ampm = hours >= 12 ? 'PM' : 'AM';
    const displayHours = hours % 12 || 12;
    return `${displayHours}:${String(minutes).padStart(2, '0')} ${ampm}`;
  }

  formatTimeRange(start: Date | string, end: Date | string): string {
    return `${this.formatTime(start)} - ${this.formatTime(end)}`;
  }

  addDays(date: Date, days: number): Date {
    const result = new Date(date);
    result.setDate(result.getDate() + days);
    return result;
  }

  getWeekDates(startDate: Date): Date[] {
    return Array.from({ length: 7 }, (_, i) => this.addDays(startDate, i));
  }

  getStartOfWeek(date: Date): Date {
    const d = new Date(date);
    const day = d.getDay();
    const diff = d.getDate() - day;
    return new Date(d.setDate(diff));
  }

  getStatusColor(status: AppointmentStatus): string {
    const colors: { [key in AppointmentStatus]: string } = {
      SCHEDULED: 'blue',
      CONFIRMED: 'emerald',
      COMPLETED: 'emerald',
      CANCELLED: 'rose',
      NO_SHOW: 'slate',
      RESCHEDULED: 'amber'
    };
    return colors[status] || 'slate';
  }

  getStatusIcon(status: AppointmentStatus): string {
    const icons: { [key in AppointmentStatus]: string } = {
      SCHEDULED: 'schedule',
      CONFIRMED: 'check_circle',
      COMPLETED: 'task_alt',
      CANCELLED: 'cancel',
      NO_SHOW: 'event_busy',
      RESCHEDULED: 'update'
    };
    return icons[status] || 'event';
  }
}
