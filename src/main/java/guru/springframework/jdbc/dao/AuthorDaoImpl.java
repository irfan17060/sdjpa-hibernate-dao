package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class AuthorDaoImpl implements AuthorDao {

    private final EntityManagerFactory entityManagerFactory;

    @Override
    public List<Author> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("find_all_author", Author.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Author> listAuthorByLastNameLike(String lastName) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createQuery("SELECT a from Author a where a.lastName like :last_name");
            query.setParameter("last_name", lastName + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Author getById(Long id) {
        return getEntityManager().find(Author.class, id);
    }

    @Override
    public Author findAuthorByName(String firstName, String lastName) {
//        TypedQuery<Author> query = getEntityManager().createQuery("SELECT a FROM Author a " +
//                "WHERE a.firstName = :first_name and a.lastName = :last_name", Author.class);

        TypedQuery<Author> query = getEntityManager().createNamedQuery("find_author_by_name", Author.class);
        query.setParameter("first_name", firstName);
        query.setParameter("last_name", lastName);

        return query.getSingleResult();
    }

    @Override
    public Author saveNewAuthor(Author author) {
        EntityManager em = getEntityManager();  // which gives me a database connection
        em.getTransaction().begin();
        em.persist(author);
        em.flush();
        em.getTransaction().commit();

        return author;
    }

    @Override
    public Author updateAuthor(Author author) {
        EntityManager em = getEntityManager();
        em.joinTransaction();
        em.merge(author);
        em.flush();
        em.clear();
        return em.find(Author.class, author.getId());
    }

    @Override
    public void deleteAuthorById(Long id) {

        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Author author = em.find(Author.class, id);
        em.remove(author);
        em.flush();
        em.getTransaction().commit();
    }

    @Override
    public Author findAuthorByNameUsingNativeSql(String firstName, String lastName) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createNativeQuery("SELECT * from Author where first_name =? and last_name= ?", Author.class);
            query.setParameter(1, firstName);
            query.setParameter(2, lastName);
            Author a = (Author) query.getSingleResult();
            return a;
        } finally {
            em.close();
        }
    }


    private EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
}
