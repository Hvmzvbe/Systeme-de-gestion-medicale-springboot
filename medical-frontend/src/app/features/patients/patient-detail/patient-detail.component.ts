import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Patient } from '../../../core/models/patient.model';
import { DossierMedical } from '../../../core/models/dossier.model';
import { PatientService } from '../../../core/services/patient.service';
import { DossierService } from '../../../core/services/dossier.service';

@Component({
  selector: 'app-patient-detail',
  templateUrl: './patient-detail.component.html',
  styleUrls: ['./patient-detail.component.scss']
})
export class PatientDetailComponent implements OnInit {
  patient: Patient | null = null;
  dossiers: DossierMedical[] = [];
  filteredDossiers: DossierMedical[] = [];
  loading = true;
  error: string | null = null;
  activeTab: 'history' | 'new' | 'documents' = 'history';
  filterType = 'all';
  displayedDossiers = 10;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private patientService: PatientService,
    private dossierService: DossierService
  ) {}

  ngOnInit(): void {
    const patientId = this.route.snapshot.paramMap.get('id');
    if (patientId) {
      this.loadPatientData(+patientId);
    }
  }

  loadPatientData(patientId: number): void {
    this.loading = true;
    this.error = null;

    // Load patient info
    this.patientService.getPatientById(patientId).subscribe({
      next: (patient) => {
        this.patient = patient;
        this.loadDossiers(patientId);
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load patient data';
        this.loading = false;
      }
    });
  }

  loadDossiers(patientId: number): void {
    this.dossierService.getDossiersByPatientId(patientId).subscribe({
      next: (dossiers) => {
        this.dossiers = dossiers.sort((a, b) =>
          new Date(b.dateConsultation).getTime() - new Date(a.dateConsultation).getTime()
        );
        this.applyFilter();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading dossiers:', err);
        this.loading = false;
      }
    });
  }

  applyFilter(): void {
    if (this.filterType === 'all') {
      this.filteredDossiers = this.dossiers;
    } else {
      this.filteredDossiers = this.dossiers.filter(
        d => d.typeConsultation === this.filterType
      );
    }
  }

  getInitials(patient: Patient): string {
    return `${patient.prenom?.charAt(0) || ''}${patient.nom?.charAt(0) || ''}`.toUpperCase();
  }

  calculateAge(dateNaissance: Date | string): number {
    return this.patientService.calculateAge(dateNaissance);
  }

  formatDateOfBirth(date: Date | string): string {
    return this.patientService.formatDateOfBirth(date);
  }

  formatConsultationDate(date: Date | string): string {
    return this.dossierService.formatConsultationDate(date);
  }

  formatConsultationTime(date: Date | string): string {
    return this.dossierService.formatConsultationTime(date);
  }

  getConsultationIcon(type: string): string {
    return this.dossierService.getConsultationIcon(type);
  }

  getConsultationBgClass(type: string): string {
    const classes: { [key: string]: string } = {
      'Cardiology': 'bg-red-50 dark:bg-red-900/20',
      'Dermatology': 'bg-purple-50 dark:bg-purple-900/20',
      'Pediatrics': 'bg-blue-50 dark:bg-blue-900/20',
      'General': 'bg-slate-100 dark:bg-slate-700'
    };
    return classes[type] || 'bg-slate-100 dark:bg-slate-700';
  }

  getConsultationTextClass(type: string): string {
    const classes: { [key: string]: string } = {
      'Cardiology': 'text-red-500 dark:text-red-400',
      'Dermatology': 'text-purple-500 dark:text-purple-400',
      'Pediatrics': 'text-primary dark:text-blue-400',
      'General': 'text-slate-500'
    };
    return classes[type] || 'text-slate-500';
  }

  getChronicDiseasesList(diseases?: string): string[] {
    if (!diseases) return [];
    return diseases.split(',').map(d => d.trim()).filter(d => d.length > 0);
  }

  loadMoreDossiers(): void {
    this.displayedDossiers += 10;
  }

  editPatient(): void {
    this.router.navigate(['/patients', this.patient?.id, 'edit']);
  }

  bookAppointment(): void {
    this.router.navigate(['/appointments', 'new'], {
      queryParams: { patientId: this.patient?.id }
    });
  }

  onDossierCreated(dossier: DossierMedical): void {
    this.dossiers.unshift(dossier);
    this.applyFilter();
    this.activeTab = 'history';
  }
}
