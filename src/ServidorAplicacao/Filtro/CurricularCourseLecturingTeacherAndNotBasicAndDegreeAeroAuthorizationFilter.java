/*
 * Created on 2/Dez/2003
 * 
 *  
 */
package ServidorAplicacao.Filtro;

import pt.utl.ist.berserk.ServiceRequest;
import pt.utl.ist.berserk.ServiceResponse;
import DataBeans.InfoCurricularCourse;
import DataBeans.InfoExecutionCourse;
import DataBeans.util.Cloner;
import Dominio.CurricularCourse;
import Dominio.ExecutionCourse;
import Dominio.ICurricularCourse;
import Dominio.IDegree;
import Dominio.IExecutionCourse;
import Dominio.IProfessorship;
import Dominio.ITeacher;
import ServidorAplicacao.IUserView;
import ServidorAplicacao.Filtro.exception.NotAuthorizedFilterException;
import ServidorPersistente.IPersistentCurricularCourse;
import ServidorPersistente.IPersistentExecutionCourse;
import ServidorPersistente.IPersistentProfessorship;
import ServidorPersistente.IPersistentTeacher;
import ServidorPersistente.ISuportePersistente;
import ServidorPersistente.OJB.SuportePersistenteOJB;
import Util.RoleType;

/**
 * @author T�nia Pous�o
 * 
 * This filter filter all currricular course that are of LEA(Engenharia
 * Aeroespacial) degree
 */
