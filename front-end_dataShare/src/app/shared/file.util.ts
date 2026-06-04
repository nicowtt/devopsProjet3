export const ALLOWED_MIME_TYPES = new Set([
  // Images
  'image/jpeg', 'image/png', 'image/gif', 'image/svg+xml', 'image/webp',
  // Vidéo
  'video/mp4', 'video/x-msvideo', 'video/x-matroska',
  // Audio
  'audio/mpeg', 'audio/wav', 'audio/ogg', 'audio/flac', 'audio/aac', 'audio/mp4',
  // Documents
  'application/pdf', 'text/plain',
  // Archives
  'application/zip', 'application/x-tar',
]);

export const ACCEPT_FILE_TYPE = [...ALLOWED_MIME_TYPES].join(',');

export function formatFileSize(bytes: number): string {
  if (bytes < 1_000_000) return Math.round(bytes / 1_000) + ' Ko';
  return Math.round(bytes / 1_000_000) + ' Mo';
}

export function expiryText(expiredAt: string): string {
  const now = Date.now();
  if (new Date(expiredAt).getTime() < now) return 'Expiré';
  const days = Math.ceil((new Date(expiredAt).getTime() - now) / 86_400_000);
  return days <= 1 ? 'Expire demain' : `Expire dans ${days} jours`;
}

export function daysRemaining(expiredAt: string): number {
  const diff = new Date(expiredAt).getTime() - Date.now();
  return Math.ceil(diff / (1000 * 60 * 60 * 24));
}

export function fileIconName(fileName: string): string {
  const ext = fileName.split('.').pop()?.toLowerCase() ?? '';
  if (['jpg', 'jpeg', 'png', 'gif', 'svg', 'bmp'].includes(ext)) return 'file-image';
  if (['mp3', 'wav', 'ogg', 'flac', 'aac', 'm4a'].includes(ext)) return 'file-music';
  if (['mp4', 'mov', 'avi', 'mkv'].includes(ext)) return 'file-play';
  return 'file';
}
