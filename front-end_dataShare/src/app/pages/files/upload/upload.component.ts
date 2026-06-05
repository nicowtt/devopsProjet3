import { Component, ElementRef, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { LucideAngularModule } from 'lucide-angular';
import { ToastrService } from 'ngx-toastr';
import { formatFileSize, fileIconName, ALLOWED_MIME_TYPES, ACCEPT_FILE_TYPE } from '../../../shared/file.util';
import { FileService } from '../../../core/services/file.service';
import { FileRequestDTO, FileResponseDTO } from '../../../core/models/file.model';
import { AuthService } from '../../../core/services/auth.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [RouterLink, FormsModule, LucideAngularModule],
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.css'
})
export class UploadComponent {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  isLoggedIn = false;
  readonly acceptFileType = ACCEPT_FILE_TYPE;

  selectedFile: File | null = null;
  isDragging = false;
  password = '';
  expiration = 7;
  shareUrl: string | null = null;
  linkCopied = false;
  errorMessage = '';
  passwordError = '';

  constructor(
    private fileService: FileService,
    private authService: AuthService,
    private toastr: ToastrService
  ) {
    this.isLoggedIn = this.authService.isLoggedIn();
  }

  get fileSizeMo(): string {
    if (!this.selectedFile) return '';
    return formatFileSize(this.selectedFile.size);
  }

  get truncatedName(): string {
    if (!this.selectedFile) return '';
    const name = this.selectedFile.name;
    return name.length > 25 ? name.slice(0, 25) + '...' : name;
  }

  get expirationMessage(): string {
    return this.expiration === 1 ? '1 jour' : `${this.expiration} jours`;
  }

  get fileIcon(): string {
    return this.selectedFile ? fileIconName(this.selectedFile.name) : 'file';
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    const file = input.files[0];
    if (!ALLOWED_MIME_TYPES.has(file.type)) {
      this.toastr.warning('Ce type de fichier n\'est pas autorisé.');
      input.value = '';
      return;
    }
    if (file.size > 1_000_000_000) {
      this.toastr.warning('Le fichier ne doit pas dépasser 1 Go.');
      input.value = '';
      return;
    }
    this.errorMessage = '';
    this.selectedFile = file;
    this.shareUrl = null;
  }

  changeFile(): void {
    this.fileInput.nativeElement.value = '';
    this.fileInput.nativeElement.click();
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(): void {
    this.isDragging = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = false;
    const file = event.dataTransfer?.files[0];
    if (!file) return;
    if (!ALLOWED_MIME_TYPES.has(file.type)) {
      this.toastr.warning('Ce type de fichier n\'est pas autorisé.');
      return;
    }
    if (file.size > 1_000_000_000) {
      this.toastr.warning('Le fichier ne doit pas dépasser 1 Go.');
      return;
    }
    this.errorMessage = '';
    this.selectedFile = file;
  }

  onSubmit(): void {
    if (!this.selectedFile) return;
    if (this.expiration < 1 || this.expiration > 7) {
      this.expiration = 7;
    }
    if (this.password && this.password.length < 6) {
      this.passwordError = 'Le mot de passe doit contenir au moins 6 caractères.';
      return;
    }
    this.passwordError = '';
    this.errorMessage = '';
    const fileRequestDTO: FileRequestDTO = {
      dayBeforeExpiration: this.expiration,
      ...(this.password ? { password: this.password } : {})
    };
    this.fileService.upload(this.selectedFile, fileRequestDTO).subscribe({
      next: (fileResponseDTO: FileResponseDTO) => {
        this.shareUrl = `${environment.baseUrl}/download/${fileResponseDTO.uuid}`;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Une erreur est survenue lors du téléversement.';
      }
    });
  }

  copyLink(): void {
    if (!this.shareUrl) return;
    navigator.clipboard.writeText(this.shareUrl).then(() => {
      this.linkCopied = true;
      setTimeout(() => this.linkCopied = false, 2000);
    });
  }
}
