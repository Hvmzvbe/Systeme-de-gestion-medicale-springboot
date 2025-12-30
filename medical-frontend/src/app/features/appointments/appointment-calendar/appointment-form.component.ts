import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AppointmentService } from '../../../core/services/appointment.service';
import { DoctorService } from '../../../core/services/doctor.service';
import { PatientService } from '../../../core/services/patient.service';
import { Patient } from '../../../core/models/patient.model';
import { Doctor } from '../../../core/models/doctor.model';
import { AppointmentDTO, AppointmentStatus, AppointmentType, TimeSlot } from '../../../core/models/appointment.model';

@Component({
  selector: 'app-appointment-form',
  templateUrl: './appointment-form.component.html',
  styleUrls: ['./appointment-form.component.scss']
})
export class AppointmentFormComponent implements OnInit {
  appointmentForm!: FormGroup;
  
  patients: Patient[] = [];
  doctors: Doctor[] = [];
  selectedPatient: Patient | null = null;
  selectedDoctor: Doctor | null = null;
  
  selectedDate: Date = new Date();
  currentMonth: Date = new Date();
  availableSlots: TimeSlot[] = [];
  selectedSlot: TimeSlot | null = null;
  
  calendarDays: (Date | null)[] = [];
  
  loading = false;
  submitting = false;
  error: string | null = null;
  successMessage: string | null = null;
  
  searchPatientTerm = '';
  searchDoctorTerm = '';
  showPatientDropdown = false;
  showDoctorDropdown = false;

  AppointmentType = AppointmentType;

  constructor(
    private fb: FormBuilder,
    private appointmentService: AppointmentService,
    private doctorService: DoctorService,
    private patientService: PatientService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.initializeForm();
    this.loadDoctors();
    this.generateCalendar();
    
    // Check for pre-selected patient from query params
    const patientId = this.route.snapshot.queryParamMap.get('patientId');
    if (patientId) {
      this.loadPatient(+patientId);
    }
  }

  initializeForm(): void {
    this.appointmentForm = this.fb.group({
      patientId: ['', Validators.required],
      doctorId: ['', Validators.required],
      dateTime: ['', Validators.required],
      duration: [30, [Validators.required, Validators.min(15)]],
      type: [AppointmentType.CONSULTATION, Validators.required],
      reason: ['', Validators.required],
      notes: [''],
      location: ['']
    });
  }

  loadPatient(patientId: number): void {
    this.patientService.getPatientById(patientId).subscribe({
      next: (patient) => {
        this.selectedPatient = patient;
        this.appointmentForm.patchValue({ patientId: patient.id });
      },
      error: (err) => {
        this.error = 'Failed to load patient';
      }
    });
  }

  loadDoctors(): void {
    this.doctorService.getAllDoctors().subscribe({
      next: (doctors) => {
        this.doctors = doctors;
        if (doctors.length > 0) {
          this.selectDoctor(doctors[0]);
        }
      },
      error: (err) => {
        this.error = 'Failed to load doctors';
      }
    });
  }

  searchPatients(): void {
    if (this.searchPatientTerm.length < 2) {
      this.patients = [];
      return;
    }

    this.patientService.searchPatients(this.searchPatientTerm).subscribe({
      next: (patients) => {
        this.patients = patients;
        this.showPatientDropdown = true;
      },
      error: (err) => {
        this.error = 'Failed to search patients';
      }
    });
  }

  selectPatient(patient: Patient): void {
    this.selectedPatient = patient;
    this.appointmentForm.patchValue({ patientId: patient.id });
    this.showPatientDropdown = false;
    this.searchPatientTerm = `${patient.prenom} ${patient.nom}`;
  }

  removePatient(): void {
    this.selectedPatient = null;
    this.appointmentForm.patchValue({ patientId: '' });
    this.searchPatientTerm = '';
  }

