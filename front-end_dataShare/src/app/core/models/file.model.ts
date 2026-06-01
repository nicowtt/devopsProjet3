export interface FileDTO {
  uuid?: string,
  name: string,
  size?: number,
  dayBeforeExpiration: number,
  password?: string,
  expiredAt?: string
}
