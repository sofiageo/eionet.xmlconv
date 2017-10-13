package eionet.gdem.web.old.schemas;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 *
 */
public class SchemasRepository implements SchemasDao {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public String schemaUrl(String schemaId) {
        return "test";
    }
}
