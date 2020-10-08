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
package org.fujionclinical.fhir.api.r5.document;

import org.fujion.common.CollectionUtil;
import org.fujionclinical.api.core.CoreUtil;
import org.fujionclinical.api.model.document.Document;
import org.fujionclinical.api.model.document.IDocument;
import org.fujionclinical.api.model.document.RelatedDocument;
import org.fujionclinical.fhir.api.common.transform.AbstractModelTransform;
import org.fujionclinical.fhir.api.r5.transform.*;
import org.hl7.fhir.r5.model.DocumentReference;
import org.hl7.fhir.r5.model.DocumentReference.DocumentReferenceRelatesToComponent;
import org.hl7.fhir.r5.model.Enumerations;
import org.hl7.fhir.r5.model.Identifier;

import java.util.List;

public class DocumentTransform extends BaseResourceTransform<IDocument, DocumentReference> {

    private static class RelatedTransform extends AbstractModelTransform<IDocument.IRelatedDocument, DocumentReferenceRelatesToComponent> {

        private RelatedTransform() {
            super(IDocument.IRelatedDocument.class, DocumentReferenceRelatesToComponent.class);
        }

        @Override
        public DocumentReferenceRelatesToComponent _fromLogicalModel(IDocument.IRelatedDocument src) {
            DocumentReferenceRelatesToComponent dest = new DocumentReferenceRelatesToComponent();
            dest.setTarget(ReferenceTransform.getInstance().fromLogicalModel(src.getDocument()));
            dest.setCode(CoreUtil.enumToEnum(src.getRelationship(), Enumerations.DocumentRelationshipType.class));
            return dest;
        }

        @Override
        public IDocument.IRelatedDocument _toLogicalModel(DocumentReferenceRelatesToComponent src) {
            IDocument.IRelatedDocument dest = new RelatedDocument();
            dest.setDocument(ReferenceTransform.getInstance().toLogicalModel(src.getTarget()));
            dest.setRelationship(CoreUtil.enumToEnum(src.getCode(), IDocument.DocumentRelationship.class));
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
        dest.setDate(DateTransform.getInstance().fromLogicalModel(src.getCreationDate()));
        dest.setStatus(CoreUtil.enumToEnum(src.getDocumentStatus(), Enumerations.DocumentReferenceStatus.class));
        IDocument.CompositionStatus status = src.getCompositionStatus();
        dest.setDocStatus(CoreUtil.enumToEnum(status, Enumerations.CompositionStatus.class));
        dest.setType(ConceptTransform.getInstance().fromLogicalModel(src.getType()));
        dest.setCategory(ConceptTransform.getInstance().fromLogicalModelAsList(src.getCategories()));
        dest.setAuthor(ReferenceTransform.getInstance().fromLogicalModelAsList(src.getAuthors()));
        dest.getContext().addEncounter(ReferenceTransform.getInstance().fromLogicalModel(src.getEncounter()));
        src.getAttachments().forEach(attachment ->
                dest.addContent().setAttachment(AttachmentTransform.getInstance().fromLogicalModel(attachment)));
        dest.setRelatesTo(relatedTransform.fromLogicalModelAsList(src.getRelatedDocuments()));
        return dest;
    }

    @Override
    public IDocument _toLogicalModel(DocumentReference src) {
        IDocument dest = super._toLogicalModel(src);
        dest.setDescription(src.getDescription());
        dest.setCreationDate(DateTransform.getInstance().toLogicalModel(src.getDate()));
        dest.setDocumentStatus(CoreUtil.enumToEnum(src.getStatus(), IDocument.DocumentStatus.class));
        dest.setDocumentStatus(CoreUtil.enumToEnum(src.getDocStatus(), IDocument.DocumentStatus.class));
        dest.setType(ConceptTransform.getInstance().toLogicalModel(src.getType()));
        dest.setCategories(ConceptTransform.getInstance().toLogicalModelAsList(src.getCategory()));
        dest.setAuthors(ReferenceTransform.getInstance().toLogicalModelAsList(src.getAuthor()));
        dest.setEncounter(ReferenceTransform.getInstance().toLogicalModel(CollectionUtil.getFirst(src.getContext().getEncounter())));
        src.getContent().forEach(content ->
                dest.addAttachments(AttachmentTransform.getInstance().toLogicalModel(content.getAttachment())));
        dest.setRelatedDocuments(relatedTransform.toLogicalModelAsList(src.getRelatesTo()));
        return dest;
    }

    @Override
    protected List<Identifier> getIdentifiers(DocumentReference documentReference) {
        return documentReference.getIdentifier();
    }

}
