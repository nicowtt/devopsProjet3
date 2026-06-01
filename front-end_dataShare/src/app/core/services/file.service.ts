import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FileDTO } from '../models/file.model';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  constructor(private httpClient: HttpClient) {}

  upload(file: File, fileDTO: FileDTO): Observable<FileDTO> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('metadata', new Blob([JSON.stringify(fileDTO)], { type: 'application/json' }));
    return this.httpClient.post<FileDTO>('/api/files', formData);
  }
}
