export interface Patient {
  id?: number;
  nom: string;
  prenom: string;
  numeroSecu: string;
  dateNaissance: string;
  sexe: 'M' | 'F';
  adresse?: string;
  codePostal?: string;
  ville?: string;
  telephone?: string;
  email?: string;
  allergies?: string;
  maladiesChroniques?: string;
  dateCreation?: string;
  dateModification?: string;
}

export interface PatientDTO {
  id?: number;
  nom: string;
  prenom: string;
  numeroSecu: string;
  dateNaissance: string;
  sexe: string;
  adresse?: string;
  codePostal?: string;
  ville?: string;
  telephone?: string;
  email?: string;
  allergies?: string;
  maladiesChroniques?: string;
}
