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
package org.fujionclinical.fhir.api.r4.document;

import org.fujion.common.CollectionUtil;
import org.fujionclinical.api.model.core.AbstractWrapper;
import org.fujionclinical.api.model.core.IAttachment;
import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.document.IDocument;
import org.fujionclinical.api.model.encounter.IEncounter;
import org.fujionclinical.api.model.person.IPerson;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.r4.common.AttachmentTransform;
import org.fujionclinical.fhir.api.r4.common.BaseResourceWrapper;
import org.fujionclinical.fhir.api.r4.common.ConceptTransform;
import org.fujionclinical.fhir.api.r4.common.ReferenceWrapper;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceContentComponent;
import org.hl7.fhir.r4.model.DocumentReference.DocumentReferenceRelatesToComponent;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Identifier;

import java.util.Date;
import java.util.List;

public class DocumentWrapper extends BaseResourceWrapper<DocumentReference> implements IDocument {

    private static class ContentTransform implements IWrapperTransform<IAttachment, DocumentReferenceContentComponent> {

        @Override
        public IAttachment _wrap(DocumentReferenceContentComponent value) {
            return AttachmentTransform.getInstance().wrap(value.getAttachment());
        }

        @Override
        public DocumentReferenceContentComponent _unwrap(IAttachment value) {
            DocumentReferenceContentComponent content = newWrapped();
            content.setAttachment(AttachmentTransform.getInstance().unwrap(value));
            return content;
        }

        @Override
        public DocumentReferenceContentComponent newWrapped() {
            return new DocumentReferenceContentComponent();
        }

    }

    private static class RelatedTransform implements IWrapperTransform<IRelatedDocument, DocumentReferenceRelatesToComponent> {

        @Override
        public IRelatedDocument _wrap(DocumentReferenceRelatesToComponent value) {
            return new RelatedWrapper(value);
        }

        @Override
        public DocumentReferenceRelatesToComponent newWrapped() {
            return new DocumentReferenceRelatesToComponent();
        }

    }

    private static class RelatedWrapper extends AbstractWrapper<DocumentReferenceRelatesToComponent> implements IRelatedDocument {

        protected RelatedWrapper(DocumentReferenceRelatesToComponent wrapped) {
            super(wrapped);
        }

        @Override
        public DocumentRelationship getRelationship() {
            return FhirUtil.convertEnum(getWrapped().getCode(), DocumentRelationship.class);
        }

        @Override
        public void setRelationship(DocumentRelationship relationship) {
            getWrapped().setCode(FhirUtil.convertEnum(relationship, DocumentReference.DocumentRelationshipType.class));
        }

    }

    private static final ContentTransform contentTransform = new ContentTransform();

    private static final RelatedTransform relatedTransform = new RelatedTransform();

    private final List<IConcept> categories;

    private final List<IPerson> authors;

    private final List<IAttachment> attachments;

    private final List<IRelatedDocument> relatedDocuments;

    private final List<IEncounter> encounters;

    protected DocumentWrapper(DocumentReference resource) {
        super(resource);
        this.categories = ConceptTransform.getInstance().wrap(resource.getCategory());
        this.authors = ReferenceWrapper.wrap(resource.getAuthor());
        this.encounters = ReferenceWrapper.wrap(resource.getContext().getEncounter());
        this.attachments = contentTransform.wrap(resource.getContent());
        this.relatedDocuments = relatedTransform.wrap(resource.getRelatesTo());
    }

    @Override
    public Date getCreationDate() {
        return getWrapped().getDate();
    }

    @Override
    public void setCreationDate(Date creationDate) {
        getWrapped().setDate(creationDate);
    }

    @Override
    public DocumentStatus getDocumentStatus() {
        return FhirUtil.convertEnum(getWrapped().getStatus(), DocumentStatus.class);
    }

    @Override
    public void setDocumentStatus(DocumentStatus status) {
        getWrapped().setStatus(FhirUtil.convertEnum(status, Enumerations.DocumentReferenceStatus.class));
    }

    @Override
    public CompositionStatus getCompositionStatus() {
        return FhirUtil.convertEnum(getWrapped().getDocStatus(), CompositionStatus.class);
    }

    @Override
    public void setCompositionStatus(CompositionStatus status) {
        getWrapped().setDocStatus(FhirUtil.convertEnum(status, DocumentReference.ReferredDocumentStatus.class));
    }

    @Override
    public IConcept getType() {
        return ConceptTransform.getInstance().wrap(getWrapped().getType());
    }

    @Override
    public void setType(IConcept type) {
        getWrapped().setType(ConceptTransform.getInstance().unwrap(type));
    }

    @Override
    public List<IConcept> getCategories() {
        return categories;
    }

    @Override
    public List<IPerson> getAuthors() {
        return authors;
    }

    @Override
    public String getDescription() {
        return getWrapped().getDescription();
    }

    @Override
    public void setDescription(String description) {
        getWrapped().setDescription(description);
    }

    @Override
    public IEncounter getEncounter() {
        return CollectionUtil.getFirst(encounters);
    }

    @Override
    public void setEncounter(IEncounter encounter) {
        encounters.clear();
        encounters.add(encounter);
    }

    @Override
    public List<IRelatedDocument> getRelatedDocuments() {
        return relatedDocuments;
    }

    @Override
    protected List<Identifier> _getIdentifiers() {
        return getWrapped().getIdentifier();
    }

    @Override
    public List<IAttachment> getAttachments() {
        return attachments;
    }

}
