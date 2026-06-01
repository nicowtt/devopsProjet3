import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

import { FileService } from './file.service';
import { FileDTO } from '../models/file.model';

describe('FileService', () => {
  let service: FileService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ]
    });

    service = TestBed.inject(FileService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('upload()', () => {
    it('must send POST request to /api/files with file and metadata', () => {
      // GIVEN
      const file = new File(['content'], 'test.txt', { type: 'text/plain' });
      const fileDTO: FileDTO = { name: 'test.txt', dayBeforeExpiration: 7 };
      const mockResponse: FileDTO = { uuid: 'abc-123', name: 'test.txt', dayBeforeExpiration: 7 };

      // WHEN
      service.upload(file, fileDTO).subscribe();

      // THEN
      const req = httpMock.expectOne('/api/files');
      expect(req.request.method).toBe('POST');
      expect(req.request.body instanceof FormData).toBe(true);
      expect(req.request.body.get('file')).toBe(file);
      req.flush(mockResponse, { status: 201, statusText: 'Created' });
    });
  });
});
