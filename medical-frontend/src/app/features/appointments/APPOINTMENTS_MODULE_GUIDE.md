# 🗓️ Module Appointments - Angular Components

## 📋 Vue d'ensemble

Ce module gère l'ensemble des fonctionnalités liées aux rendez-vous médicaux :
- **Calendar View** : Vue calendrier hebdomadaire avec timeline
- **New Appointment** : Formulaire de création de rendez-vous
- **Appointment Detail** : Détails et actions sur un rendez-vous
- **Doctor List** : Liste des médecins avec filtres et recherche

## 🏗️ Structure des fichiers créés

```
src/app/
├── core/
│   ├── models/
│   │   ├── appointment.model.ts ✅ CRÉÉ
│   │   └── doctor.model.ts ✅ CRÉÉ
│   └── services/
│       ├── appointment.service.ts ✅ CRÉÉ
│       └── doctor.service.ts ✅ CRÉÉ
│
└── features/
    └── appointments/
        ├── appointment-calendar/
        │   ├── appointment-calendar.component.ts ✅ CRÉÉ
        │   ├── appointment-calendar.component.html
        │   └── appointment-calendar.component.scss
        ├── appointment-form/
        │   ├── appointment-form.component.ts
        │   ├── appointment-form.component.html
        │   └── appointment-form.component.scss
        ├── appointment-detail/
        │   ├── appointment-detail.component.ts
        │   ├── appointment-detail.component.html
        │   └── appointment-detail.component.scss
        ├── doctor-list/
        │   ├── doctor-list.component.ts
        │   ├── doctor-list.component.html
        │   └── doctor-list.component.scss
        ├── appointments.module.ts
        └── appointments-routing.module.ts
```

## 📊 Modèles de données

### Appointment Model

```typescript
interface Appointment {
  id?: number;
  patientId: number;
  doctorId: number;
  dateTime: Date | string;
  duration: number; // minutes
  status: AppointmentStatus;
  type: AppointmentType;
  reason: string;
  notes?: string;
  location?: string;
}

enum AppointmentStatus {
  SCHEDULED = 'SCHEDULED',
  CONFIRMED = 'CONFIRMED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  NO_SHOW = 'NO_SHOW'
}
```

### Doctor Model

```typescript
interface Doctor {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  specialty: Specialty;
  photo?: string;
  rating?: number;
  reviewCount?: number;
  availability: DoctorAvailability;
  status: DoctorStatus;
}

enum Specialty {
  CARDIOLOGY = 'Cardiology',
  NEUROLOGY = 'Neurology',
  PEDIATRICS = 'Pediatrics',
  DERMATOLOGY = 'Dermatology',
  // ... autres spécialités
}
```

## 🔌 Endpoints Backend requis

### Appointments

```
GET    /api/appointments                    // Liste tous
GET    /api/appointments/{id}              // Détails
GET    /api/appointments/patient/{id}      // Par patient
GET    /api/appointments/doctor/{id}       // Par médecin
GET    /api/appointments/date?start&end    // Par période
GET    /api/appointments/status/{status}   // Par statut
GET    /api/appointments/slots/doctor/{id}/date/{date}  // Créneaux dispo
POST   /api/appointments                   // Créer
PUT    /api/appointments/{id}              // Modifier
PUT    /api/appointments/{id}/cancel       // Annuler
PUT    /api/appointments/{id}/confirm      // Confirmer
PUT    /api/appointments/{id}/complete     // Terminer
DELETE /api/appointments/{id}              // Supprimer
GET    /api/appointments/stats             // Statistiques
```

### Doctors

```
GET    /api/doctors                        // Liste tous
GET    /api/doctors/{id}                  // Détails
GET    /api/doctors/search?params         // Recherche avec filtres
GET    /api/doctors/specialty/{specialty} // Par spécialité
GET    /api/doctors/available             // Disponibles
POST   /api/doctors                       // Créer
PUT    /api/doctors/{id}                  // Modifier
PATCH  /api/doctors/{id}/status           // Changer statut
DELETE /api/doctors/{id}                  // Supprimer
```

## 🎨 Composants

### 1. Calendar View Component

**Fonctionnalités:**
- Vue hebdomadaire avec timeline (8h-18h)
- Affichage des rendez-vous positionnés selon l'heure
- Filtrage par médecin
- Code couleur par statut
- Navigation semaine précédente/suivante
- Indicateur de temps actuel
- Clic sur un événement pour voir les détails

**Props/Inputs:**
- `weekStart`: Date de début de semaine
- `selectedDoctors`: Filtres actifs

**Events/Outputs:**
- `appointmentClick`: Émis lors du clic sur un rendez-vous
- `newAppointment`: Émis pour créer un nouveau RDV

