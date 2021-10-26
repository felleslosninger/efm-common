//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.11.25 at 12:23:12 PM CET 
//


package no.difi.meldingsutveksling.domain.sbdh;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.HashSet;
import java.util.Set;


/**
 * Java class for Manifest complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="Manifest">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="NumberOfItems" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         <element name="ManifestItem" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}ManifestItem" maxOccurs="unbounded"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Manifest", propOrder = {
        "numberOfItems",
        "manifestItem"
})
@Data
public class Manifest {

    @XmlElement(name = "NumberOfItems", required = true)
    protected Long numberOfItems;

    @XmlElement(name = "ManifestItem", required = true)
    @NotEmpty
    @Valid
    protected Set<ManifestItem> manifestItem;

    @JsonProperty
    public Long getNumberOfItems() {
        return numberOfItems != null ? numberOfItems : getManifestItem().size();
    }

    /**
     * Gets the value of the manifestItem property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the manifestItem property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getManifestItem().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManifestItem }
     *
     * @return Set of {@link ManifestItem}
     */
    public Set<ManifestItem> getManifestItem() {
        if (manifestItem == null) {
            manifestItem = new HashSet<>();
        }
        return this.manifestItem;
    }

    public Manifest addManifestItem(ManifestItem item) {
        getManifestItem().add(item);
        return this;
    }
}