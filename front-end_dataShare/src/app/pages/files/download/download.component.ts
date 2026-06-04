import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FileService } from '../../../core/services/file.service';
import { FileResponseDTO } from '../../../core/models/file.model';
import { AuthService } from '../../../core/services/auth.service';
import { ToastrService } from 'ngx-toastr';
import { LucideAngularModule } from 'lucide-angular';
import { formatFileSize, daysRemaining, fileIconName } from '../../../shared/file.util';

@Component({
  selector: 'app-download',
  standalone: true,
  imports: [RouterLink, FormsModule, LucideAngularModule],
  templateUrl: './download.component.html',
  styleUrl: './download.component.css'
})
export class DownloadComponent implements OnInit {
  fileResponseDTO: FileResponseDTO | null = null;
  password = '';
  fileNotFound = false;
  loading = true;
  isLoggedIn = false;

  constructor(
    private route: ActivatedRoute,
    private fileService: FileService,
    private toastr: ToastrService,
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
        this.fileNotFound = true;
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
    return daysRemaining(this.fileResponseDTO.expiredAt);
  }

  get hasPassword(): boolean {
    return this.fileResponseDTO?.hasPassword ?? false;
  }

  get iconName(): string {
    return fileIconName(this.fileResponseDTO?.name ?? '');
  }

  get fileSizeMo(): string {
    const size = this.fileResponseDTO?.size;
    if (!size) return '';
    return formatFileSize(size);
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
        this.toastr.error('Mot de passe incorrect ou fichier indisponible.');
      }
    });
  }
}
