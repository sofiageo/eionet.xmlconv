package eionet.gdem.api.qa.web;

import com.google.gson.Gson;
import eionet.gdem.api.qa.service.QaService;
import eionet.gdem.test.ApplicationTestContext;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 *
 * @author Vasilis Skiadas<vs@eworx.gr>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationTestContext.class})
public class QaControllerTestIT {

    private MockMvc mockMvc;

    @Autowired
    QaController qaController;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(qaController).build();
    }
    
    
    @Test
    public void testFailToSendQaRequestBecauseOfEmptySourceUrl() throws Exception {
        MockHttpServletRequestBuilder request = post("/sendQARequest");
        request.contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void testFailToSendQaRequestBecauseOfEmptyScriptId() throws Exception {
        MockHttpServletRequestBuilder request = post("/sendQARequest");
        request.contentType(MediaType.APPLICATION_JSON);
        HashMap<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("source_url", "http://example.library");
        request.content(new Gson().toJson(requestBody));
        mockMvc.perform(request).andDo(print());
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

  
    @Test
    public void testSuccessFullSendQaRequest() throws Exception {
        MockHttpServletRequestBuilder request = post("/sendQARequest");
        request.contentType(MediaType.APPLICATION_JSON);
        HashMap<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("source_url", "http://converterstest.eionet.europa.eu/xmlfile/aqd-labels.xml");
        requestBody.put("script_id", "-1");
        request.content(new Gson().toJson(requestBody));
        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    public void testFailToScheduleQaRequestBecauseOfEmptyEnvelopeUrl() throws Exception {
        MockHttpServletRequestBuilder request = post("/analyzeEnvelope");
        request.contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(request).andDo(print());
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @Test
    public void testSuccessToScheduleQaRequest() throws Exception {
        MockHttpServletRequestBuilder request = post("/analyzeEnvelope");
        request.contentType(MediaType.APPLICATION_JSON);
        HashMap<String, String> requestBody = new HashMap<String, String>();
        requestBody.put("envelope_url", "http://cdrtest.eionet.europa.eu/gr/colvjazdw/envvkyrww/AutomaticQA_70556");
        request.content(new Gson().toJson(requestBody));
        mockMvc.perform(request).andDo(print());
        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    public void testSuccessGetQaResultsForJob() throws Exception {
        MockHttpServletRequestBuilder request = get("/getQAResults/{jobId}", 42);
        mockMvc.perform(request).andExpect(status().isOk());
        mockMvc.perform(request).andDo(print());
    }

    @Test
    public void testSuccessListQueries() throws Exception {
        MockHttpServletRequestBuilder request = get("/listQueries");
        request.param("schema", "http://dd.eionet.europa.eu/schemas/eu-ets-article21-1.1/Article21Questionnaire.xsd");
        mockMvc.perform(request).andExpect(status().isOk());
        mockMvc.perform(request).andDo(print());
    }

}
