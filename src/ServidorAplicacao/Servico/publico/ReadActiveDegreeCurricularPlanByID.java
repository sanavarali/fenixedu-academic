package ServidorAplicacao.Servico.publico;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;

import DataBeans.InfoCurricularCourse;
import DataBeans.InfoCurricularCourseScope;
import DataBeans.InfoCurricularYear;
import DataBeans.InfoDegree;
import DataBeans.InfoDegreeCurricularPlan;
import DataBeans.InfoExecutionDegree;
import DataBeans.InfoExecutionPeriod;
import Dominio.DegreeCurricularPlan;
import Dominio.ExecutionPeriod;
import Dominio.IExecutionDegree;
import Dominio.IDegreeCurricularPlan;
import Dominio.IExecutionPeriod;
import ServidorAplicacao.Servico.coordinator.ReadDegreeCurricularPlanBaseService;
import ServidorAplicacao.Servico.exceptions.FenixServiceException;
import ServidorPersistente.ExcepcaoPersistencia;
import ServidorPersistente.IPersistentDegreeCurricularPlan;
import ServidorPersistente.IPersistentExecutionPeriod;
import ServidorPersistente.ISuportePersistente;
import ServidorPersistente.OJB.SuportePersistenteOJB;

/**
 * @author Fernanda Quit�rio 5/Nov/2003
 *  
 */
public class ReadActiveDegreeCurricularPlanByID extends ReadDegreeCurricularPlanBaseService {

    public ReadActiveDegreeCurricularPlanByID() {

    }

    public List run(Integer degreeCurricularPlanCode, Integer executionPeriodCode)
            throws FenixServiceException {
        if (degreeCurricularPlanCode == null) {
            throw new FenixServiceException("nullDegree");
        }

        ISuportePersistente sp;
        try {
            sp = SuportePersistenteOJB.getInstance();

            IPersistentDegreeCurricularPlan persistentDegreeCurricularPlan = sp
                    .getIPersistentDegreeCurricularPlan();

            IDegreeCurricularPlan degreeCurricularPlan = (IDegreeCurricularPlan) persistentDegreeCurricularPlan
                    .readByOID(DegreeCurricularPlan.class, degreeCurricularPlanCode);

            if (degreeCurricularPlan == null) {
                throw new FenixServiceException("nullDegree");
            }

            if (executionPeriodCode == null) {
                return groupScopesByCurricularYearAndCurricularCourse(super
                        .readActiveCurricularCourseScopes(degreeCurricularPlan, sp));
            }

            IPersistentExecutionPeriod persistentExecutionPeriod = sp.getIPersistentExecutionPeriod();
            IExecutionPeriod executionPeriod = (IExecutionPeriod) persistentExecutionPeriod.readByOID(
                    ExecutionPeriod.class, executionPeriodCode);
            if (executionPeriod == null || executionPeriod.getExecutionYear() == null) {
                throw new FenixServiceException("nullDegree");
            }

            return groupScopesByCurricularYearAndCurricularCourse(super
                    .readActiveCurricularCourseScopesInExecutionYear(degreeCurricularPlan,
                            executionPeriod.getExecutionYear(), sp));

        } catch (ExcepcaoPersistencia e) {

            throw new FenixServiceException("Problems on database!", e);
        }
    }

