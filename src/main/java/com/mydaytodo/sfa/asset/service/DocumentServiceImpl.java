package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.constants.KeyStart;
import com.mydaytodo.sfa.asset.model.Document;
import com.mydaytodo.sfa.asset.model.DocumentType;
import com.mydaytodo.sfa.asset.model.DocumentMetadataUploadRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.repository.DocumentRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DocumentServiceImpl {
    @Autowired
    private DocumentRepositoryImpl documentRepository;

    public ServiceResponse saveDocumentMetadata(DocumentMetadataUploadRequest uploadRequest) {

        String key = KeyStart.DOCUMENT_KEY + System.currentTimeMillis();
        uploadRequest.setId(key);

        return ServiceResponse.builder()
                .data(null)
                .status(documentRepository.saveAsset(uploadRequest))
                .build();
    }

    public ServiceResponse getDocument(String id) {
        Document document = null;
        ServiceResponse response = new ServiceResponse();
        try {
            document = documentRepository.getDocument(id);
            if (document == null) {
                response.setData(null);
                response.setStatus(HttpStatus.NOT_FOUND.value());
                return response;
            }
            return ServiceResponse.builder()
                    .data(document)
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
                .status(documentRepository.deleteDocument(id))
                .build();
    }

    public ServiceResponse updateDocumentMetadata(String id, DocumentMetadataUploadRequest uploadRequest) {
        return ServiceResponse
                .builder()
                .status(documentRepository.updateDocument(id, uploadRequest))
                .build();
    }

    public List<Document> getUserDouments(String userId) {
        return documentRepository.getUserDocuments(userId);
    }

    public List<Document> getDocumentsOfType(String type) {
        DocumentType documentType = DocumentType.fromTypeStr(type);
        return documentRepository.getDocumentsOfType(documentType);
    }
}
