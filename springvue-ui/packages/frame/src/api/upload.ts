import { uploadByPieces } from "common";

export function upload(data: unknown, file: File, progress: unknown, size = 5) {
  return uploadByPieces(
    window.config.api_url + "/uploadByPieces",
    data,
    file,
    progress,
    size
  );
}
