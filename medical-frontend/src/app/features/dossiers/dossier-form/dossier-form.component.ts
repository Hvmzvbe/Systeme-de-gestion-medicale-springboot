import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { DossierService } from '../../../core/services/dossier.service';
import { DossierDTO, DossierMedical } from '../../../core/models/dossier.model';

@Component({
  selector: 'app-dossier-form',
  templateUrl: './dossier-form.component.html',
  styleUrls: ['./dossier-form.component.scss']
})
export class DossierFormComponent implements OnInit {
  @Input() patientId!: number;
  @Input() dossier?: DossierMedical;
  @Output() dossierCreated = new EventEmitter<DossierMedical>();
  @Output() cancelled = new EventEmitter<void>();

  dossierForm!: FormGroup;
  submitting = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private dossierService: DossierService
  ) {}

  ngOnInit(): void {
    this.initializeForm();
  }

  initializeForm(): void {
    const now = new Date();
    const formattedDate = this.formatDateForInput(now);

    this.dossierForm = this.fb.group({
      typeConsultation: [this.dossier?.typeConsultation || 'Cardiology', Validators.required],
      dateConsultation: [
        this.dossier?.dateConsultation ? this.formatDateForInput(new Date(this.dossier.dateConsultation)) : formattedDate,
        Validators.required
      ],
      medecin: [this.dossier?.medecin || '', Validators.required],
      diagnostic: [this.dossier?.diagnostic || '', Validators.required],
      traitement: [this.dossier?.traitement || '', Validators.required],
      observations: [this.dossier?.observations || ''],
      resultatTest: [this.dossier?.resultatTest || '']
    });
  }

  formatDateForInput(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.dossierForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  onSubmit(): void {
    if (this.dossierForm.invalid) {
      Object.keys(this.dossierForm.controls).forEach(key => {
        this.dossierForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.submitting = true;
    this.errorMessage = null;

    const formValue = this.dossierForm.value;

    // Convert datetime-local to ISO string
    const dossierData: DossierDTO = {
      ...formValue,
      patientId: this.patientId,
      dateConsultation: new Date(formValue.dateConsultation).toISOString()
    };

    const saveObservable = this.dossier?.id
      ? this.dossierService.updateDossier(this.dossier.id, dossierData)
      : this.dossierService.createDossier(dossierData);

    saveObservable.subscribe({
      next: (savedDossier) => {
        this.submitting = false;
        this.successMessage = this.dossier?.id
          ? 'Medical record updated successfully!'
          : 'Medical record created successfully!';

        this.dossierCreated.emit(savedDossier);

        // Clear success message after 3 seconds
        setTimeout(() => {
          this.successMessage = null;
          if (!this.dossier?.id) {
            this.dossierForm.reset({
              typeConsultation: 'Cardiology',
              dateConsultation: this.formatDateForInput(new Date())
            });
          }
        }, 3000);
      },
      error: (error) => {
        this.submitting = false;
        this.errorMessage = error.error?.message || 'An error occurred while saving the record';

        // Clear error message after 5 seconds
        setTimeout(() => {
          this.errorMessage = null;
        }, 5000);
      }
    });
  }

  onCancel(): void {
    this.cancelled.emit();
    this.dossierForm.reset({
      typeConsultation: 'Cardiology',
      dateConsultation: this.formatDateForInput(new Date())
    });
  }
}








