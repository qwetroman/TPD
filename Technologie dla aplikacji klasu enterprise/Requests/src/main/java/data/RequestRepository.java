package data;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.List;
import req.entities.Request;

@ApplicationScoped
public class RequestRepository {
    
    @Inject
    private EntityManager em;
    
    public void create(Request entity) {
        em.persist(entity);
    }
    
    public void edit(Request entity) {
        em.merge(entity);
    }
    
    public void remove(Request entity) {
        em.remove(em.merge(entity));
    }
    
    public Request find(Object id) {
        return em.find(Request.class, id);
    }
    
    public List<Request> findAll() {
        CriteriaQuery<Request> cq = em.getCriteriaBuilder().createQuery(Request.class);
        cq.select(cq.from(Request.class));
        return em.createQuery(cq).getResultList();
    }
    
}
