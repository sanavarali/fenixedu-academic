/*
 * Created on 10/Set/2003, 20:47:24
 *
 *By Goncalo Luiz gedl [AT] rnl [DOT] ist [DOT] utl [DOT] pt
 */
package ServidorAplicacao.Servico.publico;
import java.util.List;

import DataBeans.StudentGroupAttendacyInformation;
import Dominio.DisciplinaExecucao;
import Dominio.IDisciplinaExecucao;
import Dominio.IFrequenta;
import Dominio.IStudent;
import Dominio.IStudentGroupAttend;
import ServidorAplicacao.IServico;
import ServidorApresentacao.Action.Seminaries.Exceptions.BDException;
import ServidorPersistente.ExcepcaoPersistencia;
import ServidorPersistente.IDisciplinaExecucaoPersistente;
import ServidorPersistente.IFrequentaPersistente;
import ServidorPersistente.IPersistentStudentGroupAttend;
import ServidorPersistente.ISuportePersistente;
import ServidorPersistente.OJB.SuportePersistenteOJB;
/**
 * @author Goncalo Luiz gedl [AT] rnl [DOT] ist [DOT] utl [DOT] pt
 *
 * 
 * Created at 10/Set/2003, 20:47:24
 * 
 */
public class GetProjectGroupAttendantsByExecutionCourseIDANDStudentUsername implements IServico
{
	private static GetProjectGroupAttendantsByExecutionCourseIDANDStudentUsername service=
		new GetProjectGroupAttendantsByExecutionCourseIDANDStudentUsername();
	/**
	 * The singleton access method of this class.
	 **/
	public static GetProjectGroupAttendantsByExecutionCourseIDANDStudentUsername getService()
	{
		return service;
	}
	/**
	 * The actor of this class.
	 **/
	private GetProjectGroupAttendantsByExecutionCourseIDANDStudentUsername()
	{
	}
	/**
	 * Returns The Service Name */
	public final String getNome()
	{
		return "publico.GetProjectGroupAttendantsByExecutionCourseIDANDStudentUsername";
	}
	public StudentGroupAttendacyInformation run(Integer executionCourseID, String username) throws BDException
	{
		try
		{
			ISuportePersistente persistenceSupport= SuportePersistenteOJB.getInstance();
			IFrequentaPersistente persistentAttendacy= persistenceSupport.getIFrequentaPersistente();
			IStudent student= persistenceSupport.getIPersistentStudent().readByUsername(username);
			IPersistentStudentGroupAttend persistentStudentGroupAttend=
				persistenceSupport.getIPersistentStudentGroupAttend();
			//
			IDisciplinaExecucaoPersistente persistentExecutionCourse=
				persistenceSupport.getIDisciplinaExecucaoPersistente();
			//
			IDisciplinaExecucao executionCourse=
				(IDisciplinaExecucao) persistentExecutionCourse.readByOID(
					DisciplinaExecucao.class,
					executionCourseID);
			//
            //
			IFrequenta attendacy= persistentAttendacy.readByAlunoAndDisciplinaExecucao(student, executionCourse);
            if (attendacy == null)
                return null; // the student is not enrolled on this course
            IStudentGroupAttend groupAttend = persistentStudentGroupAttend.readBy(attendacy);
            StudentGroupAttendacyInformation info = new StudentGroupAttendacyInformation();
            if (groupAttend == null)
                return null; // the student has not a group, at least at this course 
            info.setShiftName(groupAttend.getStudentGroup().getShift().getNome());
            List lessons = groupAttend.getStudentGroup().getShift().getAssociatedLessons();
            info.setDegreesNames(executionCourse.getAssociatedCurricularCourses());
            info.setLessons(lessons);
            info.setGroupNumber(groupAttend.getStudentGroup().getGroupNumber());
            List groupAttends = persistentStudentGroupAttend.readByStudentGroupId(groupAttend.getKeyStudentGroup());
            info.setGroupAttends(groupAttends);
                                
            return info; 
		}
		catch (ExcepcaoPersistencia ex)
		{
			throw new BDException("Got an error while trying to get info about a student's work group", ex);
		}
	}
}
