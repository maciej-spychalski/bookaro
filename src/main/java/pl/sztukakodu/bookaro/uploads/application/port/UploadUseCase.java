package pl.sztukakodu.bookaro.uploads.application.port;

import lombok.AllArgsConstructor;
import lombok.Value;
import pl.sztukakodu.bookaro.uploads.domain.Upload;

public interface UploadUseCase {

    Upload save(SaveUploadCommand command);

    @Value
    @AllArgsConstructor
    class SaveUploadCommand {
        String filename;
        byte[] file;
        String contentType;
    }
}
