/*
 * Created on 14/Ago/2003
 */
package ServidorAplicacao.Servico.manager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pt.utl.ist.berserk.logic.serviceManager.IService;
import DataBeans.InfoExecutionDegree;
import DataBeans.InfoPeriod;
import Dominio.Campus;
import Dominio.ExecutionDegree;
import Dominio.DegreeCurricularPlan;
import Dominio.ExecutionYear;
import Dominio.ICampus;
import Dominio.IExecutionDegree;
import Dominio.IDegreeCurricularPlan;
import Dominio.IExecutionYear;
import Dominio.IPeriod;
import Dominio.Period;
import ServidorAplicacao.Servico.exceptions.ExistingServiceException;
import ServidorAplicacao.Servico.exceptions.FenixServiceException;
import ServidorAplicacao.Servico.exceptions.NonExistingServiceException;
import ServidorPersistente.ExcepcaoPersistencia;
import ServidorPersistente.IPersistentDegreeCurricularPlan;
import ServidorPersistente.IPersistentExecutionDegree;
import ServidorPersistente.IPersistentExecutionYear;
import ServidorPersistente.IPersistentPeriod;
import ServidorPersistente.ISuportePersistente;
import ServidorPersistente.OJB.SuportePersistenteOJB;
import ServidorPersistente.exceptions.ExistingPersistentException;
import ServidorPersistente.places.campus.IPersistentCampus;

/**
 * @author lmac1
 */
public class InsertExecutionDegreeAtDegreeCurricularPlan implements IService {

    public void run(InfoExecutionDegree infoExecutionDegree) throws FenixServiceException {

        IExecutionYear executionYear = null;

        try {
            ISuportePersistente persistentSuport = SuportePersistenteOJB.getInstance();

            IPersistentCampus campusDAO = persistentSuport.getIPersistentCampus();
            IPersistentDegreeCurricularPlan persistentDegreeCurricularPlan = persistentSuport
                    .getIPersistentDegreeCurricularPlan();

            ICampus campus = (ICampus) campusDAO.readByOID(Campus.class, infoExecutionDegree
                    .getInfoCampus().getIdInternal());
            if (campus == null) {
                throw new NonExistingServiceException("message.nonExistingCampus", null);
            }

            IDegreeCurricularPlan degreeCurricularPlan = (IDegreeCurricularPlan) persistentDegreeCurricularPlan
                    .readByOID(DegreeCurricularPlan.class, infoExecutionDegree
                            .getInfoDegreeCurricularPlan().getIdInternal());

            if (degreeCurricularPlan == null) {
                throw new NonExistingServiceException("message.nonExistingDegreeCurricularPlan", null);
            }
            IPersistentExecutionYear persistentExecutionYear = persistentSuport
                    .getIPersistentExecutionYear();

            executionYear = (IExecutionYear) persistentExecutionYear.readByOID(ExecutionYear.class,
                    infoExecutionDegree.getInfoExecutionYear().getIdInternal());

            if (executionYear == null) {

                throw new NonExistingServiceException("message.non.existing.execution.year", null);
            }

            IPersistentExecutionDegree persistentExecutionDegree = persistentSuport
                    .getIPersistentExecutionDegree();

            IExecutionDegree executionDegree = new ExecutionDegree();
            persistentExecutionDegree.simpleLockWrite(executionDegree);
            executionDegree.setCurricularPlan(degreeCurricularPlan);
            executionDegree.setExecutionYear(executionYear);
            executionDegree.setTemporaryExamMap(infoExecutionDegree.getTemporaryExamMap());
            executionDegree.setCampus(campus);

            setPeriods(executionDegree, infoExecutionDegree);

        } catch (ExistingPersistentException existingException) {
            throw new ExistingServiceException(
                    "O curso em execu��o referente ao ano lectivo em execu��o "
                            + executionYear.getYear(), existingException);
        } catch (ExcepcaoPersistencia excepcaoPersistencia) {
            throw new FenixServiceException(excepcaoPersistencia);
        }
    }

