import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PatientService } from '../../../core/services/patient.service';
import { Patient } from '../../../core/models/patient.model';

@Component({
  selector: 'app-patient-form',
  templateUrl: './patient-form.component.html',
  styleUrls: ['./patient-form.component.scss']
})
export class PatientFormComponent implements OnInit {
  patientForm!: FormGroup;
  isEditMode = false;
  patientId: number | null = null;
  loading = false;
  submitting = false;
  allergiesTags: string[] = [];
  allergyInput = '';

  constructor(
    private fb: FormBuilder,
    private patientService: PatientService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.initForm();

    // Vérifier si on est en mode édition
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.patientId = +params['id'];
        this.loadPatient(this.patientId);
      }
    });
  }

  initForm(): void {
    this.patientForm = this.fb.group({
      // Identity
      nom: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      prenom: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      numeroSecu: ['', [Validators.required, Validators.pattern(/^\d{15}$/)]],
      dateNaissance: ['', [Validators.required]],
      sexe: ['M', [Validators.required, Validators.pattern(/^[MF]$/)]],

      // Contact
      adresse: ['', [Validators.maxLength(100)]],
      codePostal: ['', [Validators.pattern(/^\d{5}$/)]],
      ville: ['', [Validators.maxLength(50)]],
      telephone: ['', [Validators.pattern(/^[0-9\s\-\+\(\)]*$/)]],
      email: ['', [Validators.email, Validators.maxLength(100)]],

      // Medical
      allergies: [''],
      maladiesChroniques: ['']
    });
  }

  loadPatient(id: number): void {
    this.loading = true;
    this.patientService.getPatientById(id).subscribe({
      next: (response) => {
        const patient = response.data;
        this.patientForm.patchValue(patient);

        // Charger les allergies en tags
        if (patient.allergies) {
          this.allergiesTags = patient.allergies.split(',').map(a => a.trim());
        }

        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading patient:', error);
        alert('Erreur lors du chargement du patient');
        this.loading = false;
        this.router.navigate(['/patients']);
      }
    });
  }

  addAllergyTag(event: KeyboardEvent): void {
    if (event.key === 'Enter' && this.allergyInput.trim()) {
      event.preventDefault();
      const allergy = this.allergyInput.trim();

      if (!this.allergiesTags.includes(allergy)) {
        this.allergiesTags.push(allergy);
        this.updateAllergiesField();
      }

      this.allergyInput = '';
    }
  }

  removeAllergyTag(index: number): void {
    this.allergiesTags.splice(index, 1);
    this.updateAllergiesField();
  }

  updateAllergiesField(): void {
    this.patientForm.patchValue({
      allergies: this.allergiesTags.join(', ')
    });
  }

  onSubmit(): void {
    if (this.patientForm.valid) {
      this.submitting = true;
      const patientData = this.patientForm.value;

      if (this.isEditMode && this.patientId) {
        // Update
        this.patientService.updatePatient(this.patientId, patientData).subscribe({
          next: () => {
            alert('Patient mis à jour avec succès');
            this.router.navigate(['/patients']);
          },
          error: (error) => {
            console.error('Error updating patient:', error);
            alert('Erreur lors de la mise à jour du patient');
            this.submitting = false;
          }
        });
      } else {
        // Create
        this.patientService.createPatient(patientData).subscribe({
          next: () => {
            alert('Patient créé avec succès');
            this.router.navigate(['/patients']);
          },
          error: (error) => {
            console.error('Error creating patient:', error);
            alert('Erreur lors de la création du patient');
            this.submitting = false;
          }
        });
      }
    } else {
      // Mark all fields as touched to show validation errors
      Object.keys(this.patientForm.controls).forEach(key => {
        this.patientForm.get(key)?.markAsTouched();
      });
      alert('Veuillez corriger les erreurs du formulaire');
    }
  }

  cancel(): void {
    if (confirm('Êtes-vous sûr de vouloir annuler ? Les modifications seront perdues.')) {
      this.router.navigate(['/patients']);
    }
  }

  // Getters for validation
  get nom() { return this.patientForm.get('nom'); }
  get prenom() { return this.patientForm.get('prenom'); }
  get numeroSecu() { return this.patientForm.get('numeroSecu'); }
  get dateNaissance() { return this.patientForm.get('dateNaissance'); }
  get codePostal() { return this.patientForm.get('codePostal'); }
  get email() { return this.patientForm.get('email'); }
}
