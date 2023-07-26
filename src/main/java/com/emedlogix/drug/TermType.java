//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.07.26 at 03:37:25 PM IST 
//


package com.emedlogix.drug;

import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Sub-class mainTerm and add the level attribute to it.
 * 
 * <p>Java class for termType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="termType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{}mainTermType"&gt;
 *       &lt;attribute name="level" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "termType")
public class TermType
    extends MainTermType
{

    @XmlAttribute(name = "level", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger level;

    /**
     * Gets the value of the level property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLevel() {
        return level;
    }

    /**
     * Sets the value of the level property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLevel(BigInteger value) {
        this.level = value;
    }

}
