import http from "./http-common";

class UploadFilesService {

    newUpload() {
        return http.post("/storage/new");
    }

    upload(file,id, onUploadProgress) {
        let formData = new FormData();

        formData.append("file", file);
        formData.append("id", id);

        return http.post("/storage", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
            onUploadProgress,
        });
    }

    getFiles(id) {
        return http.get(`/storage/file/${id}`);
    }

    download(id) {
        return http.get(`/storage/download/${id}`, {
            responseType: 'blob' // Define o tipo de resposta como 'blob' para arquivos binários
        });
    }
    

}

export default new UploadFilesService();