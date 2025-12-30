import { Component, OnInit } from '@angular/core';
import { Appointment, AppointmentStatus } from '../../../core/models/appointment.model';
import { Doctor } from '../../../core/models/doctor.model';
import { AppointmentService } from '../../../core/services/appointment.service';
import { DoctorService } from '../../../core/services/doctor.service';
import { Router } from '@angular/router';

interface CalendarEvent {
  appointment: Appointment;
  top: number;
  height: number;
  color: string;
}

@Component({
  selector: 'app-appointment-calendar',
  templateUrl: './appointment-calendar.component.html',
  styleUrls: ['./appointment-calendar.component.scss']
})
export class AppointmentCalendarComponent implements OnInit {
  currentWeekStart: Date = new Date();
  weekDates: Date[] = [];
  appointments: Appointment[] = [];
  filteredAppointments: Appointment[] = [];
  doctors: Doctor[] = [];
  selectedDoctors: number[] = [];

  calendarEvents: Map<number, CalendarEvent[]> = new Map(); // dayIndex -> events

  loading = true;
  error: string | null = null;

  viewMode: 'week' | 'day' | 'month' = 'week';

  // Time slots for the calendar (8:00 - 18:00)
  timeSlots: string[] = [
    '08:00', '09:00', '10:00', '11:00', '12:00',
    '13:00', '14:00', '15:00', '16:00', '17:00'
  ];

  constructor(
    private appointmentService: AppointmentService,
    private doctorService: DoctorService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.currentWeekStart = this.appointmentService.getStartOfWeek(new Date());
    this.weekDates = this.appointmentService.getWeekDates(this.currentWeekStart);
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.error = null;

    // Load doctors
    this.doctorService.getAllDoctors().subscribe({
      next: (doctors) => {
        this.doctors = doctors;
        this.loadAppointments();
      },
      error: (err) => {
        this.error = 'Failed to load doctors';
        this.loading = false;
      }
    });
  }

  loadAppointments(): void {
    this.appointmentService.getWeekAppointments(this.currentWeekStart).subscribe({
      next: (appointments) => {
        this.appointments = appointments;
        this.applyFilters();
        this.buildCalendarEvents();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Failed to load appointments';
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    if (this.selectedDoctors.length === 0) {
      this.filteredAppointments = this.appointments;
    } else {
      this.filteredAppointments = this.appointments.filter(
        apt => this.selectedDoctors.includes(apt.doctorId)
      );
    }
  }

  buildCalendarEvents(): void {
    this.calendarEvents.clear();

    this.filteredAppointments.forEach(appointment => {
      const aptDate = new Date(appointment.dateTime);
      const dayIndex = this.weekDates.findIndex(
        date => this.isSameDay(date, aptDate)
      );

      if (dayIndex >= 0) {
        const event = this.createCalendarEvent(appointment);

        if (!this.calendarEvents.has(dayIndex)) {
          this.calendarEvents.set(dayIndex, []);
        }
        this.calendarEvents.get(dayIndex)!.push(event);
      }
    });
  }

  createCalendarEvent(appointment: Appointment): CalendarEvent {
    const aptDate = new Date(appointment.dateTime);
    const hours = aptDate.getHours();
    const minutes = aptDate.getMinutes();

    // Calculate position (80px per hour, starting at 8:00)
    const startHour = 8;
    const pixelsPerHour = 80;
    const top = ((hours - startHour) * pixelsPerHour) + ((minutes / 60) * pixelsPerHour);
    const height = (appointment.duration / 60) * pixelsPerHour;

    return {
      appointment,
      top,
      height,
      color: this.appointmentService.getStatusColor(appointment.status)
    };
  }

  isSameDay(date1: Date, date2: Date): boolean {
    return date1.getDate() === date2.getDate() &&
      date1.getMonth() === date2.getMonth() &&
      date1.getFullYear() === date2.getFullYear();
  }

  isToday(date: Date): boolean {
    return this.isSameDay(date, new Date());
  }

  getCurrentTimeIndicator(): number {
    const now = new Date();
    const hours = now.getHours();
    const minutes = now.getMinutes();
    const startHour = 8;
    const pixelsPerHour = 80;
    return ((hours - startHour) * pixelsPerHour) + ((minutes / 60) * pixelsPerHour);
  }

  getEventsForDay(dayIndex: number): CalendarEvent[] {
    return this.calendarEvents.get(dayIndex) || [];
  }

  toggleDoctorFilter(doctorId: number): void {
    const index = this.selectedDoctors.indexOf(doctorId);
    if (index >= 0) {
      this.selectedDoctors.splice(index, 1);
    } else {
      this.selectedDoctors.push(doctorId);
    }
    this.applyFilters();
    this.buildCalendarEvents();
  }

  isDoctorSelected(doctorId: number): boolean {
    return this.selectedDoctors.includes(doctorId);
  }

  previousWeek(): void {
    this.currentWeekStart = this.appointmentService.addDays(this.currentWeekStart, -7);
    this.weekDates = this.appointmentService.getWeekDates(this.currentWeekStart);
    this.loadAppointments();
  }

  nextWeek(): void {
    this.currentWeekStart = this.appointmentService.addDays(this.currentWeekStart, 7);
    this.weekDates = this.appointmentService.getWeekDates(this.currentWeekStart);
    this.loadAppointments();
  }

  formatWeekRange(): string {
    const start = this.weekDates[0];
    const end = this.weekDates[6];
    const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

    return `${monthNames[start.getMonth()]} ${start.getDate()} - ${end.getDate()}, ${end.getFullYear()}`;
  }

  formatDayOfWeek(date: Date): string {
    const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    return days[date.getDay()];
  }

  formatDayNumber(date: Date): number {
    return date.getDate();
  }

  getStatusBgClass(status: AppointmentStatus): string {
    const color = this.appointmentService.getStatusColor(status);
    return `bg-${color}-50 dark:bg-${color}-900/20 border-${color}-500`;
  }

  getStatusTextClass(status: AppointmentStatus): string {
    const color = this.appointmentService.getStatusColor(status);
    return `text-${color}-600 dark:text-${color}-300`;
  }

  formatTime(dateTime: Date | string): string {
    return this.appointmentService.formatTime(dateTime);
  }

  formatTimeRange(appointment: Appointment): string {
    const start = new Date(appointment.dateTime);
    const end = new Date(start.getTime() + appointment.duration * 60000);
    return this.appointmentService.formatTimeRange(start, end);
  }

  newAppointment(): void {
    this.router.navigate(['/appointments/new']);
  }

  viewAppointment(appointment: Appointment): void {
    this.router.navigate(['/appointments', appointment.id]);
  }

  getDoctorColor(doctorId: number): string {
    const colors = ['blue', 'purple', 'emerald', 'amber', 'pink', 'indigo'];
    return colors[doctorId % colors.length];
  }

  getDoctorName(doctorId: number): string {
    const doctor = this.doctors.find(d => d.id === doctorId);
    return doctor ? `Dr. ${doctor.lastName}` : 'Unknown';
  }
}
