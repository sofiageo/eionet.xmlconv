package eionet.xmlconv.qa.services.validation;

import eionet.xmlconv.qa.Properties;
import eionet.xmlconv.qa.model.SchemaDto;
import eionet.xmlconv.qa.exceptions.DCMException;
import eionet.xmlconv.qa.exceptions.XMLConvException;
import eionet.xmlconv.qa.http.HttpFileManager;
import eionet.xmlconv.qa.services.QAFeedbackType;
import eionet.xmlconv.qa.model.ValidateDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.util.XMLCatalogResolver;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 *
 */
@Service
public class JaxpValidationService implements ValidationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxpValidationService.class);
    private ValidatorErrorHandler errorHandler = new ValidatorErrorHandler();
    private ValidationServiceFeedback validationFeedback = new ValidationServiceFeedback();
    private InputAnalyser inputAnalyser = new InputAnalyser();
    private String originalSchema;
    private String validatedSchema;
    private String validatedSchemaURL;
    private String warningMessage;

    @Override
    public String getOriginalSchema() {
        return this.originalSchema;
    }

    @Override
    public String getValidatedSchema() {
        return this.validatedSchema;
    }

    @Override
    public String getValidatedSchemaURL() {
        return this.validatedSchemaURL;
    }

    @Override
    public String getWarningMessage() {
        return warningMessage;
    }

    @Override
    public List<ValidateDto> getErrorList() {
        return errorHandler.getErrors();
    }

    @Override
    public String validate(String xml) throws DCMException {
        return validateSchema(xml, null);
    }

    @Override
    public String validateSchema(String xml, String schema) throws DCMException {
        HttpFileManager fileManager = new HttpFileManager();
        InputStream is = null;
        try {
            is = fileManager.getFileInputStream(xml, null, true);
            return validateSchema(xml, is, schema);
        } catch (MalformedURLException mfe) {
            throw new DCMException(DCMException.EXCEPTION_CONVERT_URL_MALFORMED);
        } catch (IOException ioe) {
            throw new DCMException(DCMException.EXCEPTION_CONVERT_URL_ERROR);
        } catch (Exception e) {
            throw new DCMException(DCMException.EXCEPTION_GENERAL);
        } finally {
            fileManager.closeQuietly();
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

    @Override
    public String validateSchema(String sourceUrl, InputStream srcStream, String schemaUrl) throws DCMException, XMLConvException {

        String localSchemaFilename = null;
        String resultXML = "";
        boolean isDTD = false;
        boolean isBlocker = false;
        String namespace = "";


        if (StringUtils.isEmpty(schemaUrl)) {
            inputAnalyser.parseXML(sourceUrl);
            schemaUrl = inputAnalyser.getSchemaOrDTD();
            namespace = inputAnalyser.getNamespace();
        } else {
            isDTD = schemaUrl.endsWith("dtd");
        }

        if (StringUtils.isEmpty(schemaUrl)) {
            return validationFeedback.formatFeedbackText("Could not validate XML file. Unable to locate XML Schema reference.", QAFeedbackType.BLOCKER, true);
        }
        originalSchema = schemaUrl;
        validatedSchema = schemaUrl;
        validationFeedback.setSchema(originalSchema);

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        CustomCatalogResolver resolver = new CustomCatalogResolver();
        String[] catalogs = {Properties.CATALOG_PATH};
        resolver.setPreferPublic(true);
        resolver.setCatalogList(catalogs);
        sf.setResourceResolver(resolver);

        // TODO pass local schema filename from main xmlconv
        /*String schemaFileName = schemaManager.getUplSchemaURL(schemaUrl);*/
        if (!StringUtils.equals(schemaUrl, localSchemaFilename)) {
            //XXX: replace file://
            validatedSchema = "file:///".concat(Properties.SCHEMA_DIR).concat("/").concat(localSchemaFilename);
            validatedSchemaURL = Properties.XMLCONV_URL.concat("/schema/").concat(localSchemaFilename);
        }

        try {
            Schema schema = sf.newSchema(new URL(validatedSchema));
            Validator validator = schema.newValidator();
            validator.setErrorHandler(errorHandler);

            // make parser to validate
            validator.setFeature("http://xml.org/sax/features/validation", true);
            validator.setFeature("http://apache.org/xml/features/validation/schema", true);
            validator.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
            validator.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
            validator.validate(new StreamSource(srcStream));

            // TODO post processing in main xmlconv
/*            SchemaDto schemaObj = schemaManager.getSchema(schemaUrl);
            if (schemaObj != null) {
                isBlocker = schemaObj.isBlocker();
            }*/
            LOGGER.info("Validation completed");
            validationFeedback.setValidationErrors(errorHandler.getErrors());
            resultXML = validationFeedback.formatFeedbackText(isBlocker);
            /*resultXML = postProcessor.processQAResult(resultXML, schemaUrl);
            warningMessage = postProcessor.getWarningMessage(schemaUrl);*/

        } catch (SAXException e) {
            LOGGER.error("Error: ", e);
            return validationFeedback.formatFeedbackText("Document is not well-formed: " + e.getMessage(), QAFeedbackType.BLOCKER, true);
        } catch (MalformedURLException e) {
            LOGGER.error("Error: ", e);
            return validationFeedback.formatFeedbackText("The parser could not check the document. " + e.getMessage(), QAFeedbackType.BLOCKER, true);
        } catch (IOException e) {
            LOGGER.error("Error: ", e);
            return validationFeedback.formatFeedbackText("The parser could not check the document. " + e.getMessage(), QAFeedbackType.BLOCKER, true);
        }

        return resultXML;
    }

    public class CustomCatalogResolver extends XMLCatalogResolver {
        @Override
        public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier) throws IOException {
            LOGGER.info("Validation Service resolves entity with publicId=" + resourceIdentifier.getPublicId() + " ; systemId=" + resourceIdentifier.getBaseSystemId());
            return super.resolveEntity(resourceIdentifier);
        }
    }

}
