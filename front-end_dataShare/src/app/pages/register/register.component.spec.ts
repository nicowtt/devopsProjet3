import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';

import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let httpMock: HttpTestingController;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should send POST request and navigate to /login on successful register', () => {
    // GIVEN
    component.registerForm.setValue({
      email: 'test@gmail.com',
      password: 'password123',
      confirmPassword: 'password123'
    });
    const navigateSpy = jest.spyOn(router, 'navigate');

    // WHEN
    component.onSubmit();

    // THEN
    const req = httpMock.expectOne('/api/users');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email: 'test@gmail.com', password: 'password123' });
    req.flush(null, { status: 201, statusText: 'Created' });

    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });
});
