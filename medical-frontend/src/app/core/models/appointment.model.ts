export interface Appointment {
  id?: number;
  doctorId: number;
  patientId: number;
  appointmentDateTime: string;
  reason: string;
  status?: AppointmentStatus;
  notes?: string;
}

export enum AppointmentStatus {
  SCHEDULED = 'SCHEDULED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  NO_SHOW = 'NO_SHOW'
}

export interface AppointmentRequest {
  doctorId: number;
  patientId: number;
  appointmentDateTime: string;
  reason: string;
}