    public List run(InfoExecutionDegree infoExecutionDegree, InfoExecutionPeriod infoExecutionPeriod,
            Integer curricularYear) throws FenixServiceException {

        IExecutionDegree executionDegree = InfoExecutionDegree.newDomainFromInfo(infoExecutionDegree);
        if (executionDegree != null) {
            executionDegree.setCurricularPlan(InfoDegreeCurricularPlan
                    .newDomainFromInfo(infoExecutionDegree.getInfoDegreeCurricularPlan()));
            executionDegree.getCurricularPlan().setDegree(
                    InfoDegree.newDomainFromInfo(infoExecutionDegree.getInfoDegreeCurricularPlan()
                            .getInfoDegree()));
        }
        IExecutionPeriod executionPeriod = InfoExecutionPeriod.newDomainFromInfo(infoExecutionPeriod);
        IDegreeCurricularPlan degreeCurricularPlan = executionDegree.getCurricularPlan();
        if (degreeCurricularPlan == null) {
            throw new FenixServiceException("nullDegree");
        }

        ISuportePersistente sp;
        try {
            sp = SuportePersistenteOJB.getInstance();

            IPersistentDegreeCurricularPlan persistentDegreeCurricularPlan = sp
                    .getIPersistentDegreeCurricularPlan();

            degreeCurricularPlan = (IDegreeCurricularPlan) persistentDegreeCurricularPlan.readByOID(
                    DegreeCurricularPlan.class, degreeCurricularPlan.getIdInternal());
            if (degreeCurricularPlan == null) {
                throw new FenixServiceException("nullDegree");
            }

            if (executionPeriod == null) {
                return groupScopesByCurricularYearAndCurricularCourse(super
                        .readActiveCurricularCourseScopes(degreeCurricularPlan, sp));
            }

            IPersistentExecutionPeriod persistentExecutionPeriod = sp.getIPersistentExecutionPeriod();
            executionPeriod = (IExecutionPeriod) persistentExecutionPeriod.readByOID(
                    ExecutionPeriod.class, executionPeriod.getIdInternal());
            if (executionPeriod == null || executionPeriod.getExecutionYear() == null) {
                throw new FenixServiceException("nullDegree");
            }
            //		return
            // super.readActiveCurricularCourseScopesInCurricularYearAndExecutionPeriodAndExecutionDegree(executionPeriod,executionDegree,curricularYear,
            // sp);
            return groupScopesByCurricularYearAndCurricularCourse(super
                    .readActiveCurricularCourseScopesInCurricularYearAndExecutionPeriodAndExecutionDegree(
                            executionPeriod, executionDegree, curricularYear, sp));

        } catch (ExcepcaoPersistencia e) {

            throw new FenixServiceException("Problems on database!", e);
        }
    }

    private List groupScopesByCurricularYearAndCurricularCourse(List scopes) {
        List result = new ArrayList();
        List temp = new ArrayList();

        ComparatorChain comparatorChain = new ComparatorChain();
        comparatorChain.addComparator(new BeanComparator(
                "infoCurricularSemester.infoCurricularYear.year"));
        comparatorChain.addComparator(new BeanComparator("infoCurricularSemester.semester"));
        comparatorChain.addComparator(new BeanComparator("infoCurricularCourse.name"));
        Collections.sort(scopes, comparatorChain);

        if (scopes != null && scopes.size() > 0) {
            ListIterator iter = scopes.listIterator();
            InfoCurricularYear year = null;
            InfoCurricularCourse curricularCourse = null;

            while (iter.hasNext()) {
                InfoCurricularCourseScope scope = (InfoCurricularCourseScope) iter.next();
                InfoCurricularYear scopeYear = scope.getInfoCurricularSemester().getInfoCurricularYear();
                InfoCurricularCourse scopeCurricularCourse = scope.getInfoCurricularCourse();
                if (year == null) {
                    year = scopeYear;
                }
                if (curricularCourse == null) {
                    curricularCourse = scopeCurricularCourse;
                }

                if (scopeYear.equals(year) && scopeCurricularCourse.equals(curricularCourse)) {
                    temp.add(scope);
                } else {
                    result.add(temp);
                    temp = new ArrayList();
                    year = scopeYear;
                    curricularCourse = scopeCurricularCourse;
                    temp.add(scope);
                }

                if (!iter.hasNext()) {
                    result.add(temp);
                }
            }
        }

        return result;
    }

    public InfoDegreeCurricularPlan run(Integer degreeCurricularPlanCode, Integer executionYear,
            String arg) throws FenixServiceException {

        ISuportePersistente sp;
        InfoDegreeCurricularPlan infoDegreeCurricularPlan = new InfoDegreeCurricularPlan();
        try {
            sp = SuportePersistenteOJB.getInstance();

            IPersistentDegreeCurricularPlan persistentDegreeCurricularPlan = sp
                    .getIPersistentDegreeCurricularPlan();

            IDegreeCurricularPlan degreeCurricularPlan = (IDegreeCurricularPlan) persistentDegreeCurricularPlan
                    .readByOID(DegreeCurricularPlan.class, degreeCurricularPlanCode);

            if (degreeCurricularPlan != null) {
                infoDegreeCurricularPlan = InfoDegreeCurricularPlan
                        .newInfoFromDomain(degreeCurricularPlan);

            }
            if (degreeCurricularPlan == null) {
                throw new FenixServiceException("nullDegree");
            }
        } catch (ExcepcaoPersistencia e) {

            throw new FenixServiceException("Problems on database!", e);
        }

        return infoDegreeCurricularPlan;
    }
}