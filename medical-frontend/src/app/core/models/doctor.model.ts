export interface Doctor {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  specialty: Specialty;
  photo?: string;
  rating?: number;
  reviewCount?: number;
  availability: DoctorAvailability;
  status: DoctorStatus;
  licenseNumber?: string;
  experience?: number; // années
  bio?: string;
  education?: string[];
  languages?: string[];
  createdAt?: Date | string;
  updatedAt?: Date | string;
}

export interface DoctorDTO {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  specialty: Specialty;
  photo?: string;
  rating?: number;
  reviewCount?: number;
  availability: DoctorAvailability;
  status: DoctorStatus;
  licenseNumber?: string;
  experience?: number;
  bio?: string;
}

export enum Specialty {
  CARDIOLOGY = 'Cardiology',
  NEUROLOGY = 'Neurology',
  PEDIATRICS = 'Pediatrics',
  DERMATOLOGY = 'Dermatology',
  GYNECOLOGY = 'Gynecology',
  ORTHOPEDIC = 'Orthopedic',
  GENERAL = 'General Practice',
  PSYCHIATRY = 'Psychiatry',
  OPHTHALMOLOGY = 'Ophthalmology'
}

export enum DoctorStatus {
  AVAILABLE = 'AVAILABLE',
  BUSY = 'BUSY',
  OFFLINE = 'OFFLINE',
  ON_LEAVE = 'ON_LEAVE'
}

export interface DoctorAvailability {
  monday?: TimeRange;
  tuesday?: TimeRange;
  wednesday?: TimeRange;
  thursday?: TimeRange;
  friday?: TimeRange;
  saturday?: TimeRange;
  sunday?: TimeRange;
}

export interface TimeRange {
  start: string; // HH:mm format
  end: string;   // HH:mm format
}

export interface DoctorSearchFilters {
  specialty?: Specialty;
  status?: DoctorStatus;
  searchTerm?: string;
  sortBy?: 'rating' | 'availability' | 'experience';
  minRating?: number;
}
