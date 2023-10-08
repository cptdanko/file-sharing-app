package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.constants.KeyStart;
import com.mydaytodo.sfa.asset.model.Document;
import com.mydaytodo.sfa.asset.model.DocumentUploadRequest;
import com.mydaytodo.sfa.asset.repository.DocumentRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DocumentServiceImpl {
    @Autowired
    private DocumentRepositoryImpl documentRepository;

    public HttpStatus saveDocumentMetadata(DocumentUploadRequest uploadRequest) {
        String key = KeyStart.DOCUMENT_KEY + System.currentTimeMillis();
        uploadRequest.setId(key);
        return HttpStatus.valueOf(documentRepository.saveAsset(uploadRequest));
    }
    public ResponseEntity<HttpStatus> getDocument(String id) {
        Document document = null;
        try {
            document = documentRepository.getDocument(id);
            if(document == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity(document, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.valueOf(e.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public HttpStatus deleteAsset(String id) {
        return HttpStatus.valueOf(documentRepository.deleteDocument(id));
    }
    public HttpStatus updateDocumentMetadata(String id, DocumentUploadRequest uploadRequest) {
        return HttpStatus.valueOf(documentRepository.updateDocument(id, uploadRequest));
    }

}
