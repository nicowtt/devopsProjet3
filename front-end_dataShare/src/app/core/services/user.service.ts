import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginDTO, RegisterDTO } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private httpClient: HttpClient) { }

  register(registerDTO: RegisterDTO): Observable<Object> {
    return this.httpClient.post('/api/users', registerDTO);
  }

  login(loginDTO: LoginDTO): Observable<string> {
    return this.httpClient.post('/api/users/login', loginDTO, { responseType: 'text' });
  }
}