**Calculs importants:**
```typescript
// Position d'un événement
const pixelsPerHour = 80;
const startHour = 8;
const top = ((hours - startHour) * pixelsPerHour) + ((minutes / 60) * pixelsPerHour);
const height = (duration / 60) * pixelsPerHour;
```

### 2. Appointment Form Component

**Fonctionnalités:**
- Recherche et sélection patient
- Sélection médecin avec dropdown
- Calendrier de sélection de date
- Grille de créneaux horaires disponibles
- Validation des champs
- Gestion des états (available/booked slots)

**Formulaire:**
```typescript
appointmentForm = this.fb.group({
  patientId: ['', Validators.required],
  doctorId: ['', Validators.required],
  dateTime: ['', Validators.required],
  duration: [30, Validators.required],
  type: ['CONSULTATION', Validators.required],
  reason: ['', Validators.required],
  notes: ['']
});
```

### 3. Appointment Detail Component

**Fonctionnalités:**
- Affichage complet des informations
- Badge de statut
- Informations patient et médecin
- Actions : Cancel, Reschedule, Complete
- Historique précédent
- Résultats de labo (si applicable)

**Actions:**
```typescript
cancelAppointment(id: number): void
rescheduleAppointment(id: number): void
completeAppointment(id: number): void
```

### 4. Doctor List Component

**Fonctionnalités:**
- Grille responsive de cartes médecins
- Filtres : spécialité, disponibilité, rating
- Recherche par nom
- Tri : rating, disponibilité, expérience
- Badge de statut (Available, Busy, Offline)
- Affichage rating étoiles
- Bouton "Book Appointment"
- Pagination

**Filtres:**
```typescript
interface DoctorSearchFilters {
  specialty?: Specialty;
  status?: DoctorStatus;
  searchTerm?: string;
  sortBy?: 'rating' | 'availability' | 'experience';
  minRating?: number;
}
```

## 🎯 Fonctionnalités clés

### Calendar - Gestion des événements

```typescript
// Création d'événements pour le calendrier
interface CalendarEvent {
  appointment: Appointment;
  top: number;      // Position Y en pixels
  height: number;   // Hauteur en pixels
  color: string;    // Couleur selon le statut
}

// Organisation par jour
calendarEvents: Map<number, CalendarEvent[]> = new Map();
```

### Time Slots - Créneaux disponibles

```typescript
interface TimeSlot {
  start: string;    // "09:00"
  end: string;      // "09:30"
  available: boolean;
  doctorId?: number;
}

// Récupération des créneaux
getAvailableSlots(doctorId: number, date: Date): Observable<TimeSlot[]>
```

### Status Management - Gestion des statuts

```typescript
// Couleurs par statut
SCHEDULED  → blue    (Programmé)
CONFIRMED  → emerald (Confirmé)
COMPLETED  → emerald (Terminé)
CANCELLED  → rose    (Annulé)
NO_SHOW    → slate   (Absent)
```

## 📱 Responsive Design

### Breakpoints

```scss
// Mobile (< 768px)
- Layout empilé verticalement
- Sidebar caché
- Calendar: scroll horizontal

// Tablet (768px - 1024px)
- Layout 2 colonnes
- Sidebar visible
- Calendar: 5 jours visibles

// Desktop (> 1024px)
- Layout complet
- Sidebar + main content
- Calendar: 7 jours visibles
```

## 🔐 Guards et Permissions

```typescript
// Route protection
{
  path: 'appointments',
  canActivate: [AuthGuard],
  children: [
    { path: '', component: AppointmentCalendarComponent },
    { path: 'new', component: AppointmentFormComponent },
    { path: ':id', component: AppointmentDetailComponent }
  ]
}
```

## 🧪 Tests suggérés

### Unit Tests

```typescript
describe('AppointmentService', () => {
  it('should calculate time position correctly', () => {
    const event = service.createCalendarEvent(appointment);
    expect(event.top).toBe(expectedValue);
  });

  it('should filter appointments by doctor', () => {
    const filtered = service.filterByDoctor(appointments, doctorId);
    expect(filtered.length).toBe(expected);
  });
});
```

### Integration Tests

```typescript
it('should load week appointments on init', fakeAsync(() => {
  component.ngOnInit();
  tick();
  expect(component.appointments.length).toBeGreaterThan(0);
}));
```

## 📝 Exemples d'utilisation

### Créer un rendez-vous

