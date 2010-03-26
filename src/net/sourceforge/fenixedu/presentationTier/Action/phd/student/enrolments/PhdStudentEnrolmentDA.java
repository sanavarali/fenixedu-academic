package net.sourceforge.fenixedu.presentationTier.Action.phd.student.enrolments;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.applicationTier.Filtro.exception.FenixFilterException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.dataTransferObject.phd.student.PhdStudentEnrolmentBean;
import net.sourceforge.fenixedu.dataTransferObject.student.enrollment.bolonha.BolonhaStudentEnrollmentBean;
import net.sourceforge.fenixedu.dataTransferObject.student.enrollment.bolonha.BolonhaStudentOptionalEnrollmentBean;
import net.sourceforge.fenixedu.domain.EnrolmentPeriodInCurricularCourses;
import net.sourceforge.fenixedu.domain.ExecutionSemester;
import net.sourceforge.fenixedu.domain.StudentCurricularPlan;
import net.sourceforge.fenixedu.domain.student.Registration;
import net.sourceforge.fenixedu.presentationTier.Action.student.enrollment.bolonha.BolonhaStudentEnrollmentDispatchAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Forward;
import pt.ist.fenixWebFramework.struts.annotations.Forwards;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/phdStudentEnrolment", module = "student")
@Forwards( {

	@Forward(name = "showWelcome", path = "/phd/student/enrolments/showWelcome.jsp"),

	@Forward(name = "showDegreeModulesToEnrol", path = "/phd/student/enrolments/showDegreeModulesToEnrol.jsp"),

	@Forward(name = "chooseOptionalCurricularCourseToEnrol", path = "/phd/student/enrolments/chooseOptionalCurricularCourseToEnrol.jsp")

})
public class PhdStudentEnrolmentDA extends BolonhaStudentEnrollmentDispatchAction {

    @Override
    protected BolonhaStudentEnrollmentBean createStudentEnrolmentBean(ActionForm form,
	    StudentCurricularPlan studentCurricularPlan, ExecutionSemester executionSemester) {
	return new PhdStudentEnrolmentBean(studentCurricularPlan, executionSemester, getCurricularYearForCurricularCourses(),
		getCurricularRuleLevel(form));
    }

    @Override
    public ActionForward showWelcome(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {

	super.showWelcome(mapping, form, request, response);

	final Registration registration = (Registration) request.getAttribute("registration");
	final ExecutionSemester semester = ExecutionSemester.readActualExecutionSemester();
	final EnrolmentPeriodInCurricularCourses period = registration.getLastDegreeCurricularPlan()
		.getEnrolmentPeriodInCurricularCoursesBy(semester);

	if (period != null) {
	    request.setAttribute("enrolmentPeriod", period);
	}

	return mapping.findForward("showWelcome");
    }

    private void addCompetenceCoursesAvalailableToEnrol(HttpServletRequest request, StudentCurricularPlan studentCurricularPlan) {
	request.setAttribute("competenceCoursesAvailableToEnrol", studentCurricularPlan.getRegistration()
		.getPhdIndividualProgramProcess().getCompetenceCoursesAvailableToEnrol());
    }

    @Override
    protected ActionForward prepareShowDegreeModulesToEnrol(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response, StudentCurricularPlan studentCurricularPlan, ExecutionSemester executionSemester) {

	addCompetenceCoursesAvalailableToEnrol(request, studentCurricularPlan);
	return super.prepareShowDegreeModulesToEnrol(mapping, form, request, response, studentCurricularPlan, executionSemester);
    }

    @Override
    public ActionForward prepareChooseOptionalCurricularCourseToEnrol(ActionMapping mapping, ActionForm form,
	    HttpServletRequest request, HttpServletResponse response) {

	final BolonhaStudentEnrollmentBean bean = getBolonhaStudentEnrollmentBeanFromViewState();
	addCompetenceCoursesAvalailableToEnrol(request, bean.getStudentCurricularPlan());
	return super.prepareChooseOptionalCurricularCourseToEnrol(mapping, form, request, response);
    }

    @Override
    public ActionForward updateParametersToSearchOptionalCurricularCourses(ActionMapping mapping, ActionForm actionForm,
	    HttpServletRequest request, HttpServletResponse response) {

	final BolonhaStudentOptionalEnrollmentBean bean = getBolonhaStudentOptionalEnrollmentBeanFromViewState();
	addCompetenceCoursesAvalailableToEnrol(request, bean.getStudentCurricularPlan());
	return super.updateParametersToSearchOptionalCurricularCourses(mapping, actionForm, request, response);
    }

    @Override
    public ActionForward enrolInOptionalCurricularCourse(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws FenixFilterException, FenixServiceException {

	final BolonhaStudentOptionalEnrollmentBean bean = getBolonhaStudentOptionalEnrollmentBeanFromViewState();
	addCompetenceCoursesAvalailableToEnrol(request, bean.getStudentCurricularPlan());
	return super.enrolInOptionalCurricularCourse(mapping, form, request, response);
    }

    @Override
    public ActionForward prepareChooseCycleCourseGroupToEnrol(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) {
	throw new RuntimeException("error.PhdStudentEnrolmentBean.unsupported.operation");
    }

    @Override
    public ActionForward enrolInCycleCourseGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws FenixFilterException, FenixServiceException {
	throw new RuntimeException("error.PhdStudentEnrolmentBean.unsupported.operation");
    }
}