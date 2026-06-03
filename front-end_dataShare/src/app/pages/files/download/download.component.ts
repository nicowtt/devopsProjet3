import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FileService } from '../../../core/services/file.service';
import { FileResponseDTO } from '../../../core/models/file.model';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-download',
  standalone: true,
  imports: [RouterLink, FormsModule],
  templateUrl: './download.component.html',
  styleUrl: './download.component.css'
})
export class DownloadComponent implements OnInit {
  fileResponseDTO: FileResponseDTO | null = null;
  password = '';
  errorMessage = '';
  loading = true;
  isLoggedIn = false;

  constructor(
    private route: ActivatedRoute,
    private fileService: FileService,
    authService: AuthService
  ) {
    this.isLoggedIn = authService.isLoggedIn();
  }

  ngOnInit(): void {
    const uuid = this.route.snapshot.paramMap.get('uuid') ?? '';
    this.fileService.getFile(uuid).subscribe({
      next: (fileResponseDTO) => {
        this.fileResponseDTO = fileResponseDTO;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Fichier introuvable.';
        this.loading = false;
      }
    });
  }

  get isExpired(): boolean {
    if (!this.fileResponseDTO?.expiredAt) return false;
    return new Date(this.fileResponseDTO.expiredAt) < new Date();
  }

  get daysRemaining(): number {
    if (!this.fileResponseDTO) return 0;
    const diff = new Date(this.fileResponseDTO.expiredAt).getTime() - new Date().getTime();
    return Math.ceil(diff / (1000 * 60 * 60 * 24));
  }

  get hasPassword(): boolean {
    return this.fileResponseDTO?.hasPassword ?? false;
  }

  get fileSizeMo(): string {
    if (!this.fileResponseDTO?.size) return '';
    return (this.fileResponseDTO.size / 1_000_000).toFixed(1) + ' Mo';
  }

  onDownload(): void {
    const uuid = this.route.snapshot.paramMap.get('uuid') ?? '';
    this.fileService.downloadFile(uuid, this.password || undefined).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = this.fileResponseDTO?.name ?? 'fichier';
        a.click();
        URL.revokeObjectURL(url);
      },
      error: () => {
        this.errorMessage = 'Mot de passe incorrect ou fichier indisponible.';
      }
    });
  }
}
