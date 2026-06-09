import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FileRequestDTO, FileResponseDTO } from '../models/file.model';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  constructor(private httpClient: HttpClient) {}

  upload(file: File, fileRequestDTO: FileRequestDTO): Observable<FileResponseDTO> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('metadata', new Blob([JSON.stringify(fileRequestDTO)], { type: 'application/json' }));
    return this.httpClient.post<FileResponseDTO>('/api/files', formData);
  }

  getFile(uuid: string): Observable<FileResponseDTO> {
    return this.httpClient.get<FileResponseDTO>(`/api/files/${uuid}`);
  }

  downloadFile(uuid: string, password?: string): Observable<Blob> {
    return this.httpClient.post(`/api/files/download/${uuid}`, password ?? null, {
      responseType: 'blob' as const
    });
  }

  getFiles(): Observable<FileResponseDTO[]> {
    return this.httpClient.get<FileResponseDTO[]>('/api/files');
  }

  deleteFile(uuid: string): Observable<void> {
    return this.httpClient.delete<void>(`/api/files/${uuid}`);
  }
}
