package eionet.gdem.api.qa.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import eionet.gdem.XMLConvException;
import eionet.gdem.api.errors.EmptyParameterException;
import eionet.gdem.api.errors.QaServiceException;
import eionet.gdem.api.qa.model.EnvelopeWrapper;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.test.ApplicationTestContext;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import org.apache.commons.lang.builder.EqualsBuilder;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class QaControllerTest22 {

    @Mock
    private QaService qaServiceMock;

    QaController qaController;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.qaController = new QaController(qaServiceMock);
    }

    @Test(expected = EmptyParameterException.class)
    public void testFailToperformInstantQARequestOnFileBecauseOfEmptySourceUrl() throws EmptyParameterException, XMLConvException, UnsupportedEncodingException {
        EnvelopeWrapper envelopeWrapper = new EnvelopeWrapper();
        qaController.performInstantQARequestOnFile(envelopeWrapper);
    }

    @Test(expected = EmptyParameterException.class)
    public void testFailToperformInstantQARequestOnFileBecauseOfEmptyScriptId() throws EmptyParameterException, XMLConvException, UnsupportedEncodingException {
        EnvelopeWrapper envelopeWrapper = new EnvelopeWrapper();
        envelopeWrapper.setSourceUrl("\"http://converterstest.eionet.europa.eu/xmlfile/aqd-labels.xml");
        qaController.performInstantQARequestOnFile(envelopeWrapper);
    }

    @Test(expected = XMLConvException.class)
    public void testFailToSendQaRequestBecauseOfXMLConvException() throws Exception {
        String sourceUrl = "http://converterstest.eionet.europa.eu/xmlfile/aqd-labels.xml";
        String scriptId = "-1";
        EnvelopeWrapper envelopeWrapper = new EnvelopeWrapper();
        envelopeWrapper.setSourceUrl(sourceUrl);
        envelopeWrapper.setScriptId(scriptId);
        when(qaServiceMock.runQaScript(anyString(), anyString())).thenThrow(new QaServiceException());
        qaController.performInstantQARequestOnFile(envelopeWrapper);
        ArgumentCaptor<String> sourceUrlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> scriptIdCaptor = ArgumentCaptor.forClass(String.class);
        verify(qaServiceMock, times(1)).runQaScript(sourceUrlCaptor.capture(), scriptIdCaptor.capture());
        assertTrue(EqualsBuilder.reflectionEquals(sourceUrlCaptor.getValue(), sourceUrl));
        assertTrue(EqualsBuilder.reflectionEquals(scriptIdCaptor.getValue(), scriptId));
    }

    @Test(expected = EmptyParameterException.class)
    public void testFailToscheduleQaRequestOnEnvelopeEmptyParameterException() throws EmptyParameterException, XMLConvException, JsonProcessingException {
        EnvelopeWrapper envelopeWrapper = new EnvelopeWrapper();
        qaController.scheduleQaRequestOnEnvelope(envelopeWrapper);
    }

    
    
    
    @Test
    public void SuccessScheduleQaRequestOnEnvelope() throws XMLConvException, EmptyParameterException, JsonProcessingException {
        String envelopeUrl = "http://cdrtest.eionet.europa.eu/gr/colvjazdw/envvkyrww/AutomaticQA_70556";
        EnvelopeWrapper envelopeWrapper = new EnvelopeWrapper();
        envelopeWrapper.setEnvelopeUrl(envelopeUrl);
        qaController.scheduleQaRequestOnEnvelope(envelopeWrapper);
        ArgumentCaptor<String> envelopeUrlCaptor = ArgumentCaptor.forClass(String.class);
        verify(qaServiceMock, times(1)).scheduleJobs(envelopeUrlCaptor.capture());
        assertTrue(EqualsBuilder.reflectionEquals(envelopeUrlCaptor.getValue(), envelopeUrl));
    }
}
