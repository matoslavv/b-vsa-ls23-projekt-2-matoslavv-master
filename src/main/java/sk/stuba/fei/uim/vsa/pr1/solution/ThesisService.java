package sk.stuba.fei.uim.vsa.pr1.solution;

import lombok.extern.slf4j.Slf4j;
import sk.stuba.fei.uim.vsa.pr1.AbstractThesisService;
import sk.stuba.fei.uim.vsa.pr1.bonus.Page;
import sk.stuba.fei.uim.vsa.pr1.bonus.Pageable;
import sk.stuba.fei.uim.vsa.pr1.bonus.PageableThesisService;
import sk.stuba.fei.uim.vsa.pr2.BCryptService;

import javax.persistence.*;
import javax.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


@Slf4j
public class ThesisService extends AbstractThesisService<Student, Teacher, Thesis> implements PageableThesisService<Student, Teacher, Thesis> {

    private final ThesisServiceUtils utils;

    public ThesisService() {
        super();
        utils = new ThesisServiceUtils(this.emf);
    }

    @Override
    public Student createStudent(Long aisId, String name, String email, String password, Optional<Integer> year, Optional<Integer> term, Optional<String> programme) {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Student> qId = em.createQuery("SELECT s FROM Student s WHERE s.aisId = :id", Student.class);
            qId.setParameter("id", aisId);
            qId.getSingleResult();

            throw new NonUniqueResultException("Student with specific ID already exists.");
        } catch (NoResultException e) {
        } catch (NonUniqueResultException e) {
            em.close();
            throw e;
        }

        try {
            TypedQuery<Student> qEmail = em.createQuery("SELECT s FROM Student s WHERE s.email = :email", Student.class);
            qEmail.setParameter("email", email);
            qEmail.getSingleResult();

            throw new NonUniqueResultException("Student with specific email already exists.");
        } catch (NoResultException e) {
        } catch (NonUniqueResultException e) {
            em.close();
            throw e;
        }

