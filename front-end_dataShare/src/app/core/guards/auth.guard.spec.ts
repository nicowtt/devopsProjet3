import { TestBed } from '@angular/core/testing';
import { Router, provideRouter, UrlTree } from '@angular/router';

import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('authGuard', () => {
  let authServiceMock: { isLoggedIn: jest.Mock };

  const runGuard = () =>
    TestBed.runInInjectionContext(() => authGuard({} as any, {} as any));

  beforeEach(() => {
    authServiceMock = { isLoggedIn: jest.fn() };

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock },
      ],
    });
  });

  it('returns true when user is logged in', () => {
    authServiceMock.isLoggedIn.mockReturnValue(true);
    expect(runGuard()).toBe(true);
  });

  it('redirects to /login when user is not logged in', () => {
    authServiceMock.isLoggedIn.mockReturnValue(false);
    const result = runGuard() as UrlTree;
    const router = TestBed.inject(Router);
    expect(result).toEqual(router.createUrlTree(['/login']));
  });
});
