import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';

import { FileService } from './file.service';
import { FileDTO, FileResponseDTO } from '../models/file.model';

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

  describe('getFiles()', () => {
    it('must send GET request to /api/files and return list', () => {
      // GIVEN
      const mockFiles: FileResponseDTO[] = [
        { uuid: '1', name: 'a.pdf', createdAt: '2024-01-01', expiredAt: '2099-01-01' },
        { uuid: '2', name: 'b.png', createdAt: '2024-01-01', expiredAt: '2099-01-01' },
      ];

      // WHEN
      let result: FileResponseDTO[] | undefined;
      service.getFiles().subscribe(files => result = files);

      // THEN
      const req = httpMock.expectOne('/api/files');
      expect(req.request.method).toBe('GET');
      req.flush(mockFiles);
      expect(result).toEqual(mockFiles);
    });
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
