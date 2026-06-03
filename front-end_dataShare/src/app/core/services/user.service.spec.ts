import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

import { UserService } from './user.service';
import { LoginDTO, RegisterDTO } from '../models/user.model';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ]
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // instanciate service
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('register()', () => {
    it('must send POST request to /api/register with correct data', () => {
      // GIVEN
      const newUser: RegisterDTO = {
        email: 'untest@gmail.Com',
        password: 'password'
      };

      // WHEN
      service.register(newUser).subscribe();

      // THEN
      const req = httpMock.expectOne('/api/users');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newUser);
      req.flush(null, { status: 201, statusText: 'Created' });
    });
  });

  describe('login()', () => {
    it('must send POST request to /api/login with correct data', () => {
      // GIVEN
      const loginRequestDTO: LoginDTO = { email: 'unAutreTest@gmail.com', password: 'password' };
      const fakeToken = 'jwtToken';

      // WHEN
      service.login(loginRequestDTO).subscribe();

      // THEN
      const req = httpMock.expectOne('/api/users/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(loginRequestDTO);
      req.flush(fakeToken);
    });

    it('must return error on bad credentials', () => {
      // GIVEN
      const loginDTO: LoginDTO = { email: 'unAutreTest@gmail.com', password: 'badPassword' };

      // WHEN
      service.login(loginDTO).subscribe({
        error: err => expect(err.status).toBe(401)
      });

      // THEN
      const req = httpMock.expectOne('/api/users/login');
      req.flush(null, { status: 401, statusText: 'Unauthorized' });
    });
  });
});
