import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FileUploadResponse {
  uuid: string;
  name: string;
  size: number;
  expiredAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class FileService {
  constructor(private httpClient: HttpClient) {}

  upload(file: File, dayBeforeExpiration: number, password?: string): Observable<FileUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);

    const metadata: Record<string, unknown> = { dayBeforeExpiration };
    if (password) metadata['password'] = password;

    formData.append('metadata', new Blob([JSON.stringify(metadata)], { type: 'application/json' }));

    return this.httpClient.post<FileUploadResponse>('/api/files', formData);
  }
}
