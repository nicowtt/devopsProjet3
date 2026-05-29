import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';

import { LoginComponent } from './login.component';
import { AuthService } from '../../core/services/auth.service';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let httpMock: HttpTestingController;
  let router: Router;
  let authService: AuthService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    authService = TestBed.inject(AuthService);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should save token and navigate on successful login', () => {
    // GIVEN
    component.loginForm.setValue({ email: 'test@gmail.com', password: 'password123' });
    const saveTokenSpy = jest.spyOn(authService, 'saveToken');
    const navigateSpy = jest.spyOn(router, 'navigate');

    // WHEN
    component.onSubmit();

    // THEN
    const req = httpMock.expectOne('/api/login');
    expect(req.request.method).toBe('POST');
    req.flush('fake-jwt-token', { status: 200, statusText: 'OK' });

    expect(saveTokenSpy).toHaveBeenCalledWith('fake-jwt-token');
    expect(navigateSpy).toHaveBeenCalledWith(['/']);
  });
});
