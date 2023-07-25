//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.07.25 at 12:51:27 PM IST 
//


package com.emedlogix.drug;

import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Table cell (TD) definition supporting new/old tags.
 * 
 * <p>Java class for cellType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cellType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{}contentType"&gt;
 *       &lt;attribute name="col" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cellType")
public class CellType
    extends ContentType
{

    @XmlAttribute(name = "col", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger col;

    /**
     * Gets the value of the col property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCol() {
        return col;
    }

    /**
     * Sets the value of the col property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCol(BigInteger value) {
        this.col = value;
    }

}
