export interface Patient {
  id?: number;
  nom: string;
  prenom: string;
  dateNaissance: Date | string;
  sexe: 'M' | 'F';
  adresse: string;
  telephone: string;
  email?: string;
  numeroSecu: string;
  groupeSanguin?: string;
  allergies?: string;
  maladiesChroniques?: string;
  contactUrgence?: string;
  telephoneUrgence?: string;
  dateCreation?: Date | string;
  dateModification?: Date | string;
}

export interface PatientDTO {
  id?: number;
  nom: string;
  prenom: string;
  dateNaissance: string;
  sexe: string;
  adresse: string;
  telephone: string;
  email?: string;
  numeroSecu: string;
  groupeSanguin?: string;
  allergies?: string;
  maladiesChroniques?: string;
  contactUrgence?: string;
  telephoneUrgence?: string;
  dateCreation?: string;
  dateModification?: string;
}