    private void setPeriods(IExecutionDegree executionDegree, InfoExecutionDegree infoExecutionDegree)
            throws FenixServiceException {
        InfoPeriod infoPeriodExamsFirstSemester = infoExecutionDegree.getInfoPeriodExamsFirstSemester();
        setCompositePeriod(executionDegree, infoPeriodExamsFirstSemester, 11);

        InfoPeriod infoPeriodExamsSecondSemester = infoExecutionDegree
                .getInfoPeriodExamsSecondSemester();
        setCompositePeriod(executionDegree, infoPeriodExamsSecondSemester, 12);

        InfoPeriod infoPeriodLessonsFirstSemester = infoExecutionDegree
                .getInfoPeriodLessonsFirstSemester();
        setCompositePeriod(executionDegree, infoPeriodLessonsFirstSemester, 21);

        InfoPeriod infoPeriodLessonsSecondSemester = infoExecutionDegree
                .getInfoPeriodLessonsSecondSemester();
        setCompositePeriod(executionDegree, infoPeriodLessonsSecondSemester, 22);
    }

    private void setCompositePeriod(IExecutionDegree executionDegree, InfoPeriod infoPeriod,
            int periodToAssociateExecutionDegree) throws FenixServiceException {
        try {
            ISuportePersistente persistentSuport = SuportePersistenteOJB.getInstance();
            IPersistentPeriod periodDAO = persistentSuport.getIPersistentPeriod();
            List infoPeriodList = new ArrayList();

            infoPeriodList.add(infoPeriod);

            while (infoPeriod.getNextPeriod() != null) {
                infoPeriodList.add(infoPeriod.getNextPeriod());
                infoPeriod = infoPeriod.getNextPeriod();
            }

            //inicializacao
            int infoPeriodListSize = infoPeriodList.size();

            InfoPeriod infoPeriodNew = (InfoPeriod) infoPeriodList.get(infoPeriodListSize - 1);

            IPeriod period = (IPeriod) periodDAO.readByCalendarAndNextPeriod(infoPeriodNew
                    .getStartDate(), infoPeriodNew.getEndDate(), null);

            if (period == null) {
                Calendar startDate = infoPeriodNew.getStartDate();
                Calendar endDate = infoPeriodNew.getEndDate();
                period = new Period(startDate, endDate);
                periodDAO.simpleLockWrite(period);
            }

            //iteracoes
            for (int i = infoPeriodListSize - 2; i >= 0; i--) {
                Integer keyNextPeriod = period.getIdInternal();

                IPeriod nextPeriod = period;

                infoPeriodNew = (InfoPeriod) infoPeriodList.get(i);

                period = (IPeriod) periodDAO.readByCalendarAndNextPeriod(infoPeriodNew.getStartDate(),
                        infoPeriodNew.getEndDate(), keyNextPeriod);

                if (period == null) {
                    Calendar startDate = infoPeriodNew.getStartDate();
                    Calendar endDate = infoPeriodNew.getEndDate();
                    period = new Period(startDate, endDate);
                    periodDAO.simpleLockWrite(period);
                    period.setNextPeriod(nextPeriod);
                }
            }

            if (periodToAssociateExecutionDegree == 11) {
                executionDegree.setPeriodExamsFirstSemester(period);
            } else if (periodToAssociateExecutionDegree == 12) {
                executionDegree.setPeriodExamsSecondSemester(period);
            } else if (periodToAssociateExecutionDegree == 21) {
                executionDegree.setPeriodLessonsFirstSemester(period);
            } else if (periodToAssociateExecutionDegree == 22) {
                executionDegree.setPeriodLessonsSecondSemester(period);
            }

        } catch (ExcepcaoPersistencia excepcaoPersistencia) {
            throw new FenixServiceException(excepcaoPersistencia);
        }
    }

}