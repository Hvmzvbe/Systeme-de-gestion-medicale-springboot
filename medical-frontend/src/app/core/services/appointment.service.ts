import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Appointment, AppointmentRequest } from '../models/appointment.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  private endpoint = environment.endpoints.appointments;

  constructor(private api: ApiService) {}

  createAppointment(request: AppointmentRequest): Observable<Appointment> {
    return this.api.post<Appointment>(this.endpoint, request);
  }

  getAppointmentById(id: number): Observable<Appointment> {
    return this.api.get<Appointment>(`${this.endpoint}/${id}`);
  }

  getAppointmentsByPatient(patientId: number): Observable<Appointment[]> {
    return this.api.get<Appointment[]>(`${this.endpoint}/patient/${patientId}`);
  }

  getAppointmentsByDoctor(doctorId: number): Observable<Appointment[]> {
    return this.api.get<Appointment[]>(`${this.endpoint}/doctor/${doctorId}`);
  }

  getUpcomingAppointments(): Observable<Appointment[]> {
    return this.api.get<Appointment[]>(`${this.endpoint}/upcoming`);
  }

  updateAppointmentStatus(id: number, status: string): Observable<Appointment> {
    return this.api.put<Appointment>(`${this.endpoint}/${id}/status/${status}`, {});
  }

  cancelAppointment(id: number): Observable<Appointment> {
    return this.api.put<Appointment>(`${this.endpoint}/${id}/cancel`, {});
  }

  deleteAppointment(id: number): Observable<void> {
    return this.api.delete<void>(`${this.endpoint}/${id}`);
  }
}
