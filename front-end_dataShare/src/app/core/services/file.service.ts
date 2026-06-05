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
    const params: Record<string, string> = password ? { password } : {};
    return this.httpClient.get(`/api/files/${uuid}`, {
      params,
      responseType: 'blob' as const
    }) as Observable<Blob>;
  }

  getFiles(): Observable<FileResponseDTO[]> {
    return this.httpClient.get<FileResponseDTO[]>('/api/files');
  }

  deleteFile(uuid: string): Observable<void> {
    return this.httpClient.delete<void>(`/api/files/${uuid}`);
  }
}
