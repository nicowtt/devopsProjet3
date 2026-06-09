import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { LUCIDE_ICONS, LucideIconProvider, File, FileImage, FileMusic, FilePlay, X, Menu, LogOut, Trash, ArrowRight, EllipsisVertical, Lock } from 'lucide-angular';

import { SpaceComponent } from './space.component';
import { FileService } from '../../core/services/file.service';
import { AuthService } from '../../core/services/auth.service';
import { FileResponseDTO } from '../../core/models/file.model';

const FUTUR = '2099-01-01';
const PAST   = '2020-01-01';

const mockFiles: FileResponseDTO[] = [
  { uuid: '1', name: 'test1.pdf', size: 2_000_000, createdAt: '2026-06-04', expiredAt: FUTUR },
  { uuid: '2', name: 'test2.png',    size:   500_000, createdAt: '2026-06-04', expiredAt: PAST  },
];

describe('SpaceComponent', () => {
  let component: SpaceComponent;
  let fixture: ComponentFixture<SpaceComponent>;
  let fileServiceMock: jest.Mocked<Pick<FileService, 'getFiles'>>;

  beforeEach(async () => {
    fileServiceMock = { getFiles: jest.fn().mockReturnValue(of(mockFiles)) };

    await TestBed.configureTestingModule({
      imports: [SpaceComponent],
      providers: [
        provideRouter([]),
        { provide: LUCIDE_ICONS, useValue: new LucideIconProvider({ File, FileImage, FileMusic, FilePlay, X, Menu, LogOut, Trash, ArrowRight, EllipsisVertical, Lock }), multi: true },
        { provide: FileService,   useValue: fileServiceMock },
        { provide: AuthService,   useValue: { removeToken: jest.fn() } },
        { provide: ToastrService, useValue: { info: jest.fn() } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SpaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load files on init', () => {
    expect(fileServiceMock.getFiles).toHaveBeenCalled();
    expect(component.files).toEqual(mockFiles);
  });

  describe('filteredFiles', () => {
    it('returns all files for filter "tous"', () => {
      component.filter = 'tous';
      expect(component.filteredFiles.length).toBe(2);
    });

    it('returns only active files for filter "actifs"', () => {
      component.filter = 'actifs';
      expect(component.filteredFiles.map(f => f.uuid)).toEqual(['1']);
    });

    it('returns only expired files for filter "expire"', () => {
      component.filter = 'expire';
      expect(component.filteredFiles.map(f => f.uuid)).toEqual(['2']);
    });
  });
});
