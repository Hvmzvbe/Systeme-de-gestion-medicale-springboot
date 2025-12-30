export interface DossierMedical {
  id?: number;
  patientId: number;
  typeConsultation: string;
  dateConsultation: Date | string;
  medecin: string;
  diagnostic: string;
  traitement: string;
  observations?: string;
  resultatTest?: string;
  dateCreation?: Date | string;
  dateModification?: Date | string;
}

export interface DossierDTO {
  id?: number;
  patientId: number;
  typeConsultation: string;
  dateConsultation: string;
  medecin: string;
  diagnostic: string;
  traitement: string;
  observations?: string;
  resultatTest?: string;
  dateCreation?: string;
  dateModification?: string;
}

export enum TypeConsultation {
  CARDIOLOGY = 'Cardiology',
  DERMATOLOGY = 'Dermatology',
  PEDIATRICS = 'Pediatrics',
  GENERAL = 'General'
}
