package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.constants.KeyStart;
import com.mydaytodo.sfa.asset.model.File;
import com.mydaytodo.sfa.asset.model.FileType;
import com.mydaytodo.sfa.asset.model.FileMetadataUploadRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.repository.FileRepositoryImpl;
import com.mydaytodo.sfa.asset.utilities.StringManipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class FileServiceImpl {
    @Autowired
    private FileRepositoryImpl fileRepository;

    public ServiceResponse saveDocumentMetadata(FileMetadataUploadRequest uploadRequest) {

        String key = KeyStart.DOCUMENT_KEY + System.currentTimeMillis();
        uploadRequest.setId(key);

        return ServiceResponse.builder()
                .data(null)
                .status(fileRepository.saveFileMetadata(uploadRequest))
                .build();
    }

    public ServiceResponse getDocument(String id) {
        File file = null;
        ServiceResponse response = new ServiceResponse();
        try {
            file = fileRepository.getDocument(id);
            if (file == null) {
                response.setData(null);
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return response;
            }
            return ServiceResponse.builder()
                    .data(file)
                    .status(HttpStatus.OK.value())
                    .build();
        } catch (Exception e) {
            return ServiceResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
        }
    }

    public ServiceResponse deleteAsset(String id) {
        return ServiceResponse.builder()
                .status(fileRepository.deleteDocument(id))
                .build();
    }

    public ServiceResponse updateFileMetadata(String id, FileMetadataUploadRequest uploadRequest) {
        return ServiceResponse
                .builder()
                .status(fileRepository.updateDocument(id, uploadRequest))
                .build();
    }

    public List<File> getUserDouments(String userId) {
        return fileRepository.getUserDocuments(userId);
    }

    public List<File> getFilesOfType(String type) {
        FileType fileType = FileType.fromTypeStr(type);
        return fileRepository.getDocumentsOfType(fileType);
    }

    public ServiceResponse validateFileType(String[] filenames) {
        log.info("About to validate filenames {}", Arrays.toString(filenames));
        for (String filename : filenames) {
            String ext = StringManipService.getExtension(filename).toLowerCase();
            log.info("ext {}", ext);
            if (!Arrays.stream(StringManipService.VALID_EXT).anyMatch(s -> s.contains(ext))) {
                String msg = String.format("Filename with %s, does not have a valid extension", filename);
                log.info("Incorrect filename supplied because of {}", msg);
                return ServiceResponse.builder()
                        .message(msg)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .data(null)
                        .build();
            }
        }
        log.info("successfully validated the filenames");
        return ServiceResponse.builder()
                        .status(null)
                        .data(null)
                        .message("Filenames valid")
                        .build();
    }
}
