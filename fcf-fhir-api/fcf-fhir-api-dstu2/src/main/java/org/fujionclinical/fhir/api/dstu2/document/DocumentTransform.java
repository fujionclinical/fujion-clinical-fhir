/*
 * #%L
 * Fujion Clinical Framework
 * %%
 * Copyright (C) 2020 fujionclinical.org
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.fujionclinical.org/licensing/disclaimer
 *
 * #L%
 */
package org.fujionclinical.fhir.api.dstu2.document;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.DocumentReference;
import ca.uhn.fhir.model.dstu2.valueset.DocumentReferenceStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.DocumentRelationshipTypeEnum;
import org.fujion.common.CollectionUtil;
import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.document.Document;
import org.fujionclinical.api.model.document.IDocument;
import org.fujionclinical.api.model.document.RelatedDocument;
import org.fujionclinical.fhir.api.common.core.Constants;
import org.fujionclinical.fhir.api.common.transform.AbstractModelTransform;
import org.fujionclinical.fhir.api.dstu2.common.FhirUtilDstu2;
import org.fujionclinical.fhir.api.dstu2.transform.*;

import java.util.List;

public class DocumentTransform extends BaseResourceTransform<IDocument, DocumentReference> {

    private static class RelatedTransform extends AbstractModelTransform<IDocument.IRelatedDocument, DocumentReference.RelatesTo> {

        private RelatedTransform() {
            super(IDocument.IRelatedDocument.class, DocumentReference.RelatesTo.class);
        }

        @Override
        public DocumentReference.RelatesTo _fromLogicalModel(IDocument.IRelatedDocument src) {
            DocumentReference.RelatesTo dest = new DocumentReference.RelatesTo();
            dest.setTarget(ReferenceTransform.getInstance().fromLogicalModel(src.getDocument()));
            dest.setCode(CoreUtil.enumToEnum(src.getRelationship(), DocumentRelationshipTypeEnum.class));
            return dest;
        }

        @Override
        public IDocument.IRelatedDocument _toLogicalModel(DocumentReference.RelatesTo src) {
            IDocument.IRelatedDocument dest = new RelatedDocument();
            dest.setDocument(ReferenceTransform.getInstance().toLogicalModel(src.getTarget()));
            dest.setRelationship(CoreUtil.stringToEnum(src.getCode(), IDocument.DocumentRelationship.class));
            return dest;
        }

    }

    private static final RelatedTransform relatedTransform = new RelatedTransform();

    private static final DocumentTransform instance = new DocumentTransform();

    public static DocumentTransform getInstance() {
        return instance;
    }

    private DocumentTransform() {
        super(IDocument.class, DocumentReference.class);
    }

    @Override
    protected IDocument newLogical() {
        return new Document();
    }

    @Override
    protected DocumentReference newNative() {
        return new DocumentReference();
    }

    @Override
    public DocumentReference _fromLogicalModel(IDocument src) {
        DocumentReference dest = super._fromLogicalModel(src);
        dest.setDescription(src.getDescription());
        dest.setCreated(DateTimeTransform.getInstance().fromLogicalModel(src.getCreationDate()));
        dest.setStatus(CoreUtil.enumToEnum(src.getDocumentStatus(), DocumentReferenceStatusEnum.class));
        IDocument.CompositionStatus status = src.getCompositionStatus();
        CodeableConceptDt value = status == null ? null : FhirUtilDstu2.createCodeableConcept(Constants.DOCUMENT_COMPOSITION_STATUS_SYSTEM, status.name().toLowerCase(), status.name());
        dest.setDocStatus(value);
        dest.setType(ConceptTransform.getInstance().fromLogicalModel(src.getType()));
        dest.setClassElement(ConceptTransform.getInstance().fromLogicalModel(CollectionUtil.getFirst(src.getCategories())));
        dest.setAuthor(ReferenceTransform.getInstance().fromLogicalModel(src.getAuthors()));
        dest.getContext().setEncounter(ReferenceTransform.getInstance().fromLogicalModel(src.getEncounter()));
        src.getAttachments().forEach(attachment ->
                dest.addContent().setAttachment(AttachmentTransform.getInstance().fromLogicalModel(attachment)));
        dest.setRelatesTo(relatedTransform.fromLogicalModel(src.getRelatedDocuments()));
        return dest;
    }

    @Override
    public IDocument _toLogicalModel(DocumentReference src) {
        IDocument dest = super._toLogicalModel(src);
        dest.setDescription(src.getDescription());
        dest.setCreationDate(DateTimeTransform.getInstance().toLogicalModel(src.getCreatedElement()));
        dest.setDocumentStatus(CoreUtil.stringToEnum(src.getStatus(), IDocument.DocumentStatus.class));
        CodeableConceptDt status = src.getDocStatus();
        dest.setDocumentStatus(FhirUtilDstu2.convertConceptToEnum(ConceptTransform.getInstance().toLogicalModel(status), IDocument.DocumentStatus.class));
        dest.setType(ConceptTransform.getInstance().toLogicalModel(src.getType()));
        dest.addCategories(ConceptTransform.getInstance().toLogicalModel(src.getClassElement()));
        dest.setAuthors(ReferenceTransform.getInstance().toLogicalModel(src.getAuthor()));
        dest.setEncounter(ReferenceTransform.getInstance().toLogicalModel(src.getContext().getEncounter()));
        src.getContent().forEach(content ->
                dest.addAttachments(AttachmentTransform.getInstance().toLogicalModel(content.getAttachment())));
        dest.setRelatedDocuments(relatedTransform.toLogicalModel(src.getRelatesTo()));
        return dest;
    }

    @Override
    protected List<IdentifierDt> getIdentifiers(DocumentReference documentReference) {
        return documentReference.getIdentifier();
    }

}
