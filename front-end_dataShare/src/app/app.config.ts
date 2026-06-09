import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { LUCIDE_ICONS, LucideIconProvider, Lock, CloudUpload, CloudDownload, File, FileImage, FileMusic, FilePlay, Eye, EyeOff, Info, TriangleAlert, X, Menu, LogOut, Trash, ArrowRight, EllipsisVertical, Copy } from 'lucide-angular';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideToastr } from 'ngx-toastr';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptors([authInterceptor])),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideAnimations(),
    provideToastr({ timeOut: 3000, positionClass: 'toast-top-center', preventDuplicates: true }),
    { provide: LUCIDE_ICONS, useValue: new LucideIconProvider({ Lock, CloudUpload, CloudDownload, File, FileImage, FileMusic, FilePlay, Eye, EyeOff, Info, TriangleAlert, X, Menu, LogOut, Trash, ArrowRight, EllipsisVertical, Copy }), multi: true },
  ]
};
