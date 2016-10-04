package eionet.gdem.api.serverstatus.web.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Properties;
import eionet.gdem.XMLConvException;
import eionet.gdem.api.serverstatus.web.service.ServerStatusObject;
import eionet.gdem.api.serverstatus.web.service.ServerStatusService;
import eionet.gdem.services.db.dao.IXQJobDao;
import eionet.gdem.services.impl.QueueJobsServiceImpl;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import static java.util.Objects.isNull;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Aris Katsanas <aka@eworx.gr>
 */
@Service
public class ServerStatusServiceImpl implements ServerStatusService {
    
    private IXQJobDao ixqJobDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueJobsServiceImpl.class);

    @Autowired
    public ServerStatusServiceImpl(@Qualifier("xqJobDao") IXQJobDao ixqJobDao) {
        this.ixqJobDao = ixqJobDao;
    }    
    @Override
    public ServerStatusObject getServerStatus() throws XMLConvException {
        int isRancher = Properties.getIsRancher();
                
        ServerStatusObject res = new ServerStatusObject () ;
        
        if (isRancher == 1 ) {
            try {
                getRancherInfo ( res );
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(ServerStatusServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return getWorkqueueInfo( res );
        
    }
    
    private ServerStatusObject getWorkqueueInfo ( ServerStatusObject res ) {
        try {
            String [] [] queryResults = ixqJobDao.getJobsSumInstanceAndStatus();
                        if ( isNull(queryResults) ) return null;
            for ( int i = 0 ; i < queryResults.length ; i ++ ) {
                res.insertJobStatusByInstance(queryResults [i][0], queryResults [i][1], parseInt ( queryResults [i][2]) );
            }
            return res;

        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(ServerStatusServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    private void getRancherInfo( ServerStatusObject res ) throws IOException {
                
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(Collections.singletonList(new MediaType("application","json")));
        HttpEntity<?> requestEntity = new HttpEntity<Object>(requestHeaders);

        // Create a new RestTemplate instance
        RestTemplate restTemplate = new RestTemplate();

        // Add the Jackson message converter
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        // Make the HTTP GET request
        ResponseEntity<  String  > responseEntity = restTemplate.exchange( Properties.rancherMetadataUrl , HttpMethod.GET, requestEntity, String.class );
        String responseBody = responseEntity.getBody();

        ObjectMapper mapper = new ObjectMapper();
        
        RancherStatus[] rancherStatus = mapper.readValue( responseBody , RancherStatus[].class );
        
        for (int i = 0; i < rancherStatus.length ; i ++) {
            res.insertHealthStatusByInstance( rancherStatus[i].getName(), rancherStatus[i].getHealth_state());
        }
        
    }
    
}
