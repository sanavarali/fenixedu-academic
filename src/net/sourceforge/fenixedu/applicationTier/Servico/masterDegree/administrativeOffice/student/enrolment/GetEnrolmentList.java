/*
 * Created on 14/Mar/2003
 *
 */

package net.sourceforge.fenixedu.applicationTier.Servico.masterDegree.administrativeOffice.student.enrolment;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.dataTransferObject.InfoEnrolment;
import net.sourceforge.fenixedu.dataTransferObject.InfoEnrolmentWithStudentPlanAndCourseAndEvaluationsAndExecutionPeriod;
import net.sourceforge.fenixedu.dataTransferObject.InfoEnrolmentWithStudentPlanAndCourseAndExecutionPeriod;
import net.sourceforge.fenixedu.domain.IEnrolment;
import net.sourceforge.fenixedu.domain.IEnrolmentInExtraCurricularCourse;
import net.sourceforge.fenixedu.domain.IStudentCurricularPlan;
import net.sourceforge.fenixedu.domain.StudentCurricularPlan;
import net.sourceforge.fenixedu.domain.curriculum.CurricularCourseType;
import net.sourceforge.fenixedu.domain.curriculum.EnrollmentState;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.ISuportePersistente;
import net.sourceforge.fenixedu.persistenceTier.PersistenceSupportFactory;

import org.apache.ojb.broker.core.proxy.ProxyHelper;

import pt.utl.ist.berserk.logic.serviceManager.IService;

/**
 * @author Nuno Nunes (nmsn@rnl.ist.utl.pt) Joana Mota (jccm@rnl.ist.utl.pt)
 */
public class GetEnrolmentList implements IService {

	public List run(Integer studentCurricularPlanID, EnrollmentState enrollmentState)
			throws FenixServiceException, Exception {

		ISuportePersistente sp = null;
		List enrolmentList = null;

		sp = PersistenceSupportFactory.getDefaultPersistenceSupport();

		// Read the list

		enrolmentList = sp.getIPersistentEnrolment()
				.readEnrolmentsByStudentCurricularPlanAndEnrolmentState(studentCurricularPlanID,
						enrollmentState);

		List result = new ArrayList();
		Iterator iterator = enrolmentList.iterator();

		while (iterator.hasNext()) {
			IEnrolment enrolment = (IEnrolment) iterator.next();

			if (enrolment instanceof Proxy) {
				enrolment = (IEnrolment) ProxyHelper.getRealObject(enrolment);
			}

			if (enrolment instanceof IEnrolmentInExtraCurricularCourse) {
				continue;
			}

			if (!enrolment.getCurricularCourse().getType().equals(CurricularCourseType.P_TYPE_COURSE)) {
				InfoEnrolment infoEnrolment = InfoEnrolmentWithStudentPlanAndCourseAndEvaluationsAndExecutionPeriod
						.newInfoFromDomain(enrolment);
				result.add(infoEnrolment);
			}
		}

		return result;
	}

	public List run(Integer studentCurricularPlanID) throws FenixServiceException, Exception {

		ISuportePersistente sp = null;
		List enrolmentList = null;

		sp = PersistenceSupportFactory.getDefaultPersistenceSupport();

		// Read the list

		IStudentCurricularPlan studentCurricularPlan = (IStudentCurricularPlan) sp
				.getIStudentCurricularPlanPersistente().readByOID(StudentCurricularPlan.class,
						studentCurricularPlanID);
		enrolmentList = studentCurricularPlan.getEnrolments();

		List result = new ArrayList();
		Iterator iterator = enrolmentList.iterator();

		while (iterator.hasNext()) {
			IEnrolment enrolment = (IEnrolment) iterator.next();

			if (enrolment instanceof Proxy) {
				enrolment = (IEnrolment) ProxyHelper.getRealObject(enrolment);
			}

			if (enrolment instanceof IEnrolmentInExtraCurricularCourse) {
				continue;
			}

			if (!enrolment.getCurricularCourse().getType().equals(CurricularCourseType.P_TYPE_COURSE)) {
				InfoEnrolment infoEnrolment = InfoEnrolmentWithStudentPlanAndCourseAndExecutionPeriod
						.newInfoFromDomain(enrolment);
				result.add(infoEnrolment);
			}
		}

		return result;
	}

	public List run(Integer studentCurricularPlanID, EnrollmentState enrollmentState,
			Boolean pTypeEnrolments) throws FenixServiceException, Exception {

		if (!pTypeEnrolments.booleanValue()) {
			return this.run(studentCurricularPlanID, enrollmentState);
		}

		ISuportePersistente sp = PersistenceSupportFactory.getDefaultPersistenceSupport();

		// Read the list
		List enrolmentList = sp.getIPersistentEnrolment()
				.readEnrolmentsByStudentCurricularPlanAndEnrolmentState(studentCurricularPlanID,
						enrollmentState);

		// clone
		List result = new ArrayList();
		for (Iterator iter = enrolmentList.iterator(); iter.hasNext();) {
			IEnrolment enrolment = (IEnrolment) iter.next();

			if (enrolment instanceof Proxy) {
				enrolment = (IEnrolment) ProxyHelper.getRealObject(enrolment);
			}

			if (enrolment instanceof IEnrolmentInExtraCurricularCourse) {
				continue;
			}

			InfoEnrolment infoEnrolment = InfoEnrolmentWithStudentPlanAndCourseAndEvaluationsAndExecutionPeriod
					.newInfoFromDomain(enrolment);
			result.add(infoEnrolment);
		}

		return result;
	}

}