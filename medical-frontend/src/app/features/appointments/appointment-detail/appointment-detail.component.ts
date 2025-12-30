import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Appointment, AppointmentStatus } from '../../../core/models/appointment.model';
import { AppointmentService } from '../../../core/services/appointment.service';

@Component({
  selector: 'app-appointment-detail',
  templateUrl: './appointment-detail.component.html',
  styleUrls: ['./appointment-detail.component.scss']
})
export class AppointmentDetailComponent implements OnInit {
  appointment: Appointment | null = null;
  loading = true;
  error: string | null = null;
  successMessage: string | null = null;
  
  AppointmentStatus = AppointmentStatus;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private appointmentService: AppointmentService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadAppointment(+id);
    }
  }

  loadAppointment(id: number): void {
    this.loading = true;
    this.error = null;
    
    this.appointmentService.getAppointmentById(id).subscribe({
      next: (appointment) => {
        this.appointment = appointment;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load appointment details';
        this.loading = false;
      }
    });
  }

  confirmAppointment(): void {
    if (!this.appointment?.id) return;
    
    this.appointmentService.confirmAppointment(this.appointment.id).subscribe({
      next: (appointment) => {
        this.appointment = appointment;
        this.successMessage = 'Appointment confirmed successfully';
        this.hideMessageAfterDelay();
      },
      error: (err) => {
        this.error = 'Failed to confirm appointment';
        this.hideMessageAfterDelay();
      }
    });
  }

  completeAppointment(): void {
    if (!this.appointment?.id) return;
    
    this.appointmentService.completeAppointment(this.appointment.id).subscribe({
      next: (appointment) => {
        this.appointment = appointment;
        this.successMessage = 'Appointment marked as completed';
        this.hideMessageAfterDelay();
      },
      error: (err) => {
        this.error = 'Failed to complete appointment';
        this.hideMessageAfterDelay();
      }
    });
  }

  cancelAppointment(): void {
    if (!this.appointment?.id) return;
    
    const confirmed = confirm('Are you sure you want to cancel this appointment?');
    if (!confirmed) return;
    
    this.appointmentService.cancelAppointment(this.appointment.id).subscribe({
      next: (appointment) => {
        this.appointment = appointment;
        this.successMessage = 'Appointment cancelled successfully';
        this.hideMessageAfterDelay();
      },
      error: (err) => {
        this.error = 'Failed to cancel appointment';
        this.hideMessageAfterDelay();
      }
    });
  }

  rescheduleAppointment(): void {
    if (!this.appointment?.id) return;
    this.router.navigate(['/appointments/new'], {
      queryParams: { 
        rescheduleId: this.appointment.id,
        patientId: this.appointment.patientId 
      }
    });
  }

  backToList(): void {
    this.router.navigate(['/appointments']);
  }

  viewPatient(): void {
    if (this.appointment?.patientId) {
      this.router.navigate(['/patients', this.appointment.patientId]);
    }
  }

  getStatusBadgeClass(): string {
    if (!this.appointment) return '';
    
    const classes: { [key in AppointmentStatus]: string } = {
      SCHEDULED: 'bg-blue-50 dark:bg-blue-900/30 text-blue-700 dark:text-blue-300 border-blue-100 dark:border-blue-800',
      CONFIRMED: 'bg-emerald-50 dark:bg-emerald-900/30 text-emerald-700 dark:text-emerald-300 border-emerald-100 dark:border-emerald-800',
      COMPLETED: 'bg-green-50 dark:bg-green-900/30 text-green-700 dark:text-green-300 border-green-100 dark:border-green-800',
      CANCELLED: 'bg-red-50 dark:bg-red-900/30 text-red-700 dark:text-red-300 border-red-100 dark:border-red-800',
      NO_SHOW: 'bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300 border-slate-200 dark:border-slate-700',
      RESCHEDULED: 'bg-amber-50 dark:bg-amber-900/30 text-amber-700 dark:text-amber-300 border-amber-100 dark:border-amber-800'
    };
    
    return classes[this.appointment.status] || classes.SCHEDULED;
  }

  getStatusIcon(): string {
    if (!this.appointment) return 'event';
    return this.appointmentService.getStatusIcon(this.appointment.status);
  }

  formatDate(date: Date | string): string {
    const d = new Date(date);
    const options: Intl.DateTimeFormatOptions = { 
      weekday: 'long', 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    };
    return d.toLocaleDateString('en-US', options);
  }

  formatTime(date: Date | string): string {
    return this.appointmentService.formatTime(date);
  }

  formatTimeRange(): string {
    if (!this.appointment) return '';
    
    const start = new Date(this.appointment.dateTime);
    const end = new Date(start.getTime() + this.appointment.duration * 60000);
    
    return this.appointmentService.formatTimeRange(start, end);
  }

  getMonthShort(date: Date | string): string {
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    const d = new Date(date);
    return months[d.getMonth()];
  }

  getDayNumber(date: Date | string): number {
    return new Date(date).getDate();
  }

  canConfirm(): boolean {
    return this.appointment?.status === AppointmentStatus.SCHEDULED;
  }

  canComplete(): boolean {
    return this.appointment?.status === AppointmentStatus.CONFIRMED || 
           this.appointment?.status === AppointmentStatus.SCHEDULED;
  }

  canCancel(): boolean {
    return this.appointment?.status !== AppointmentStatus.CANCELLED &&
           this.appointment?.status !== AppointmentStatus.COMPLETED;
  }

  canReschedule(): boolean {
    return this.appointment?.status !== AppointmentStatus.COMPLETED &&
           this.appointment?.status !== AppointmentStatus.CANCELLED;
  }

  private hideMessageAfterDelay(): void {
    setTimeout(() => {
      this.successMessage = null;
      this.error = null;
    }, 3000);
  }
}
