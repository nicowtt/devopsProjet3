import { Routes } from '@angular/router';
import { RegisterComponent } from './pages/register/register.component';
import { LoginComponent } from './pages/login/login.component';
import { HomeComponent } from './pages/home/home.component';
import { UploadComponent } from './pages/files/upload/upload.component';
import { DownloadComponent } from './pages/files/download/download.component';
import { SpaceComponent } from './pages/space/space.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'upload',
    component: UploadComponent
  },
  {
    path: 'download/:uuid',
    component: DownloadComponent
  },
  {
    path: 'space',
    component: SpaceComponent,
    canActivate: [authGuard]
  }
];