        try {

            if (aisId == null || name == null || email == null || password == null) {
                throw new IllegalArgumentException("Required fields are not filled.");
            }

            Student s = new Student();
            s.setAisId(aisId);
            s.setName(name);
            s.setEmail(email);
            s.setPassword(BCryptService.hash(new String(Base64.getDecoder().decode(password))));
            s.setYear(year.orElse(null));
            s.setTerm(term.orElse(null));
            s.setProgramme(programme.orElse(null));
            em.getTransaction().begin();
            em.persist(s);
            em.getTransaction().commit();

            return s;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Student getStudent(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            if (id == null) {
                throw new IllegalArgumentException("Student ID is null");
            }

            return em.find(Student.class, id);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public <T extends User> T getUserByEmail(String email, Class<T> clazz) {
        EntityManager em = emf.createEntityManager();

        if (email == null) {
            em.close();
            throw new IllegalArgumentException("Provided email must not be null");
        }

        TypedQuery<T> query = em.createQuery("SELECT u FROM " + clazz.getName() + " u WHERE u.email = :email", clazz);
        query.setParameter("email", email);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @Override
    public Student updateStudent(Student student) {
        if (student == null)
            throw new IllegalArgumentException("Provided student must not be null");
        if (student.getAisId() == null)
            throw new IllegalArgumentException("Provided student.aisId must not be null");
        return utils.update(student);
    }

    @Override
    public List<Student> getStudents() {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Student> qs = em.createQuery("SELECT s FROM Student s", Student.class);
            return qs.getResultList();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Student deleteStudent(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            if (id == null) {
                throw new IllegalArgumentException("Student ID is null");
            }

            Student student = em.find(Student.class, id);
            if (student == null) {
                throw new NotFoundException("Student was not found");
            }

            em.getTransaction().begin();
            Thesis thesis = student.getThesis();
            if (thesis != null) {
                thesis.setAuthor(null);
                thesis.setStatus(ThesisStatus.FREE_TO_TAKE);
                em.merge(thesis);
            }

            em.remove(student);
            em.getTransaction().commit();
            return student;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Teacher createTeacher(Long aisId, String name, String email, String password, Optional<String> department, Optional<String> institute) {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Teacher> qId = em.createQuery("SELECT s FROM Teacher s WHERE s.aisId = :id", Teacher.class);
            qId.setParameter("id", aisId);
            qId.getSingleResult();

            throw new NonUniqueResultException("Teacher with specific ID already exists.");
        } catch (NoResultException e) {
        } catch (NonUniqueResultException e) {
            em.close();
            throw e;
        }

        try {
            TypedQuery<Teacher> qEmail = em.createQuery("SELECT s FROM Teacher s WHERE s.email = :email", Teacher.class);
            qEmail.setParameter("email", email);
            qEmail.getSingleResult();

            throw new NonUniqueResultException("Teacher with specific email already exists.");
        } catch (NoResultException e) {
        } catch (NonUniqueResultException e) {
            em.close();
            throw e;
        }

        try {
            if (aisId == null || name == null || email == null || password == null) {
                throw new IllegalArgumentException("Required fields are not filled."); /////////////
            }

            Teacher t = new Teacher();
            t.setAisId(aisId);
            t.setName(name);
            t.setEmail(email);
            t.setPassword(BCryptService.hash(new String(Base64.getDecoder().decode(password))));
            t.setInstitute(institute.orElse(null));
            t.setDepartment(department.orElse(null));
            t.setSupervisedTheses(Collections.emptyList());

            em.getTransaction().begin();
            em.persist(t);
            em.getTransaction().commit();

            return t;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Teacher getTeacher(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            if (id == null) {
                throw new IllegalArgumentException("Teacher ID is null");
            }

            return em.find(Teacher.class, id);
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Teacher updateTeacher(Teacher teacher) {
        if (teacher == null)
            throw new IllegalArgumentException("Provided teacher must not be null");
        if (teacher.getAisId() == null)
            throw new IllegalArgumentException("Provided teacher.aisId must not be null");
        return utils.update(teacher);
    }

    @Override
    public List<Teacher> getTeachers() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Teacher> qt = em.createQuery("SELECT t FROM Teacher t", Teacher.class);
            return qt.getResultList();
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Teacher deleteTeacher(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            if (id == null) {
                throw new IllegalArgumentException("Teacher ID is null");
            }
            
            Teacher teacher = em.find(Teacher.class, id);
            if (teacher == null) {
                throw new NotFoundException("Teacher was not found");
            }

            utils.execute("delete from Thesis t where t.supervisor.aisId = :teacherId", Collections.singletonMap("teacherId", id), true);

            em.getTransaction().begin();
            List<Thesis> theses = teacher.getSupervisedTheses();
            if (!theses.isEmpty()) {
                for(Thesis t : theses) {
                    Student dev = t.getAuthor();
                    if (dev != null) {
                        dev.setThesis(null);
                        em.merge(dev);
                    }
                }
            }

            em.remove(teacher);
            em.getTransaction().commit();

            return teacher;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Thesis makeThesisAssignment(Long supervisor, String title, ThesisType type, String registrationNumber, Optional<String> description) {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Thesis> qThesis = em.createQuery("SELECT t FROM Thesis t WHERE t.registrationNumber = :regNumber", Thesis.class);
            qThesis.setParameter("regNumber", registrationNumber);
            qThesis.getSingleResult();

            throw new NonUniqueResultException("Thesis with registration number already exists");
        } catch (NoResultException ignored) {

        } catch (NonUniqueResultException e) {
            em.close();
            throw e;
        }

        try {
            if (supervisor == null) {
                throw new IllegalArgumentException("Supervisor ID cannot be null");
            }

            Teacher sup = em.find(Teacher.class, supervisor);
            if (sup == null) {
                throw new NotFoundException("Supervisor was not found");
            }

            Thesis t = new Thesis();
            t.setRegistrationNumber(registrationNumber);
            t.setTitle(title);
            t.setDescription(description.orElse(null));
            t.setSupervisor(sup);
            t.setDepartment(sup.getDepartment());

            LocalDate now = LocalDate.now();
            t.setPublishedOn(Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            t.setDeadline(Date.from(now.plusMonths(3).atStartOfDay(ZoneId.systemDefault()).toInstant()));
            t.setStatus(ThesisStatus.FREE_TO_TAKE);
            t.setType(type);

            em.getTransaction().begin();
            em.persist(t);
//            em.merge(sup);
            sup.getSupervisedTheses().add(t);
            em.getTransaction().commit();

            return t;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Thesis assignThesis(Long thesisId, Long studentId) {
        EntityManager em = emf.createEntityManager();

        try {
            if (thesisId == null || studentId == null) {
                throw new IllegalArgumentException("Thesis ID or Student ID is null");
            }

            Thesis t = this.getThesis(thesisId);
            if (t == null) {
                throw new NotFoundException("Thesis was not found");
            }
            if (t.getStatus().compareTo(ThesisStatus.FREE_TO_TAKE) != 0 || LocalDate.now().isAfter(t.getDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())) {
                throw new IllegalStateException("Thesis can't be assigned");
            }

            // praca moze byt priradena
            Student s = this.getStudent(studentId);
            if (s == null) {
                throw new NotFoundException("Student was not found");
            }

            if (s.getThesis() != null) {
                em.getTransaction().begin();
                s.getThesis().setStatus(ThesisStatus.FREE_TO_TAKE);
                s.getThesis().setAuthor(null);
                em.merge(s.getThesis());
                s.setThesis(null);
                em.getTransaction().commit();
            }

            em.getTransaction().begin();
            s.setThesis(t);
            em.merge(s);

            t.setAuthor(s);
            t.setStatus(ThesisStatus.IN_PROGRESS);
            t = em.merge(t);
            em.getTransaction().commit();

            return t;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Thesis submitThesis(Long thesisId) {
        EntityManager em = emf.createEntityManager();

        try {
            if (thesisId == null) {
                throw new IllegalArgumentException("Thesis ID cannot be null");
            }

//            TypedQuery<Thesis> qThesis = em.createQuery("SELECT t FROM Thesis t WHERE t.id = :thesisId", Thesis.class);
//            qThesis.setParameter("thesisId", thesisId);

//            Thesis t = qThesis.getSingleResult();
            Thesis t = this.getThesis(thesisId);

            if (t.getStatus().compareTo(ThesisStatus.SUBMITTED) == 0 || LocalDate.now().isAfter(t.getDeadline().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()) || t.getAuthor() == null) {
                throw new IllegalStateException("Thesis can't be submitted");
            }

            em.getTransaction().begin();
            if (t.getStatus() == ThesisStatus.IN_PROGRESS) {
                t.setStatus(ThesisStatus.SUBMITTED);
            } else {
                throw new IllegalStateException("Thesis cannot be submitted");
            }

//            switch (t.getStatus()) {
//                case IN_PROGRESS:
//                    t.setStatus(ThesisStatus.SUBMITTED);
//                    break;
//                default:
//                    em.getTransaction().commit();
//                    em.close();
//                    return null;
//            }
            t = em.merge(t);
            em.getTransaction().commit();

            return t;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;
        } finally {
            em.close();
        }

        //        if (thesisId == null)
//            throw new IllegalArgumentException("Thesis id must not be null");
//        Thesis thesis = getThesis(thesisId);
//        if (thesis == null)
//            throw new IllegalArgumentException("Thesis with id '" + thesisId + "' has not been found");
//        if (LocalDate.now().isAfter(asLocalDate(thesis.getDeadline())))
//            throw new IllegalStateException("Thesis cannot be submitted after the deadline on " + thesis.getDeadline().toString());
//        if (thesis.getStatus() != ThesisStatus.IN_PROGRESS)
//            throw new IllegalStateException("Thesis is not in the state to be submitted");
//        if (thesis.getAuthor() == null)
//            throw new IllegalStateException("Thesis cannot be submitted if it hasn't been assigned to a student");
//        thesis.setStatus(ThesisStatus.SUBMITTED);
//        return utils.update(thesis);
    }

    public List<Thesis> searchThesis(Long studentId, Long teacherId) {
        EntityManager em = emf.createEntityManager();

        try {
            if (studentId == null && teacherId == null) {
                throw new IllegalArgumentException("Neither Student ID nor Teacher ID are filled in");
            }

            TypedQuery<Thesis> qThesis;
            if (teacherId != null) {
                qThesis = em.createQuery("SELECT t FROM Thesis t WHERE t.supervisor.aisId = :teacherId", Thesis.class);
                qThesis.setParameter("teacherId", teacherId);

            } else {
                qThesis = em.createQuery("SELECT t FROM Thesis t WHERE t.author.aisId = :studentId", Thesis.class);
                qThesis.setParameter("studentId", studentId);
            }

            return qThesis.getResultList();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Thesis deleteThesis(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            if (id == null) {
                throw new IllegalArgumentException("Thesis ID cannot be null");
            }

            Thesis thesis = em.find(Thesis.class, id);
            if (thesis == null) {
                throw new NotFoundException("Thesis was not found");
            }

            em.getTransaction().begin();
            Student s = thesis.getAuthor();
            if (s != null) {
                s.setThesis(null);
                em.merge(s);
            }

            Teacher sup = thesis.getSupervisor();
            List<Thesis> theses = sup.getSupervisedTheses();
            theses.removeIf(t -> t.getId().equals(id));
            em.merge(sup);

//            utils.execute("delete from Thesis t where t = :teacherId", Collections.singletonMap("teacherId", id), true);

            em.remove(thesis);
            em.getTransaction().commit();
            return thesis;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Thesis> getTheses() {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Thesis> qt = em.createQuery("SELECT t FROM Thesis t", Thesis.class);
            return qt.getResultList();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Thesis> getThesesByTeacher(Long teacherId) {
        return utils.findByNamedQuery(Thesis.FIND_ALL_BY_SUPERVISOR, Thesis.class, Collections.singletonMap("teacherId", teacherId));
    }

    @Override
    public Thesis getThesisByStudent(Long studentId) {
        List<Thesis> results = utils.findByNamedQuery(Thesis.FIND_ALL_BY_AUTHOR, Thesis.class, Collections.singletonMap("studentId", studentId));
        return results.stream().findFirst().orElse(null);
    }

    @Override
    public Thesis getThesis(Long id) {
        EntityManager em = emf.createEntityManager();

        try {
            if (id == null) {
                throw new IllegalArgumentException("Thesis ID cannot be null");
            }

            return em.find(Thesis.class, id);
        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Thesis updateThesis(Thesis thesis) {
        if (thesis == null)
            throw new IllegalArgumentException("Provided thesis must not be null");
        if (thesis.getId() == null)
            throw new IllegalArgumentException("Provided thesis.id must not be null");
        return utils.update(thesis);
    }

    // --- BONUS ---

    @Override
    public Page<Student> findStudents(Optional<String> name, Optional<String> year, Pageable pageable) {
        String query = "select s from Student s";
        List<String> conditions = new ArrayList<>();
        name.ifPresent(s -> conditions.add("s.name like '%" + s + "%'"));
        year.ifPresent(s -> conditions.add("s.year = " + Integer.parseInt(s)));
        if (!conditions.isEmpty()) {
            query += " where ";
            query += String.join(" and ", conditions);
        }
        final String finalQuery = query;
        List<Student> students = utils.findByQuery(em ->
                em.createQuery(finalQuery, Student.class)
                        .setMaxResults(pageable.getPageSize())
                        .setFirstResult(pageable.getPageSize() * pageable.getPageNumber()));
        Page<Student> page = new PageImpl<>(students, pageable);
        page.setTotalElements(utils.getCount(query, Collections.emptyMap()));
        page.getTotalPages();
        return page;
    }

    @Override
    public Page<Teacher> findTeachers(Optional<String> name, Optional<String> institute, Pageable pageable) {
        String query = "select t from Teacher t";
        List<String> conditions = new ArrayList<>();
        name.ifPresent(t -> conditions.add("t.name like '%" + t + "%'"));
        institute.ifPresent(t -> conditions.add("t.institute = '" + t + "'"));
        if (!conditions.isEmpty()) {
            query += " where ";
            query += String.join(" and ", conditions);
        }
        final String finalQuery = query;
        List<Teacher> teachers = utils.findByQuery(em ->
                em.createQuery(finalQuery, Teacher.class)
                        .setMaxResults(pageable.getPageSize())
                        .setFirstResult(pageable.getPageSize() * pageable.getPageNumber()));
        Page<Teacher> page = new PageImpl<>(teachers, pageable);
        page.setTotalElements(utils.getCount(query, Collections.emptyMap()));
        page.getTotalPages();
        return page;
    }

    @Override
    public Page<Thesis> findTheses(Optional<String> department, Optional<Date> publishedOn, Optional<String> type, Optional<String> status, Pageable pageable) {
        String query = "select t from Thesis t";
        List<String> conditions = new ArrayList<>();
        Map<String, Object> parameters = new HashMap<>();
        department.ifPresent(d -> {
            conditions.add("t.department = :ddepartment");
            parameters.put("ddepartment", d);
        });
        publishedOn.ifPresent(d -> {
            conditions.add("t.publishedOn = :publishDate");
            parameters.put("publishDate", d);
        });
        type.ifPresent(t -> {
            conditions.add("t.type = :ttype");
            parameters.put("ttype", ThesisType.valueOf(t.toUpperCase()));
        });
        status.ifPresent(s -> {
            conditions.add("t.status = :sstatus");
            parameters.put("sstatus", ThesisStatus.valueOf(s.toUpperCase()));
        });
        if (!conditions.isEmpty()) {
            query += " where ";
            query += String.join(" and ", conditions);
        }
        final String finalQuery = query;
        List<Thesis> theses = utils.findByQuery(em -> {
            TypedQuery<Thesis> q = em.createQuery(finalQuery, Thesis.class)
                    .setMaxResults(pageable.getPageSize())
                    .setFirstResult(pageable.getPageSize() * pageable.getPageNumber());
            if (!parameters.isEmpty()) {
                parameters.forEach(q::setParameter);
            }
            return q;
        });
        Page<Thesis> page = new PageImpl<>(theses, pageable);
        page.setTotalElements(utils.getCount(query, parameters));
        page.getTotalPages();
        return page;
    }
}
