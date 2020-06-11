package org.fujionclinical.fhir.api.dstu2.document;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.DocumentReference;
import ca.uhn.fhir.model.dstu2.valueset.DocumentReferenceStatusEnum;
import ca.uhn.fhir.model.dstu2.valueset.DocumentRelationshipTypeEnum;
import org.fujionclinical.api.model.core.AbstractWrapper;
import org.fujionclinical.api.model.core.IAttachment;
import org.fujionclinical.api.model.core.IConcept;
import org.fujionclinical.api.model.core.IWrapperTransform;
import org.fujionclinical.api.model.document.IDocument;
import org.fujionclinical.api.model.encounter.IEncounter;
import org.fujionclinical.api.model.person.IPerson;
import org.fujionclinical.fhir.api.common.core.Constants;
import org.fujionclinical.fhir.api.common.core.FhirUtil;
import org.fujionclinical.fhir.api.dstu2.common.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DocumentWrapper extends BaseResourceWrapper<DocumentReference> implements IDocument {

    private static class ContentTransform implements IWrapperTransform<IAttachment, DocumentReference.Content> {

        @Override
        public IAttachment _wrap(DocumentReference.Content value) {
            return AttachmentTransform.getInstance().wrap(value.getAttachment());
        }

        @Override
        public DocumentReference.Content _unwrap(IAttachment value) {
            DocumentReference.Content content = newWrapped();
            content.setAttachment(AttachmentTransform.getInstance().unwrap(value));
            return content;
        }

        @Override
        public DocumentReference.Content newWrapped() {
            return new DocumentReference.Content();
        }

    }

    private static class RelatedTransform implements IWrapperTransform<IRelatedDocument, DocumentReference.RelatesTo> {

        @Override
        public IRelatedDocument _wrap(DocumentReference.RelatesTo value) {
            return new RelatedWrapper(value);
        }

        @Override
        public DocumentReference.RelatesTo newWrapped() {
            return new DocumentReference.RelatesTo();
        }

    }

    private static class RelatedWrapper extends AbstractWrapper<DocumentReference.RelatesTo> implements IRelatedDocument {

        protected RelatedWrapper(DocumentReference.RelatesTo wrapped) {
            super(wrapped);
        }

        @Override
        public DocumentRelationship getRelationship() {
            return FhirUtil.convertEnum(getWrapped().getCodeElement().getValueAsEnum(), DocumentRelationship.class);
        }

        @Override
        public void setRelationship(DocumentRelationship relationship) {
            getWrapped().setCode(FhirUtil.convertEnum(relationship, DocumentRelationshipTypeEnum.class));
        }

    }

    private static final ContentTransform contentTransform = new ContentTransform();

    private static final RelatedTransform relatedTransform = new RelatedTransform();

    private final List<IConcept> categories;

    private final List<IPerson> authors;

    private final List<IAttachment> attachments;

    private final List<IRelatedDocument> relatedDocuments;

    private final ReferenceWrapper<IEncounter> encounterRef;

    protected DocumentWrapper(DocumentReference resource) {
        super(resource);
        this.categories = ConceptTransform.getInstance().wrap(Collections.singletonList(resource.getClassElement()));
        this.authors = ReferenceWrapper.wrap(resource.getAuthor());
        this.encounterRef = ReferenceWrapper.wrap(resource.getContext().getEncounter());
        this.attachments = contentTransform.wrap(resource.getContent());
        this.relatedDocuments = relatedTransform.wrap(resource.getRelatesTo());
    }

    @Override
    public Date getCreationDate() {
        return getWrapped().getCreated();
    }

    @Override
    public void setCreationDate(Date creationDate) {
        getWrapped().setCreated(creationDate, TemporalPrecisionEnum.SECOND);
    }

    @Override
    public DocumentStatus getDocumentStatus() {
        return FhirUtil.convertEnum(getWrapped().getStatusElement().getValueAsEnum(), DocumentStatus.class);
    }

    @Override
    public void setDocumentStatus(DocumentStatus status) {
        getWrapped().setStatus(FhirUtil.convertEnum(status, DocumentReferenceStatusEnum.class));
    }

    @Override
    public CompositionStatus getCompositionStatus() {
        List<CodingDt> status = getWrapped().getDocStatus().getCoding();
        return FhirUtil.convertEnum(status.isEmpty() ? null : status.get(0).getCode(), CompositionStatus.class);
    }

    @Override
    public void setCompositionStatus(CompositionStatus status) {
        CodeableConceptDt value = status == null ? null : FhirUtilDstu2.createCodeableConcept(Constants.DOCUMENT_COMPOSITION_STATUS_SYSTEM, status.name().toLowerCase(), status.name());
        getWrapped().setDocStatus(value);
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
        return encounterRef.getWrapped();
    }

    @Override
    public void setEncounter(IEncounter encounter) {
        encounterRef.setWrapped(encounter);
    }

    @Override
    public List<IRelatedDocument> getRelatedDocuments() {
        return relatedDocuments;
    }

    @Override
    protected List<IdentifierDt> _getIdentifiers() {
        return getWrapped().getIdentifier();
    }

    @Override
    public List<IAttachment> getAttachments() {
        return attachments;
    }

}
