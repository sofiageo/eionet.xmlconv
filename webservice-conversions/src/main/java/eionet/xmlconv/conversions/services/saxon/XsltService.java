package eionet.xmlconv.conversions.services.saxon;

import eionet.xmlconv.conversions.exceptions.ServiceException;
import eionet.xmlconv.conversions.services.saxon.XsltErrorListener;
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

/**
 *
 *
 */
@Service
public class XsltService {

    private Processor processor;
    private static final Logger LOGGER = LoggerFactory.getLogger(XsltService.class);

    @Autowired
    public XsltService(Processor processor) {
        this.processor = processor;
    }

    public void transform() {
        try {
            /*Processor proc = SaxonProcessor.getProcessor();*/
            XsltCompiler comp = processor.newXsltCompiler();
            XsltErrorListener errors = new XsltErrorListener();
            StreamSource transformerSource = new StreamSource(xslStream);
            if (getXslPath() != null) {
                transformerSource.setSystemId(getXslPath());
            }
            XsltExecutable exp = comp.compile(transformerSource);
            XdmNode source = processor.newDocumentBuilder().build(new StreamSource(in));
            Serializer ser = processor.newSerializer(out);
            //ser.setOutputProperty(Serializer.Property.METHOD, "html");
            //ser.setOutputProperty(Serializer.Property.INDENT, "yes");
            XsltTransformer trans = exp.load();
            trans.setInitialContextNode(source);
            trans.setParameter(new QName(DD_DOMAIN_PARAM), new XdmAtomicValue(eionet.gdem.Properties.ddURL));
            setTransformerParameters(trans);
            trans.setErrorListener(errors);
            trans.setDestination(ser);
            trans.transform();
        } catch (SaxonApiException e) {
            LOGGER.error("XSLT error: ", e);
            throw new ServiceException(e);
        }

    }


}