  selectDoctor(doctor: Doctor): void {
    this.selectedDoctor = doctor;
    this.appointmentForm.patchValue({ doctorId: doctor.id });
    this.loadAvailableSlots();
  }

  generateCalendar(): void {
    const year = this.currentMonth.getFullYear();
    const month = this.currentMonth.getMonth();
    
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    
    const daysInMonth = lastDay.getDate();
    const startingDayOfWeek = firstDay.getDay();
    
    this.calendarDays = [];
    
    // Add empty cells for days before the first day of month
    for (let i = 0; i < startingDayOfWeek; i++) {
      this.calendarDays.push(null);
    }
    
    // Add all days of the month
    for (let day = 1; day <= daysInMonth; day++) {
      this.calendarDays.push(new Date(year, month, day));
    }
  }

  previousMonth(): void {
    this.currentMonth = new Date(
      this.currentMonth.getFullYear(),
      this.currentMonth.getMonth() - 1,
      1
    );
    this.generateCalendar();
  }

  nextMonth(): void {
    this.currentMonth = new Date(
      this.currentMonth.getFullYear(),
      this.currentMonth.getMonth() + 1,
      1
    );
    this.generateCalendar();
  }

  selectDate(date: Date): void {
    this.selectedDate = date;
    this.loadAvailableSlots();
  }

  isDateSelected(date: Date | null): boolean {
    if (!date || !this.selectedDate) return false;
    return date.toDateString() === this.selectedDate.toDateString();
  }

  isToday(date: Date | null): boolean {
    if (!date) return false;
    return date.toDateString() === new Date().toDateString();
  }

  isPastDate(date: Date | null): boolean {
    if (!date) return false;
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return date < today;
  }

  loadAvailableSlots(): void {
    if (!this.selectedDoctor || !this.selectedDate) {
      return;
    }

    this.loading = true;
    this.appointmentService.getAvailableSlots(this.selectedDoctor.id!, this.selectedDate)
      .subscribe({
        next: (slots) => {
          this.availableSlots = slots;
          this.loading = false;
        },
        error: (err) => {
          this.error = 'Failed to load available slots';
          this.loading = false;
        }
      });
  }

  selectSlot(slot: TimeSlot): void {
    if (!slot.available) return;
    
    this.selectedSlot = slot;
    
    // Combine date and time
    const dateTime = new Date(this.selectedDate);
    const [hours, minutes] = slot.start.split(':');
    dateTime.setHours(parseInt(hours), parseInt(minutes));
    
    this.appointmentForm.patchValue({
      dateTime: this.appointmentService.formatDateTime(dateTime)
    });
  }

  isSlotSelected(slot: TimeSlot): boolean {
    return this.selectedSlot?.start === slot.start;
  }

  formatMonthYear(): string {
    const months = ['January', 'February', 'March', 'April', 'May', 'June',
                    'July', 'August', 'September', 'October', 'November', 'December'];
    return `${months[this.currentMonth.getMonth()]} ${this.currentMonth.getFullYear()}`;
  }

  getSlotsAvailableCount(): number {
    return this.availableSlots.filter(s => s.available).length;
  }

  onSubmit(): void {
    if (this.appointmentForm.invalid) {
      Object.keys(this.appointmentForm.controls).forEach(key => {
        this.appointmentForm.get(key)?.markAsTouched();
      });
      return;
    }

    this.submitting = true;
    this.error = null;

    const formValue = this.appointmentForm.value;
    const appointmentData: AppointmentDTO = {
      ...formValue,
      status: AppointmentStatus.SCHEDULED
    };

    this.appointmentService.createAppointment(appointmentData).subscribe({
      next: (appointment) => {
        this.submitting = false;
        this.successMessage = 'Appointment scheduled successfully!';
        
        setTimeout(() => {
          this.router.navigate(['/appointments', appointment.id]);
        }, 1500);
      },
      error: (err) => {
        this.submitting = false;
        this.error = err.error?.message || 'Failed to create appointment';
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/appointments']);
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.appointmentForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }
}
