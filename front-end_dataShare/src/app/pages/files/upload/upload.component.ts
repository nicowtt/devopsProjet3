import { Component, ElementRef, ViewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FileService, FileUploadResponse } from '../../../core/services/file.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.css'
})
export class UploadComponent {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  isLoggedIn = false;

  selectedFile: File | null = null;
  password = '';
  expiration = 7;
  shareUrl: string | null = null;
  linkCopied = false;
  errorMessage = '';
  passwordError = '';

  constructor(
    private fileService: FileService,
    private authService: AuthService
  ) {
    this.isLoggedIn = this.authService.isLoggedIn();
  }

  get fileSizeMo(): string {
    if (!this.selectedFile) return '';
    return (this.selectedFile.size / 1_000_000).toFixed(1) + ' Mo';
  }

  get truncatedName(): string {
    if (!this.selectedFile) return '';
    const name = this.selectedFile.name;
    return name.length > 25 ? name.slice(0, 25) + '...' : name;
  }

  get expirationMessage(): string {
    return this.expiration === 1 ? '1 jour' : `${this.expiration} jours`;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    const file = input.files[0];
    if (file.size > 1_000_000_000) {
      this.errorMessage = 'Le fichier ne doit pas dépasser 1 Go.';
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

  onSubmit(): void {
    if (!this.selectedFile) return;
    if (this.password && this.password.length < 6) {
      this.passwordError = 'Le mot de passe doit contenir au moins 6 caractères.';
      return;
    }
    this.passwordError = '';
    this.errorMessage = '';
    this.fileService.upload(this.selectedFile, this.expiration, this.password || undefined).subscribe({
      next: (response: FileUploadResponse) => {
        this.shareUrl = `https://localhost:9000/${response.uuid}`;
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
