package eionet.xmlconv.qa.services.saxon;

import eionet.xmlconv.qa.Properties;
import eionet.xmlconv.qa.exceptions.XMLConvException;
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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;

/**
 * Saxon XQuery Engine Implementation.
 * @author Unknown
 * @author George Sofianos
 */
public class SaxonImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaxonImpl.class);

    /**
     * Default Constructor
     * @throws XMLConvException If an error occurs.
     */
    public SaxonImpl() throws XMLConvException {
    }

    protected void runQuery(XQScript script, OutputStream result) throws XMLConvException {

        Processor proc = SaxonProcessor.getProcessor();
        XQueryCompiler comp = proc.newXQueryCompiler();

        String queriesPathURI = Utils.getURIfromPath(Properties.queriesFolder, true);
        comp.setBaseURI(URI.create(queriesPathURI));

        Reader queryReader = null;
        try {
            Serializer out = proc.newSerializer(result);
            out.setOutputProperty(Serializer.Property.INDENT, "no");
            out.setOutputProperty(Serializer.Property.ENCODING, DEFAULT_ENCODING);
            // if the output is html, then use method="xml" in output, otherwise, it's not valid xml
            if (getOutputType().equals(HTML_CONTENT_TYPE)) {
                out.setOutputProperty(Serializer.Property.METHOD, XML_CONTENT_TYPE);
            } else {
                out.setOutputProperty(Serializer.Property.METHOD, getOutputType());
            }
            // add xml declaration only, if the output should be XML
            if (getOutputType().equals(XML_CONTENT_TYPE)) {
                out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "no");
            } else {
                out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
            }
            if (!Utils.isNullStr(script.getScriptSource())) {
                queryReader = new StringReader(script.getScriptSource());
            } else if (!Utils.isNullStr(script.getScriptFileName())) {
                queryReader = new FileReader(script.getScriptFileName());
            } else {
                throw new XMLConvException("XQuery engine could not find script source or script file name!");
            }

            XQueryExecutable exp = comp.compile(queryReader);
            XQueryEvaluator ev = exp.load();
            ev.setExternalVariable(new QName("source_url"), new XdmAtomicValue(script.getSrcFileUrl()));
            //ev.setExternalVariable(new QName("base_url"), new XdmAtomicValue("http://" + Properties.appHost + Properties.contextPath));
            XdmValue val = ev.evaluate();
            proc.writeXdmValue(val, out);
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
