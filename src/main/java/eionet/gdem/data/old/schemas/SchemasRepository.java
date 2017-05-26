package eionet.gdem.data.old.schemas;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 *
 */
@Repository
public class SchemasRepository implements SchemasDao {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public String schemaUrl() {

    }
}
