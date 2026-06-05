import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { provideToastr } from 'ngx-toastr';
import { LUCIDE_ICONS, LucideIconProvider, File as FileIcon, FileImage, FileMusic, FilePlay, Lock, Eye, EyeOff } from 'lucide-angular';

import { DownloadComponent } from './download.component';
import { FileService } from '../../../core/services/file.service';
import { AuthService } from '../../../core/services/auth.service';
import { FileResponseDTO } from '../../../core/models/file.model';

const mockFile: FileResponseDTO = {
  uuid: 'abc-123',
  name: 'test.pdf',
  size: 2_500_000,
  createdAt: new Date().toISOString(),
  expiredAt: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString(),
  hasPassword: true,
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
        provideToastr(),
        { provide: LUCIDE_ICONS, useValue: new LucideIconProvider({ File: FileIcon, FileImage, FileMusic, FilePlay, Lock, Eye, EyeOff }), multi: true },
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

  it('isExpired should return true for a past date and false when null', () => {
    expect(component.daysRemaining).toBeGreaterThan(0);

    component.fileResponseDTO = { ...mockFile, expiredAt: new Date(Date.now() - 1000).toISOString() };
    expect(component.isExpired).toBe(true);

    component.fileResponseDTO = null;
    expect(component.isExpired).toBe(false);
    expect(component.daysRemaining).toBe(0);
  });
});
