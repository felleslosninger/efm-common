//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.11.25 at 12:23:12 PM CET 
//


package no.difi.meldingsutveksling.domain.sbdh;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import no.difi.meldingsutveksling.domain.PartnerIdentifier;
import no.difi.meldingsutveksling.validation.group.ValidationGroups;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.ConvertGroup;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


/**
 * Java class for StandardBusinessDocumentHeader complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="StandardBusinessDocumentHeader">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="HeaderVersion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="Sender" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}Partner" maxOccurs="unbounded"/>
 *         <element name="Receiver" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}Partner" maxOccurs="unbounded"/>
 *         <element name="DocumentIdentification" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}DocumentIdentification"/>
 *         <element name="Manifest" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}Manifest" minOccurs="0"/>
 *         <element name="BusinessScope" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}BusinessScope" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StandardBusinessDocumentHeader", propOrder = {
        "headerVersion",
        "sender",
        "receiver",
        "documentIdentification",
        "manifest",
        "businessScope"
})
@Data
@SuppressWarnings("unused")
public class StandardBusinessDocumentHeader {

    @XmlElement(name = "HeaderVersion", required = true)
    @NotNull
    private String headerVersion;

    @XmlElement(name = "Sender", required = true)
    @Size(max = 1)
    @Valid
    @ConvertGroup(to = ValidationGroups.Partner.Sender.class)
    private Set<@Valid Partner> sender;

    @XmlElement(name = "Receiver", required = true)
    @Size(max = 1)
    @Valid
    @ConvertGroup(to = ValidationGroups.Partner.Receiver.class)
    private Set<@Valid Partner> receiver;

    @XmlElement(name = "DocumentIdentification", required = true)
    @NotNull
    @Valid
    private DocumentIdentification documentIdentification;

    @XmlElement(name = "Manifest")
    @Valid
    private Manifest manifest;

    @XmlElement(name = "BusinessScope")
    @NotNull
    @Valid
    private BusinessScope businessScope;

    public Set<Partner> getSender() {
        if (sender == null) {
            sender = new HashSet<>();
        }
        return this.sender;
    }

    public StandardBusinessDocumentHeader addSender(Partner partner) {
        getSender().add(partner);
        return this;
    }

    public Set<Partner> getReceiver() {
        if (receiver == null) {
            receiver = new HashSet<>();
        }
        return this.receiver;
    }

    public StandardBusinessDocumentHeader addReceiver(Partner partner) {
        getReceiver().add(partner);
        return this;
    }

    @JsonIgnore
    public String getMessageId() {
        return getDocumentIdentification().getInstanceIdentifier();
    }

    @JsonIgnore
    public String getConversationId() {
        return getScope(ScopeType.CONVERSATION_ID)
                .map(Scope::getInstanceIdentifier)
                .orElse(null);
    }


    @JsonIgnore
    public String getDocumentType() {
        return getDocumentIdentification().getStandard();
    }

    @JsonIgnore
    public Set<Scope> getScopes() {
        return Optional.ofNullable(getBusinessScope())
                .flatMap(p -> Optional.ofNullable(p.getScope()))
                .orElseGet(Collections::emptySet);
    }

    @JsonIgnore
    public Optional<Scope> getScope(ScopeType scopeType) {
        return getScopes()
                .stream()
                .filter(scope -> scopeType.toString().equals(scope.getType()) || scopeType.name().equals(scope.getType()))
                .findAny();
    }

    @JsonIgnore
    public void addScope(Scope scope) {
        Optional.ofNullable(getBusinessScope()).ifPresent(p -> p.addScopes(scope));
    }

    @JsonIgnore
    public Optional<String> getType() {
        return Optional.ofNullable(getDocumentIdentification())
                .flatMap(p -> Optional.ofNullable(p.getType()));
    }

    @JsonIgnore
    public Optional<OffsetDateTime> getExpectedResponseDateTime() {
        return getScope(ScopeType.CONVERSATION_ID)
                .flatMap(p -> Optional.ofNullable(p.getScopeInformation()))
                .flatMap(p -> p.stream().findFirst())
                .flatMap(p -> Optional.ofNullable(p.getExpectedResponseDateTime()));
    }

    @JsonIgnore
    public PartnerIdentifier getSenderIdentifier() {
        return getFirstSender()
                .flatMap(p -> Optional.ofNullable(p.getIdentifier()))
                .flatMap(p -> Optional.ofNullable(p.getValue()))
                .map(PartnerIdentifier::parse)
                .orElse(null);
    }

    @JsonIgnore
    public StandardBusinessDocumentHeader setSenderIdentifier(PartnerIdentifier identifier) {
        getSender().clear();
        addSender(new Partner()
                .setIdentifier(new PartnerIdentification()
                        .setAuthority(identifier.getAuthority())
                        .setValue(identifier.getIdentifier())
                )
        );

        return this;
    }

    @JsonIgnore
    public PartnerIdentifier getReceiverIdentifier() {
        return getFirstReceiver()
                .flatMap(p -> Optional.ofNullable(p.getIdentifier()))
                .flatMap(p -> Optional.ofNullable(p.getValue()))
                .map(PartnerIdentifier::parse)
                .orElse(null);
    }

    @JsonIgnore
    public StandardBusinessDocumentHeader setReceiverIdentifier(PartnerIdentifier identifier) {
        getReceiver().clear();
        addReceiver(new Partner()
                .setIdentifier(new PartnerIdentification()
                        .setAuthority(identifier.getAuthority())
                        .setValue(identifier.getIdentifier())
                )
        );

        return this;
    }

    private Optional<Partner> getFirstSender() {
        if (sender == null) {
            return Optional.empty();
        }
        return sender.stream().findFirst();
    }

    private Optional<Partner> getFirstReceiver() {
        if (receiver == null) {
            return Optional.empty();
        }
        return receiver.stream().findFirst();
    }
}
