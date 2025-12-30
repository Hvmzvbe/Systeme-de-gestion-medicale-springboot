import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PatientService } from '../../../core/services/patient.service';
import { Patient } from '../../../core/models/patient.model';

@Component({
  selector: 'app-patient-list',
  templateUrl: './patient-list.component.html',
  styleUrls: ['./patient-list.component.scss']
})
export class PatientListComponent implements OnInit {
  patients: Patient[] = [];
  filteredPatients: Patient[] = [];
  loading = false;
  error: string | null = null;

  // Filtres
  searchTerm = '';
  selectedCity = 'all';
  selectedStatus = 'all';
  selectedAllergy = 'all';

  // Pagination
  currentPage = 1;
  itemsPerPage = 10;
  totalItems = 0;

  constructor(
    private patientService: PatientService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.loading = true;
    this.error = null;

    this.patientService.getAllPatients().subscribe({
      next: (response) => {
        this.patients = response.data;
        this.filteredPatients = this.patients;
        this.totalItems = this.patients.length;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Erreur lors du chargement des patients';
        console.error('Error loading patients:', error);
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredPatients = this.patients.filter(patient => {
      const matchesSearch =
        patient.nom.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        patient.prenom.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        patient.numeroSecu.includes(this.searchTerm) ||
        (patient.email && patient.email.toLowerCase().includes(this.searchTerm.toLowerCase()));

      const matchesCity = this.selectedCity === 'all' || patient.ville === this.selectedCity;

      const matchesAllergy = this.selectedAllergy === 'all' ||
        (patient.allergies && patient.allergies.toLowerCase().includes(this.selectedAllergy.toLowerCase()));

      return matchesSearch && matchesCity && matchesAllergy;
    });

    this.totalItems = this.filteredPatients.length;
  }

  onSearch(): void {
    this.currentPage = 1;
    this.applyFilters();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedCity = 'all';
    this.selectedStatus = 'all';
    this.selectedAllergy = 'all';
    this.applyFilters();
  }

  viewPatient(id: number): void {
    this.router.navigate(['/patients', id]);
  }

  editPatient(id: number): void {
    this.router.navigate(['/patients', id, 'edit']);
  }

  deletePatient(id: number): void {
    if (confirm('Êtes-vous sûr de vouloir supprimer ce patient ?')) {
      this.patientService.deletePatient(id).subscribe({
        next: () => {
          this.loadPatients();
        },
        error: (error) => {
          alert('Erreur lors de la suppression du patient');
          console.error('Error deleting patient:', error);
        }
      });
    }
  }

  newPatient(): void {
    this.router.navigate(['/patients/new']);
  }

  calculateAge(dateNaissance: string): number {
    const today = new Date();
    const birthDate = new Date(dateNaissance);
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();

    if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
      age--;
    }

    return age;
  }

  formatSSN(ssn: string): string {
    // Format: ***-**-4582
    if (ssn.length >= 15) {
      return `***-**-${ssn.slice(-4)}`;
    }
    return ssn;
  }

  get paginatedPatients(): Patient[] {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    const end = start + this.itemsPerPage;
    return this.filteredPatients.slice(start, end);
  }

  get totalPages(): number {
    return Math.ceil(this.totalItems / this.itemsPerPage);
  }

  changePage(page: number): void {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
    }
  }

  changeItemsPerPage(value: number): void {
    this.itemsPerPage = value;
    this.currentPage = 1;
  }
}
