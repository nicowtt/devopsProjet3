import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RegisterDTO } from '../models/register.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private httpClient: HttpClient) { }

  register(registerDTO: RegisterDTO): Observable<Object> {
    return this.httpClient.post('/api/register', registerDTO);
  }
}
