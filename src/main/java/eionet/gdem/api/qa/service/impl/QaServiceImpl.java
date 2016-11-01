package eionet.gdem.api.qa.service.impl;

import eionet.gdem.XMLConvException;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.qa.XQueryService;
import eionet.gdem.api.qa.model.QaResultsWrapper;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@Service
public class QaServiceImpl implements QaService {

    @Override
    public HashMap<String, String> extractSchemasAndFilesFromEnvelopeUrl(String envelopeUrl) throws XMLConvException {
        HashMap<String, String> fileSchemaAndLinks = new HashMap<String, String>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(envelopeUrl + "/xml").openStream());
            XPath xPath = XPathFactory.newInstance().newXPath();
            XPathExpression expr = xPath.compile("//envelope/file");
            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            int length = nl.getLength();
            for (int i = 0; i < length; i++) {
                NamedNodeMap fileNode = nl.item(i).getAttributes();
                fileSchemaAndLinks.put(fileNode.getNamedItem("link").getTextContent(), fileNode.getNamedItem("schema").getTextContent());
            }

        } catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException ex) {
            throw new XMLConvException("exception while parsing the envelope URL:" + envelopeUrl + " to extract files and schemas", ex);
        }
        return fileSchemaAndLinks;
    }

    @Override
    public List<QaResultsWrapper> scheduleJobs(String envelopeUrl) throws XMLConvException {

        HashMap<String, String> fileSchemasAndLinks = extractSchemasAndFilesFromEnvelopeUrl(envelopeUrl);

        XQueryService xqService = new XQueryService();
        Hashtable table = new Hashtable();
        try {
            for (Map.Entry<String, String> entry : fileSchemasAndLinks.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key != "" && value != "") {
                    Vector files = new Vector();
                    files.add(key);
                    table.put(value, files);
                }
            }
            Vector jobIdsAndFileUrls = xqService.analyzeXMLFiles(table);
            List<QaResultsWrapper> results = new ArrayList<QaResultsWrapper>();
            for (int i = 0; i < jobIdsAndFileUrls.size(); i++) {
                Vector<String> KeyValuePair = (Vector<String>) jobIdsAndFileUrls.get(i);
                QaResultsWrapper qaResult = new QaResultsWrapper();
                qaResult.setJobId(KeyValuePair.get(0));
                qaResult.setFileUrl(KeyValuePair.get(1));
                results.add(qaResult);
            }

            return results;
        } catch (XMLConvException ex) {
            throw new XMLConvException("error scheduling Jobs with XQueryService ", ex);
        }

    }

    @Override
    public Vector runQaScript(String sourceUrl, String scriptId) throws XMLConvException {
        XQueryService xqService = new XQueryService();
        try {
            return xqService.runQAScript(sourceUrl, scriptId);
        } catch (XMLConvException ex) {
            throw new XMLConvException("error running Qa Script for sourceUrl :" + sourceUrl + " and scriptId:" + scriptId, ex);
        }
    }

}