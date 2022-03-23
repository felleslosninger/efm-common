//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.11.25 at 12:23:12 PM CET 
//


package no.difi.meldingsutveksling.domain.sbdh;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import no.difi.meldingsutveksling.validation.MessageType;
import no.difi.meldingsutveksling.validation.UUID;
import no.difi.meldingsutveksling.xml.OffsetDateTimeAdapter;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.OffsetDateTime;


/**
 * Java class for DocumentIdentification complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="DocumentIdentification">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Standard" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="TypeVersion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="InstanceIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="Type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="MultipleType" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="CreationDateAndTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentIdentification", propOrder = {
        "standard",
        "typeVersion",
        "instanceIdentifier",
        "type",
        "multipleType",
        "creationDateAndTime"
})
@Data
public class DocumentIdentification implements Serializable {

    @XmlElement(name = "Standard", required = true)
    @NotNull
    protected String standard;

    @XmlElement(name = "TypeVersion", required = true)
    @NotNull
    protected String typeVersion;

    @XmlElement(name = "InstanceIdentifier", required = true)
    @UUID
    protected String instanceIdentifier;

    @XmlElement(name = "Type", required = true)
    @NotNull
    @MessageType
    protected String type;

    @XmlElement(name = "MultipleType")
    protected Boolean multipleType;

    @XmlElement(name = "CreationDateAndTime", required = true)
    @XmlSchemaType(name = "dateTime")
    @XmlJavaTypeAdapter(OffsetDateTimeAdapter.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected OffsetDateTime creationDateAndTime;

}
