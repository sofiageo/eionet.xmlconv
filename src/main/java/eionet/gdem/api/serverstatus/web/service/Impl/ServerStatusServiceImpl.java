package eionet.gdem.api.serverstatus.web.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import eionet.gdem.Properties;
import eionet.gdem.exceptions.XMLConvException;
import eionet.gdem.api.serverstatus.web.service.ServerStatusObject;
import eionet.gdem.api.serverstatus.web.service.ServerStatusService;
import eionet.gdem.web.spring.workqueue.IXQJobDao;
import eionet.gdem.web.spring.workqueue.QueueJobsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

import static java.lang.Integer.parseInt;
import static java.util.Objects.isNull;

/**
 *
 * @author Aris Katsanas <aka@eworx.gr>
 */
@Service
public class ServerStatusServiceImpl implements ServerStatusService {
    
    private IXQJobDao ixqJobDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueJobsServiceImpl.class);

    /** holds the clustered quartz scheduler shared amongst instances*/
    private static class isRancher {
        static final int IS_RANCHER = Properties.getIsRancher();
    }

    private static RestTemplate restTemplate;

    @Autowired
    public ServerStatusServiceImpl(@Qualifier("xqJobDao") IXQJobDao ixqJobDao , RestTemplate restTemplate) {
        this.ixqJobDao = ixqJobDao;
        this.restTemplate = restTemplate;
    }

    @Override
    public ServerStatusObject getServerStatus() throws XMLConvException {

        ServerStatusObject res = new ServerStatusObject () ;
        
        if ( isRancher.IS_RANCHER == 1 ) {
            try {
                getRancherInfo ( res );
            } catch (IOException ex) {
                LOGGER.error( "getRancherInfo: ", ex );
                res = new ServerStatusObject () ;
            }
        }
        return getWorkqueueInfo( res );
        
    }
    
    private ServerStatusObject getWorkqueueInfo ( ServerStatusObject res ) {
        try {
            String [] [] queryResults = ixqJobDao.getJobsSumInstanceAndStatus();
            if ( isNull(queryResults) ) {
                return ( isRancher.IS_RANCHER == 1) ? res : null;
            }
            for ( int i = 0 ; i < queryResults.length ; i ++ ) {
                res.insertJobStatusByInstance(queryResults [i][0], queryResults [i][1], parseInt ( queryResults [i][2]) );
            }
            return res;

        } catch (SQLException ex) {
            LOGGER.error( "getWorkqueueInfo: ", ex);
            return null;
        }
    }
    
    private void getRancherInfo( ServerStatusObject res ) throws IOException {
                
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAccept(Collections.singletonList(new MediaType("application","json")));
        HttpEntity<?> requestEntity = new HttpEntity<>(requestHeaders);

        // Add the Jackson message converter
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        // Make the HTTP GET request
        ResponseEntity<  String  > responseEntity = restTemplate.exchange( Properties.rancherMetadataUrl , HttpMethod.GET, requestEntity, String.class );
        String responseBody = responseEntity.getBody();

        ObjectMapper mapper = new ObjectMapper();
        
        RancherStatus[] rancherStatus = mapper.readValue( responseBody , RancherStatus[].class );
        
        for (int i = 0; i < rancherStatus.length ; i ++) {
            res.insertHealthStatusByInstance( rancherStatus[i].getName(), rancherStatus[i].getHealth_state() + " / " + rancherStatus[i].getState());
        }
        
    }
    
}
