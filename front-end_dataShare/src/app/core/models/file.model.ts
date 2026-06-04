export interface FileRequestDTO {
  dayBeforeExpiration: number,
  password?: string,
}
export interface FileResponseDTO {
  uuid?: string,
  name: string,
  size?: number,
  createdAt: string,
  expiredAt: string,
  hasPassword?: boolean,
}
