package eionet.gdem.api.serverstatus.web.service;

import eionet.gdem.exceptions.XMLConvException;

/**
 *
 * @author Aris Katsanas <aka@eworx.gr>
 */
public interface ServerStatusService {
    ServerStatusObject getServerStatus() throws XMLConvException;
}
