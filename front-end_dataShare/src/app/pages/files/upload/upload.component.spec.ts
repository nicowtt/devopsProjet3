import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { provideToastr, ToastrService } from 'ngx-toastr';
import { LUCIDE_ICONS, LucideIconProvider, Eye, EyeOff, File as FileIcon, FileImage, FileMusic, FilePlay } from 'lucide-angular';

import { UploadComponent } from './upload.component';

describe('UploadComponent', () => {
  let component: UploadComponent;
  let fixture: ComponentFixture<UploadComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        provideToastr(),
        { provide: LUCIDE_ICONS, useValue: new LucideIconProvider({ Eye, EyeOff, File: FileIcon, FileImage, FileMusic, FilePlay }), multi: true },
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UploadComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    fixture.detectChanges();
  });

  afterEach(() => httpMock.verify());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('fileSizeMo should return formatted size when file is selected', () => {
    component.selectedFile = new File(['x'.repeat(2_600_000)], 'test.jpg');
    expect(component.fileSizeMo).toContain('Mo');
  });

  it('truncatedName should truncate long filenames and return short ones as-is', () => {
    component.selectedFile = new File([''], 'un-nom-de-fichier-vraiment-tres-long.pdf');
    expect(component.truncatedName).toContain('...');
    component.selectedFile = new File([''], 'court.pdf');
    expect(component.truncatedName).toBe('court.pdf');
  });

  it('expirationMessage should return singular or plural', () => {
    component.expiration = 1;
    expect(component.expirationMessage).toBe('1 jour');
    component.expiration = 7;
    expect(component.expirationMessage).toBe('7 jours');
  });

  it('onFileSelected should set selectedFile for valid file and error for file > 1 Go', () => {
    const file = new File(['content'], 'test.pdf', { type: 'application/pdf' });
    component.onFileSelected({ target: { files: [file], value: '' } } as unknown as Event);
    expect(component.selectedFile).toBe(file);

    const toastr = TestBed.inject(ToastrService);
    const warnSpy = jest.spyOn(toastr, 'warning');
    const bigFile = { size: 2_000_000_000, name: 'big.zip', type: 'application/zip' } as File;
    component.onFileSelected({ target: { files: [bigFile], value: '' } } as unknown as Event);
    expect(warnSpy).toHaveBeenCalledWith('Le fichier ne doit pas dépasser 1 Go.');
  });

  it('onDragOver should set isDragging, onDragLeave should reset it', () => {
    component.onDragOver({ preventDefault: jest.fn() } as unknown as DragEvent);
    expect(component.isDragging).toBe(true);
    component.onDragLeave();
    expect(component.isDragging).toBe(false);
  });

  it('onDrop should set selectedFile or errorMessage based on file size', () => {
    const file = new File(['content'], 'drop.pdf', { type: 'application/pdf' });
    component.onDrop({ preventDefault: jest.fn(), dataTransfer: { files: [file] } } as unknown as DragEvent);
    expect(component.selectedFile).toBe(file);

    const toastr2 = TestBed.inject(ToastrService);
    const warnSpy2 = jest.spyOn(toastr2, 'warning');
    const bigFile = { size: 2_000_000_000, name: 'big.zip', type: 'application/zip' } as File;
    component.onDrop({ preventDefault: jest.fn(), dataTransfer: { files: [bigFile] } } as unknown as DragEvent);
    expect(warnSpy2).toHaveBeenCalledWith('Le fichier ne doit pas dépasser 1 Go.');
  });

  it('onSubmit should set passwordError when password is too short', () => {
    component.selectedFile = new File(['content'], 'test.pdf');
    component.password = 'abc';
    component.onSubmit();
    expect(component.passwordError).toBe('Le mot de passe doit contenir au moins 6 caractères.');
    httpMock.expectNone('/api/files');
  });

  it('onSubmit should set shareUrl on success and errorMessage on failure', () => {
    component.selectedFile = new File(['content'], 'test.pdf');
    component.onSubmit();
    httpMock.expectOne('/api/files').flush({ uuid: 'abc-123' }, { status: 201, statusText: 'Created' });
    expect(component.shareUrl).toContain('abc-123');

    component.selectedFile = new File(['content'], 'test.pdf');
    component.shareUrl = null;
    component.onSubmit();
    httpMock.expectOne('/api/files').flush({ message: 'Erreur' }, { status: 500, statusText: 'Error' });
    expect(component.errorMessage).toBe('Erreur');
  });
});