public class CurricularCourseLecturingTeacherAndNotBasicAndDegreeAeroAuthorizationFilter extends
        AuthorizationByRoleFilter {

    public CurricularCourseLecturingTeacherAndNotBasicAndDegreeAeroAuthorizationFilter() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see ServidorAplicacao.Filtro.AuthorizationByRoleFilter#getRoleType()
     */
    protected RoleType getRoleType() {
        return RoleType.TEACHER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ServidorAplicacao.Filtro.AuthorizationByRoleFilter#execute(pt.utl.ist.berserk.ServiceRequest,
     *      pt.utl.ist.berserk.ServiceResponse)
     */
    public void execute(ServiceRequest request, ServiceResponse response) throws Exception {
        IUserView id = getRemoteUser(request);
        Object[] argumentos = getServiceCallArguments(request);

        if ((id == null) || (id.getRoles() == null)
                || !AuthorizationUtils.containsRole(id.getRoles(), getRoleType())
                || !lecturesExecutionCourse(id, argumentos)
                || !CurricularCourseBelongsExecutionCourse(id, argumentos)
                || !CurricularCourseNotBasic(argumentos) || !CurricularCourseAeroDegree(argumentos)) {
            throw new NotAuthorizedFilterException();
        }
    }

    /**
     * @param id
     * @param argumentos
     * @return
     */
    private boolean CurricularCourseBelongsExecutionCourse(IUserView id, Object[] argumentos) {
        InfoExecutionCourse infoExecutionCourse = null;
        IExecutionCourse executionCourse = null;
        ICurricularCourse curricularCourse = null;
        InfoCurricularCourse infoCurricularCourse = null;
        ISuportePersistente sp;
        if (argumentos == null) {
            return false;
        }
        try {

            sp = SuportePersistenteOJB.getInstance();
            IPersistentExecutionCourse persistentExecutionCourse = sp.getIPersistentExecutionCourse();
            IPersistentCurricularCourse persistentCurricularCourse = sp.getIPersistentCurricularCourse();
            if (argumentos[0] instanceof InfoExecutionCourse) {
                infoExecutionCourse = (InfoExecutionCourse) argumentos[0];
                executionCourse = (IExecutionCourse) persistentExecutionCourse.readByOID(
                        ExecutionCourse.class, infoExecutionCourse.getIdInternal());
            } else {
                executionCourse = (IExecutionCourse) persistentExecutionCourse.readByOID(
                        ExecutionCourse.class, (Integer) argumentos[0]);
            }
            if (argumentos[1] instanceof InfoCurricularCourse) {
                infoCurricularCourse = (InfoCurricularCourse) argumentos[1];
                curricularCourse = (ICurricularCourse) persistentCurricularCourse.readByOID(
                        CurricularCourse.class, infoCurricularCourse.getIdInternal());
            } else {
                curricularCourse = (ICurricularCourse) persistentCurricularCourse.readByOID(
                        CurricularCourse.class, (Integer) argumentos[1]);
            }

        } catch (Exception e) {
            return false;
        }
        return executionCourse.getAssociatedCurricularCourses().contains(curricularCourse);
    }

    /**
     * @param id
     * @param argumentos
     * @return
     */
    private boolean lecturesExecutionCourse(IUserView id, Object[] argumentos) {
        InfoExecutionCourse infoExecutionCourse = null;
        IExecutionCourse executionCourse = null;
        ISuportePersistente sp;
        IProfessorship professorship = null;
        if (argumentos == null) {
            return false;
        }
        try {

            sp = SuportePersistenteOJB.getInstance();
            IPersistentExecutionCourse persistentExecutionCourse = sp.getIPersistentExecutionCourse();
            if (argumentos[0] instanceof InfoExecutionCourse) {
                infoExecutionCourse = (InfoExecutionCourse) argumentos[0];
                executionCourse = Cloner.copyInfoExecutionCourse2ExecutionCourse(infoExecutionCourse);
            } else {
                executionCourse = (IExecutionCourse) persistentExecutionCourse.readByOID(
                        ExecutionCourse.class, (Integer) argumentos[0]);
            }

            IPersistentTeacher persistentTeacher = sp.getIPersistentTeacher();
            ITeacher teacher = persistentTeacher.readTeacherByUsername(id.getUtilizador());
            if (teacher != null && executionCourse != null) {
                IPersistentProfessorship persistentProfessorship = sp.getIPersistentProfessorship();
                professorship = persistentProfessorship.readByTeacherAndExecutionCoursePB(teacher,
                        executionCourse);
            }
        } catch (Exception e) {
            return false;
        }
        return professorship != null;
    }

    /**
     * @param argumentos
     * @return
     */
    private boolean CurricularCourseAeroDegree(Object[] argumentos) {
        if (argumentos == null) {
            return false;
        }

        InfoCurricularCourse infoCurricularCourse = null;
        ICurricularCourse curricularCourse = null;
        IDegree degree = null;

        ISuportePersistente sp;
        try {

            sp = SuportePersistenteOJB.getInstance();
            IPersistentCurricularCourse persistentCurricularCourse = sp.getIPersistentCurricularCourse();
            if (argumentos[1] instanceof InfoCurricularCourse) {
                infoCurricularCourse = (InfoCurricularCourse) argumentos[1];
                curricularCourse = (ICurricularCourse) persistentCurricularCourse.readByOID(
                        CurricularCourse.class, infoCurricularCourse.getIdInternal());
            } else {
                curricularCourse = (ICurricularCourse) persistentCurricularCourse.readByOID(
                        CurricularCourse.class, (Integer) argumentos[1]);
            }

            degree = curricularCourse.getDegreeCurricularPlan().getDegree();
        } catch (Exception e) {
            return false;
        }
        return degree.getSigla().equals("LEA");//codigo
        // do
        // curso
        // de
        // Aeroespacial
    }

    /**
     * @param argumentos
     * @return
     */
    private boolean CurricularCourseNotBasic(Object[] argumentos) {
        if (argumentos == null) {
            return false;
        }

        InfoCurricularCourse infoCurricularCourse = null;
        ICurricularCourse curricularCourse = null;

        ISuportePersistente sp;
        try {

            sp = SuportePersistenteOJB.getInstance();
            IPersistentCurricularCourse persistentCurricularCourse = sp.getIPersistentCurricularCourse();
            if (argumentos[1] instanceof InfoCurricularCourse) {
                infoCurricularCourse = (InfoCurricularCourse) argumentos[1];
                curricularCourse = (ICurricularCourse) persistentCurricularCourse.readByOID(
                        CurricularCourse.class, infoCurricularCourse.getIdInternal());
            } else {
                curricularCourse = (ICurricularCourse) persistentCurricularCourse.readByOID(
                        CurricularCourse.class, (Integer) argumentos[1]);
            }
        } catch (Exception e) {
            return false;
        }
        return curricularCourse.getBasic().equals(Boolean.FALSE);
    }
}