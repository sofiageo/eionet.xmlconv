package eionet.xmlconv.qa.services.saxon;

import eionet.xmlconv.qa.Properties;
import eionet.xmlconv.qa.exceptions.XMLConvException;
import eionet.xmlconv.qa.model.QAScript;
import eionet.xmlconv.qa.utils.Utils;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Saxon XQuery Engine Implementation.
 * @author George Sofianos
 */
public class SaxonImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaxonImpl.class);
    private static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
    private Processor processor;

    @Autowired
    public SaxonImpl(Processor processor) {
        this.processor = processor;
    }

    /**
     * Default Constructor
     * @throws XMLConvException If an error occurs.
     */
    public SaxonImpl() throws XMLConvException {
    }

    public String execute(QAScript script) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        execute(script, out);
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    private void execute(QAScript script, OutputStream result) throws XMLConvException {

        String outputType = script.getOutputType();
        String scriptSource = script.getScriptSource();
        String scriptFilename = script.getSrcFileUrl();
        String xmlFileUrl = script.getXmlFileUrl();

        /*Processor proc = SaxonProcessor.getProcessor();*/
        XQueryCompiler comp = processor.newXQueryCompiler();

        String queriesPathURI = Utils.getURIfromPath(Properties.QUERIES_DIR, true);
        comp.setBaseURI(URI.create(queriesPathURI));

        Reader queryReader = null;
        try {
            Serializer out = processor.newSerializer(result);
            out.setOutputProperty(Serializer.Property.INDENT, "no");
            out.setOutputProperty(Serializer.Property.ENCODING, DEFAULT_ENCODING);
            // if the output is html, then use method="xml" in output, otherwise, it's not valid xml
            if (outputType.equals("html")) {
                out.setOutputProperty(Serializer.Property.METHOD, "xml");
            } else {
                out.setOutputProperty(Serializer.Property.METHOD, outputType);
            }
            // add xml declaration only, if the output should be XML
            if (outputType.equals("xml")) {
                out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "no");
            } else {
                out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
            }
            if (!Utils.isNullStr(scriptSource)) {
                queryReader = new StringReader(scriptSource);
            } else if (!Utils.isNullStr(scriptFilename)) {
                queryReader = new FileReader(scriptFilename);
            } else {
                throw new XMLConvException("XQuery engine could not find script source or script file name!");
            }

            XQueryExecutable exp = comp.compile(queryReader);
            XQueryEvaluator ev = exp.load();
            ev.setExternalVariable(new QName("source_url"), new XdmAtomicValue(xmlFileUrl));
            //ev.setExternalVariable(new QName("base_url"), new XdmAtomicValue("http://" + Properties.appHost + Properties.contextPath));
            XdmValue val = ev.evaluate();
            processor.writeXdmValue(val, out);
        } catch (SaxonApiException e) {
            LOGGER.debug("Error in XQuery script: " + e.getMessage());
            throw new XMLConvException(e.getMessage(), e);
        } catch (FileNotFoundException e) {
            LOGGER.error("XQuery script file not found: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error("IO Error while reading script: " + e.getMessage());
        } finally {
            if (queryReader != null) {
                try {
                    queryReader.close();
                } catch (IOException e) {
                    LOGGER.error("Error while attempting to close reader: " + e.getMessage());
                }
            }
        }
    }
}
