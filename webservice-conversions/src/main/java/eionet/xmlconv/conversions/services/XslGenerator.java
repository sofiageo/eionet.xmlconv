package eionet.xmlconv.conversions.services;

/*import eionet.gdem.XMLConvException;
import eionet.gdem.conversion.converters.TransformerErrorListener;
import eionet.gdem.http.CustomURI;
import eionet.gdem.qa.engines.SaxonProcessor;*/
import eionet.xmlconv.conversions.Properties;
import eionet.xmlconv.conversions.cache.MemoryCache;
import eionet.xmlconv.conversions.exceptions.XMLConvException;
import eionet.xmlconv.conversions.http.CustomURI;
import eionet.xmlconv.conversions.services.converters.TransformerErrorListener;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * XSL scripts generator.
 * @author Unknown
 * @author George Sofianos
 */
@Service
public class XslGenerator {

    private Processor processor;

    @Autowired
    public XslGenerator(Processor processor) {
        this.processor = processor;
    }

    // TODO: Replace custom cache.
    public static MemoryCache MemCache = new MemoryCache(10000, 10);

    /**
     * Converts XML
     * @param xmlURL The XML URL
     * @param conversionURL Conversion URL
     * @return InputStream
     * @throws XMLConvException If an error occurs.
     * TODO FIX THIS
     */
    public static ByteArrayInputStream convertXML(String xmlURL, String conversionURL) throws XMLConvException {
        String cacheId = xmlURL + "_" + conversionURL;
        byte[] result = (byte[]) MemCache.getContent(cacheId);
        if (result == null) {
            result = makeDynamicXSL(xmlURL, conversionURL);
            MemCache.put(cacheId, result, Integer.MAX_VALUE);
        }
        return new ByteArrayInputStream(result);
    }

    /**
     * Creates dynamic XSL file
     * @param sourceURL Source URL
     * @param xslFile XSL file
     * @return XSL byte array
     * @throws XMLConvException If an error occurs.
     */
    private static byte[] makeDynamicXSL(String sourceURL, String xslFile) throws XMLConvException {
        byte[] result = null;
        ByteArrayOutputStream os = null;
        try {
            CustomURI uri = new CustomURI(sourceURL);
            uri.getURL();
            os = new ByteArrayOutputStream();

            Processor proc = new Processor(false);
            XsltCompiler comp = proc.newXsltCompiler();
            TransformerErrorListener errors = new TransformerErrorListener();
            StreamSource transformerSource = new StreamSource(xslFile);
            transformerSource.setSystemId(xslFile);

            XsltExecutable exp = comp.compile(transformerSource);
            // TODO: Maybe replace this with HTTP file manager to take advantage of the file cache.
            XdmNode source = proc.newDocumentBuilder().build(new StreamSource(sourceURL));
            Serializer ser = proc.newSerializer(os);
            ser.setOutputProperty(Serializer.Property.METHOD, "html");
            ser.setOutputProperty(Serializer.Property.INDENT, "yes");
            XsltTransformer trans = exp.load();
            trans.setInitialContextNode(source);
            trans.setParameter(new QName("dd_domain"), new XdmAtomicValue(Properties.DD_URL));

            trans.setErrorListener(errors);
            trans.setDestination(ser);
            trans.transform();

            result = os.toByteArray();
        } catch (MalformedURLException mfe) {
            throw new XMLConvException("Bad URL : " + mfe.toString(), mfe);
        } catch (IOException ioe) {
            throw new XMLConvException("Error opening URL " + ioe.toString(), ioe);
        } catch (Exception e) {
            throw new XMLConvException("Error converting: " + e.toString(), e);
        }
        return result;
    }

}
