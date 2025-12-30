export interface Appointment {
  id?: number;
  patientId: number;
  doctorId: number;
  dateTime: Date | string;
  duration: number; // en minutes
  status: AppointmentStatus;
  type: AppointmentType;
  reason: string;
  notes?: string;
  location?: string;
  createdAt?: Date | string;
  updatedAt?: Date | string;

  // Relations (peuvent être chargées)
  patient?: {
    id: number;
    firstName: string;
    lastName: string;
    age: number;
    gender: string;
  };

  doctor?: {
    id: number;
    firstName: string;
    lastName: string;
    specialty: string;
    photo?: string;
  };
}

export interface AppointmentDTO {
  id?: number;
  patientId: number;
  doctorId: number;
  dateTime: string;
  duration: number;
  status: AppointmentStatus;
  type: AppointmentType;
  reason: string;
  notes?: string;
  location?: string;
}

export enum AppointmentStatus {
  SCHEDULED = 'SCHEDULED',
  CONFIRMED = 'CONFIRMED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  NO_SHOW = 'NO_SHOW',
  RESCHEDULED = 'RESCHEDULED'
}

export enum AppointmentType {
  CONSULTATION = 'CONSULTATION',
  FOLLOW_UP = 'FOLLOW_UP',
  CHECKUP = 'CHECKUP',
  EMERGENCY = 'EMERGENCY',
  SURGERY = 'SURGERY',
  VACCINATION = 'VACCINATION'
}

export interface TimeSlot {
  start: string;
  end: string;
  available: boolean;
  doctorId?: number;
}

export interface DaySchedule {
  date: Date | string;
  slots: TimeSlot[];
}

export interface AppointmentStats {
  total: number;
  scheduled: number;
  completed: number;
  cancelled: number;
  noShow: number;
}
