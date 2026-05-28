import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

import { UserService } from './user.service';
import { RegisterDTO } from '../models/register.model';

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
      const req = httpMock.expectOne('/api/register');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newUser);
      req.flush(null, { status: 201, statusText: 'Created' });
    });
  });
});
