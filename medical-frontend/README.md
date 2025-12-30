# Medical Frontend - Angular Application

## 📋 Vue d'ensemble

Application Angular pour le système de gestion médicale, connectée aux microservices Spring Boot.

## 🏗️ Structure du Projet

```
medical-frontend/
├── src/
│   ├── app/
│   │   ├── core/
│   │   │   ├── guards/
│   │   │   │   └── auth.guard.ts
│   │   │   ├── interceptors/
│   │   │   │   ├── auth.interceptor.ts
│   │   │   │   ├── error.interceptor.ts
│   │   │   │   └── loading.interceptor.ts
│   │   │   ├── models/
│   │   │   │   ├── api-response.model.ts
│   │   │   │   ├── patient.model.ts
│   │   │   │   ├── dossier.model.ts
│   │   │   │   ├── appointment.model.ts
│   │   │   │   └── doctor.model.ts
│   │   │   ├── services/
│   │   │   │   ├── patient.service.ts
│   │   │   │   ├── dossier.service.ts
│   │   │   │   ├── appointment.service.ts
│   │   │   │   ├── doctor.service.ts
│   │   │   │   └── auth.service.ts
│   │   │   └── core.module.ts
│   │   │
│   │   ├── features/
│   │   │   ├── patients/
│   │   │   │   ├── patient-detail/
│   │   │   │   │   ├── patient-detail.component.ts
│   │   │   │   │   ├── patient-detail.component.html
│   │   │   │   │   └── patient-detail.component.scss
│   │   │   │   ├── patient-form/
│   │   │   │   │   ├── patient-form.component.ts
│   │   │   │   │   ├── patient-form.component.html
│   │   │   │   │   ├── patient-form.component.scss
│   │   │   │   │   ├── dossier-form.component.ts
│   │   │   │   │   └── dossier-form.component.html
│   │   │   │   ├── patient-list/
│   │   │   │   │   ├── patient-list.component.ts
│   │   │   │   │   ├── patient-list.component.html
│   │   │   │   │   └── patient-list.component.scss
│   │   │   │   ├── patients.module.ts
│   │   │   │   └── patients-routing.module.ts
│   │   │   │
│   │   │   ├── appointments/
│   │   │   ├── dashboard/
│   │   │   ├── doctors/
│   │   │   └── dossiers/
│   │   │
│   │   ├── shared/
│   │   │   ├── components/
│   │   │   │   ├── header/
│   │   │   │   │   ├── header.component.ts
│   │   │   │   │   ├── header.component.html
│   │   │   │   │   └── header.component.scss
│   │   │   │   ├── sidebar/
│   │   │   │   │   ├── sidebar.component.ts
│   │   │   │   │   ├── sidebar.component.html
│   │   │   │   │   └── sidebar.component.scss
│   │   │   │   └── loading/
│   │   │   │       ├── loading.component.ts
│   │   │   │       ├── loading.component.html
│   │   │       └── loading.component.scss
│   │   │   ├── pipes/
│   │   │   └── shared.module.ts
│   │   │
│   │   ├── app.component.ts
│   │   ├── app.component.html
│   │   ├── app.module.ts
│   │   └── app-routing.module.ts
│   │
│   ├── assets/
│   ├── environments/
│   │   ├── environment.ts
│   │   └── environment.prod.ts
│   ├── styles/
│   │   └── tailwind.css
│   ├── index.html
│   └── main.ts
│
├── angular.json
├── package.json
├── tailwind.config.js
└── tsconfig.json
```

## 📦 Installation

### Prérequis
- Node.js (v18 ou supérieur)
- npm (v9 ou supérieur)
- Angular CLI (v18)

### Étapes d'installation

```bash
# 1. Créer le projet Angular
ng new medical-frontend --routing --style=scss

# 2. Installer les dépendances
cd medical-frontend
npm install

# 3. Installer Tailwind CSS
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init

# 4. Installer Material Icons (via CDN dans index.html)
# Déjà configuré dans le template HTML
```

## ⚙️ Configuration

### 1. Environnement (src/environments/environment.ts)

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080', // API Gateway URL
  endpoints: {
    patients: '/api/patients',
    dossiers: '/api/dossiers',
    appointments: '/api/appointments',
    doctors: '/api/doctors',
    auth: '/api/auth'
  }
};
```

### 2. Tailwind Configuration (tailwind.config.js)

```javascript
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        'primary': '#0066cc',
        'primary-dark': '#0052a3',
        'background-light': '#f5f7f8',
        'background-dark': '#0f1923',
        'surface-light': '#ffffff',
        'surface-dark': '#1e293b',
        'border-light': '#e2e8f0',
        'border-dark': '#334155',
      },
      fontFamily: {
        'display': ['Inter', 'sans-serif']
      },
    },
  },
  plugins: [
    require('@tailwindcss/forms'),
  ],
}
```

### 3. Styles globaux (src/styles.scss)

```scss
@tailwind base;
@tailwind components;
@tailwind utilities;

body {
  margin: 0;
  font-family: 'Inter', sans-serif;
}
```

### 4. Index.html - Ajouter Google Fonts et Material Icons

```html



  
  Medical Frontend
