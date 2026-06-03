import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';

import { DownloadComponent } from './download.component';
import { FileService } from '../../../core/services/file.service';
import { AuthService } from '../../../core/services/auth.service';
import { FileResponseDTO } from '../../../core/models/file.model';

const mockFile: FileResponseDTO = {
  uuid: 'abc-123',
  name: 'test.pdf',
  size: 2_500_000,
  expiredAt: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString(),
  isProtected: 'true',
};

describe('DownloadComponent', () => {
  let component: DownloadComponent;
  let fixture: ComponentFixture<DownloadComponent>;
  let fileServiceMock: jest.Mocked<Pick<FileService, 'getFile' | 'downloadFile'>>;

  beforeEach(async () => {
    fileServiceMock = {
      getFile: jest.fn().mockReturnValue(of(mockFile)),
      downloadFile: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [DownloadComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: FileService, useValue: fileServiceMock },
        { provide: AuthService, useValue: { isLoggedIn: () => false } },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => 'abc-123' } } } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DownloadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and populate fileResponseDTO on init', () => {
    expect(component).toBeTruthy();
    expect(component.fileResponseDTO).toEqual(mockFile);
    expect(component.loading).toBe(false);
  });

  it('should set errorMessage when getFile fails', async () => {
    fileServiceMock.getFile.mockReturnValue(throwError(() => new Error()));
    const f = TestBed.createComponent(DownloadComponent);
    f.detectChanges();
    await f.whenStable();
    expect(f.componentInstance.errorMessage).toBe('Fichier introuvable.');
  });

  it('isExpired should return true for a past date and false when null', () => {
    expect(component.daysRemaining).toBeGreaterThan(0);

    component.fileResponseDTO = { ...mockFile, expiredAt: new Date(Date.now() - 1000).toISOString() };
    expect(component.isExpired).toBe(true);

    component.fileResponseDTO = null;
    expect(component.isExpired).toBe(false);
    expect(component.daysRemaining).toBe(0);
  });

  it('fileSizeMo should return formatted size or empty string', () => {
    expect(component.fileSizeMo).toBe('2.5 Mo');
    component.fileResponseDTO = null;
    expect(component.fileSizeMo).toBe('');
  });

  it('onDownload should trigger anchor click on success', () => {
    const blob = new Blob(['data']);
    fileServiceMock.downloadFile.mockReturnValue(of(blob));
    global.URL.createObjectURL = jest.fn().mockReturnValue('blob:x');
    global.URL.revokeObjectURL = jest.fn();
    const mockAnchor = { href: '', download: '', click: jest.fn() } as unknown as HTMLAnchorElement;
    jest.spyOn(document, 'createElement').mockReturnValue(mockAnchor);

    component.password = 'secret';
    component.fileResponseDTO = null;
    component.onDownload();

    expect(mockAnchor.click).toHaveBeenCalled();
    jest.restoreAllMocks();
  });

  it('onDownload should set errorMessage on failure', () => {
    fileServiceMock.downloadFile.mockReturnValue(throwError(() => new Error()));
    component.onDownload();
    expect(component.errorMessage).toBe('Mot de passe incorrect ou fichier indisponible.');
  });
});