```typescript
const appointmentData: AppointmentDTO = {
  patientId: 1,
  doctorId: 2,
  dateTime: '2024-01-15T09:30:00',
  duration: 30,
  status: AppointmentStatus.SCHEDULED,
  type: AppointmentType.CONSULTATION,
  reason: 'Annual checkup'
};

this.appointmentService.createAppointment(appointmentData)
  .subscribe(appointment => {
    console.log('Created:', appointment);
  });
```

### Récupérer les créneaux disponibles

```typescript
const doctorId = 1;
const date = new Date('2024-01-15');

this.appointmentService.getAvailableSlots(doctorId, date)
  .subscribe(slots => {
    this.availableSlots = slots.filter(s => s.available);
  });
```

### Filtrer les médecins

```typescript
const filters: DoctorSearchFilters = {
  specialty: Specialty.CARDIOLOGY,
  status: DoctorStatus.AVAILABLE,
  minRating: 4.0,
  sortBy: 'rating'
};

this.doctorService.searchDoctors(filters)
  .subscribe(doctors => {
    this.doctors = doctors;
  });
```

## 🚀 Commandes de génération

```bash
# Générer le module appointments
ng g module features/appointments --routing

# Générer les composants
ng g component features/appointments/appointment-calendar
ng g component features/appointments/appointment-form
ng g component features/appointments/appointment-detail
ng g component features/appointments/doctor-list

# Copier les fichiers fournis
# - appointment.model.ts → core/models/
# - doctor.model.ts → core/models/
# - appointment.service.ts → core/services/
# - doctor.service.ts → core/services/
# - appointment-calendar.component.ts → features/appointments/appointment-calendar/
```

## ⚡ Optimisations

### Performance

```typescript
// Utiliser ChangeDetectionStrategy.OnPush
@Component({
  selector: 'app-appointment-calendar',
  changeDetection: ChangeDetectionStrategy.OnPush
})

// Utiliser trackBy pour les *ngFor
trackByAppointmentId(index: number, item: Appointment): number {
  return item.id!;
}
```

### Caching

```typescript
// Cache les médecins pour éviter les requêtes répétées
private doctorsCache: Doctor[] | null = null;

getDoctors(): Observable<Doctor[]> {
  if (this.doctorsCache) {
    return of(this.doctorsCache);
  }
  return this.http.get<Doctor[]>(...).pipe(
    tap(doctors => this.doctorsCache = doctors)
  );
}
```

## 🎨 Styles personnalisés

```scss
// Calendar scrollbar custom
.calendar-scroll::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.calendar-scroll::-webkit-scrollbar-thumb {
  background-color: #cbd5e1;
  border-radius: 4px;
}

// Appointment event hover effect
.appointment-event {
  transition: all 0.2s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
}
```

## 📊 Gestion d'état (optionnel avec NgRx)

```typescript
// Actions
export const loadAppointments = createAction('[Appointments] Load');
export const loadAppointmentsSuccess = createAction(
  '[Appointments] Load Success',
  props<{ appointments: Appointment[] }>()
);

// Reducer
export const appointmentsReducer = createReducer(
  initialState,
  on(loadAppointmentsSuccess, (state, { appointments }) => ({
    ...state,
    appointments,
    loading: false
  }))
);
```

## 🔄 Temps réel (optionnel avec WebSocket)

```typescript
// WebSocket pour les mises à jour en temps réel
this.wsService.onAppointmentUpdate()
  .subscribe(appointment => {
    this.updateAppointmentInList(appointment);
    this.notifyUser('Appointment updated');
  });
```

## 📚 Ressources additionnelles

- [FullCalendar Angular](https://fullcalendar.io/docs/angular) - Alternative pour le calendrier
- [PrimeNG Calendar](https://primeng.org/calendar) - Composant calendrier avancé
- [Angular Material Datepicker](https://material.angular.io/components/datepicker) - Pour la sélection de date

## ✅ Checklist d'implémentation

- [ ] Modèles créés (appointment, doctor)
- [ ] Services créés (appointment, doctor)
- [ ] Calendar component (HTML + TS)
- [ ] Appointment form component
- [ ] Appointment detail component
- [ ] Doctor list component
- [ ] Routing configuré
- [ ] Guards ajoutés
- [ ] Tests unitaires
- [ ] Tests d'intégration
- [ ] Documentation API
- [ ] Optimisations performance
- [ ] Responsive design vérifié
- [ ] Accessibilité (a11y)
- [ ] Dark mode testé

## 🎯 Résumé

Ce module Appointments offre une solution complète pour :
- ✅ Visualiser les rendez-vous en vue calendrier
- ✅ Créer et modifier des rendez-vous
- ✅ Gérer les disponibilités des médecins
- ✅ Filtrer et rechercher
- ✅ Gérer les statuts et actions
- ✅ Interface responsive et moderne
