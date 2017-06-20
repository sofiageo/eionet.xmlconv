package eionet.xmlconv.conversions.services.saxon;

import eionet.xmlconv.conversions.exceptions.ServiceException;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

/**
 *
 *
 */
@Service
public class TransformationService {

    private Processor processor;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformationService.class);

    @Autowired
    public TransformationService(Processor processor) {
        this.processor = processor;
    }

    public void transform(InputStream in, InputStream xslStream, OutputStream out) {
        try {
            /*Processor proc = SaxonProcessor.getProcessor();*/
            XsltCompiler comp = processor.newXsltCompiler();
            TransformerErrorListener errors = new TransformerErrorListener();
            StreamSource transformerSource = new StreamSource(xslStream);
/*            if (getXslPath() != null) {
                transformerSource.setSystemId(getXslPath());
            }*/
            XsltExecutable exp = comp.compile(transformerSource);
            XdmNode source = processor.newDocumentBuilder().build(new StreamSource(in));
            Serializer ser = processor.newSerializer(out);
            //ser.setOutputProperty(Serializer.Property.METHOD, "html");
            //ser.setOutputProperty(Serializer.Property.INDENT, "yes");
            XsltTransformer trans = exp.load();
            trans.setInitialContextNode(source);
            /*trans.setParameter(new QName(DD_DOMAIN_PARAM), new XdmAtomicValue(eionet.gdem.Properties.ddURL));*/
            /*setTransformerParameters(trans);*/
            trans.setErrorListener(errors);
            trans.setDestination(ser);
            trans.transform();
        } catch (SaxonApiException e) {
            LOGGER.error("XSLT error: ", e);
            throw new ServiceException(e);
        }

    }

    /**
     * Sets the map of xsl global parameters to xsl transformer.
     * @param transformer XSL transformer object.
     */
    /*private void setTransformerParameters(XsltTransformer transformer) {

        if (xslParams == null) {
            return;
        }

        Iterator<String> keys = xslParams.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = xslParams.get(key);
            if (value != null) {
                transformer.setParameter(new QName(key), new XdmAtomicValue(value));
            }
        }

        // sets base URI for xmlfiles uploaded into xmlconv
        String xmlFilePathURI = Utils.getURIfromPath(eionet.gdem.Properties.xmlfileFolder, true);

        if (xmlFilePathURI != null) {
            transformer.setParameter(new QName(XML_FOLDER_URI_PARAM), new XdmAtomicValue(xmlFilePathURI));
        }

    }*/

}
