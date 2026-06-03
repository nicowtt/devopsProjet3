export interface FileRequestDTO {
  dayBeforeExpiration: number,
  password?: string,
}
export interface FileResponseDTO {
  uuid?: string,
  name: string,
  size?: number,
  expiredAt: string,
  hasPassword?: boolean,
}
