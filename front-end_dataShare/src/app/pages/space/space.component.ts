import { Component, HostListener, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { ToastrService } from 'ngx-toastr';
import { FileService } from '../../core/services/file.service';
import { AuthService } from '../../core/services/auth.service';
import { FileResponseDTO } from '../../core/models/file.model';
import { fileIconName, expiryText as expiryText, formatFileSize } from '../../shared/file.util';

type Filter = 'tous' | 'actifs' | 'expire';

@Component({
  selector: 'app-space',
  standalone: true,
  imports: [RouterLink, LucideAngularModule, DatePipe],
  templateUrl: './space.component.html',
  styleUrl: './space.component.css'
})
export class SpaceComponent implements OnInit {
  files: FileResponseDTO[] = [];
  loading = true;
  filter: Filter = 'tous';
  sidebarOpen = false;
  openMenuUuid: string | null = null;
  confirmDeleteUuid: string | null = null;

  constructor(
    private fileService: FileService,
    private authService: AuthService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.fileService.getFiles().subscribe({
      next: files => {
        this.files = files;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.toastr.error(err.error?.message ?? 'Erreur lors du chargement des fichiers.');
      }
    });
  }

  get filteredFiles(): FileResponseDTO[] {
    if (this.filter === 'actifs') return this.files.filter(f => !this.isExpired(f));
    if (this.filter === 'expire') return this.files.filter(f => this.isExpired(f));
    return this.files;
  }

  isExpired(file: FileResponseDTO): boolean {
    return new Date(file.expiredAt) < new Date();
  }

  expiryText(file: FileResponseDTO): string {
    return expiryText(file.expiredAt);
  }

  setFilter(f: Filter): void {
    this.filter = f;
  }

  @HostListener('document:click')
  closeMenu(): void {
    this.openMenuUuid = null;
    this.confirmDeleteUuid = null;
  }

  toggleMenu(uuid: string, event: MouseEvent): void {
    event.stopPropagation();
    this.openMenuUuid = this.openMenuUuid === uuid ? null : uuid;
    this.confirmDeleteUuid = null;
  }

  requestDelete(uuid: string): void {
    this.confirmDeleteUuid = uuid;
  }

  cancelDelete(): void {
    this.confirmDeleteUuid = null;
  }

  fileIconName(file: FileResponseDTO): string {
    return fileIconName(file.name);
  }

  fileSize(file: FileResponseDTO): string {
    return file.size ? formatFileSize(file.size) : '';
  }

  navigateToFile(uuid: string): void {
    this.router.navigate(['/download', uuid]);
  }

  logout(): void {
    this.authService.removeToken();
    this.toastr.info('Vous avez été déconnecté.');
    this.router.navigate(['/login']);
  }

  deleteFile(uuid: string): void {
    this.fileService.deleteFile(uuid).subscribe({
      next: () => {
        this.files = this.files.filter(f => f.uuid !== uuid);
        this.openMenuUuid = null;
        this.confirmDeleteUuid = null;
        this.toastr.success('Le fichier a bien été supprimé.');
      },
      error: (err) => {
        this.toastr.error(err.error?.message ?? 'Erreur lors de la suppression du fichier.');
      }
    });
  }
}
