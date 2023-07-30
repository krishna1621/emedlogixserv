//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.07.30 at 11:12:46 PM IST 
//


package com.emedlogix.drug;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.emedlogix.drug package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Version_QNAME = new QName("", "version");
    private final static QName _Title_QNAME = new QName("", "title");
    private final static QName _Manif_QNAME = new QName("", "manif");
    private final static QName _See_QNAME = new QName("", "see");
    private final static QName _SeeAlso_QNAME = new QName("", "seeAlso");
    private final static QName _Head_QNAME = new QName("", "head");
    private final static QName _IndexHeading_QNAME = new QName("", "indexHeading");
    private final static QName _Cell_QNAME = new QName("", "cell");
    private final static QName _Term_QNAME = new QName("", "term");
    private final static QName _MainTerm_QNAME = new QName("", "mainTerm");
    private final static QName _Letter_QNAME = new QName("", "letter");
    private final static QName _ContentTypeNew_QNAME = new QName("", "new");
    private final static QName _ContentTypeOld_QNAME = new QName("", "old");
    private final static QName _ContentTypeNemod_QNAME = new QName("", "nemod");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.emedlogix.drug
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ContentType }
     * 
     */
    public ContentType createContentType() {
        return new ContentType();
    }

    /**
     * Create an instance of {@link HeadType }
     * 
     */
    public HeadType createHeadType() {
        return new HeadType();
    }

    /**
     * Create an instance of {@link IndexHeadingType }
     * 
     */
    public IndexHeadingType createIndexHeadingType() {
        return new IndexHeadingType();
    }

    /**
     * Create an instance of {@link CellType }
     * 
     */
    public CellType createCellType() {
        return new CellType();
    }

    /**
     * Create an instance of {@link TermType }
     * 
     */
    public TermType createTermType() {
        return new TermType();
    }

    /**
     * Create an instance of {@link MainTermType }
     * 
     */
    public MainTermType createMainTermType() {
        return new MainTermType();
    }

    /**
     * Create an instance of {@link LetterType }
     * 
     */
    public LetterType createLetterType() {
        return new LetterType();
    }

    /**
     * Create an instance of {@link ICD10CMIndex }
     * 
     */
    public ICD10CMIndex createICD10CMIndex() {
        return new ICD10CMIndex();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContentType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ContentType }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "version")
    public JAXBElement<ContentType> createVersion(ContentType value) {
        return new JAXBElement<ContentType>(_Version_QNAME, ContentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContentType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ContentType }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "title")
    public JAXBElement<ContentType> createTitle(ContentType value) {
        return new JAXBElement<ContentType>(_Title_QNAME, ContentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContentType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ContentType }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "manif")
    public JAXBElement<ContentType> createManif(ContentType value) {
        return new JAXBElement<ContentType>(_Manif_QNAME, ContentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContentType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ContentType }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "see")
    public JAXBElement<ContentType> createSee(ContentType value) {
        return new JAXBElement<ContentType>(_See_QNAME, ContentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContentType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ContentType }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "seeAlso")
    public JAXBElement<ContentType> createSeeAlso(ContentType value) {
        return new JAXBElement<ContentType>(_SeeAlso_QNAME, ContentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HeadType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link HeadType }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "head")
    public JAXBElement<HeadType> createHead(HeadType value) {
        return new JAXBElement<HeadType>(_Head_QNAME, HeadType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IndexHeadingType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link IndexHeadingType }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "indexHeading")
    public JAXBElement<IndexHeadingType> createIndexHeading(IndexHeadingType value) {
        return new JAXBElement<IndexHeadingType>(_IndexHeading_QNAME, IndexHeadingType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CellType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CellType }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "cell")
    public JAXBElement<CellType> createCell(CellType value) {
        return new JAXBElement<CellType>(_Cell_QNAME, CellType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TermType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TermType }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "term")
    public JAXBElement<TermType> createTerm(TermType value) {
        return new JAXBElement<TermType>(_Term_QNAME, TermType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MainTermType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link MainTermType }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "mainTerm")
    public JAXBElement<MainTermType> createMainTerm(MainTermType value) {
        return new JAXBElement<MainTermType>(_MainTerm_QNAME, MainTermType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LetterType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LetterType }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "letter")
    public JAXBElement<LetterType> createLetter(LetterType value) {
        return new JAXBElement<LetterType>(_Letter_QNAME, LetterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "new", scope = ContentType.class)
    public JAXBElement<String> createContentTypeNew(String value) {
        return new JAXBElement<String>(_ContentTypeNew_QNAME, String.class, ContentType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "old", scope = ContentType.class)
    public JAXBElement<String> createContentTypeOld(String value) {
        return new JAXBElement<String>(_ContentTypeOld_QNAME, String.class, ContentType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link String }{@code >}
     */
    @XmlElementDecl(namespace = "", name = "nemod", scope = ContentType.class)
    public JAXBElement<String> createContentTypeNemod(String value) {
        return new JAXBElement<String>(_ContentTypeNemod_QNAME, String.class, ContentType.class, value);
    }

}
