/*
 * LerTurnosDeTurma.java
 *
 * Created on 28 de Outubro de 2002, 20:26
 */

package ServidorAplicacao.Servico.sop;

/**
 * Servi�o LerTurnosDeTurma
 * 
 * @author tfc130
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pt.utl.ist.berserk.logic.serviceManager.IService;
import DataBeans.InfoExecutionDegree;
import DataBeans.InfoExecutionPeriod;
import DataBeans.InfoLesson;
import DataBeans.InfoShift;
import DataBeans.util.Cloner;
import Dominio.ILesson;
import Dominio.IExecutionDegree;
import Dominio.IExecutionPeriod;
import Dominio.ISchoolClass;
import Dominio.IShift;
import Dominio.SchoolClass;
import ServidorAplicacao.Servico.exceptions.FenixServiceException;
import ServidorPersistente.ExcepcaoPersistencia;
import ServidorPersistente.ISuportePersistente;
import ServidorPersistente.ITurmaTurnoPersistente;
import ServidorPersistente.OJB.SuportePersistenteOJB;

public class LerTurnosDeTurma implements IService {

    public Object run(String className, InfoExecutionDegree infoExecutionDegree,
            InfoExecutionPeriod infoExecutionPeriod) throws FenixServiceException {

        List infoShiftAndLessons = new ArrayList();

        try {
            ISuportePersistente sp = SuportePersistenteOJB.getInstance();

            ITurmaTurnoPersistente classShiftDAO = sp.getITurmaTurnoPersistente();

            IExecutionPeriod executionPeriod = Cloner
                    .copyInfoExecutionPeriod2IExecutionPeriod(infoExecutionPeriod);
            IExecutionDegree executionDegree = Cloner
                    .copyInfoExecutionDegree2ExecutionDegree(infoExecutionDegree);

            ISchoolClass group = new SchoolClass();

            group.setExecutionDegree(executionDegree);
            group.setExecutionPeriod(executionPeriod);
            group.setNome(className);

            List shiftList = classShiftDAO.readByClass(group);

            Iterator iterator = shiftList.iterator();
            //			infoTurnos = new ArrayList();

            while (iterator.hasNext()) {
                IShift turno = (IShift) iterator.next();
                InfoShift infoTurno = (InfoShift) Cloner.get(turno);

                List aulas = turno.getAssociatedLessons();
                Iterator itLessons = aulas.iterator();

                List infoLessons = new ArrayList();
                InfoLesson infoLesson;

                while (itLessons.hasNext()) {
                    infoLesson = Cloner.copyILesson2InfoLesson((ILesson) itLessons.next());

                    infoLesson.setInfoShift(infoTurno);
                    infoLessons.add(infoLesson);
                }

                infoTurno.setInfoLessons(infoLessons);
                infoShiftAndLessons.add(infoTurno);

            }
        } catch (ExcepcaoPersistencia ex) {
            throw new FenixServiceException(ex);
        }
        return infoShiftAndLessons;

    }

}