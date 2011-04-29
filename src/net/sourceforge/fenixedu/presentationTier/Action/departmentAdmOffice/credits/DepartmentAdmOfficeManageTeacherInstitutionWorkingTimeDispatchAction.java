/**
 * Nov 23, 2005
 */
package net.sourceforge.fenixedu.presentationTier.Action.departmentAdmOffice.credits;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.applicationTier.IUserView;
import net.sourceforge.fenixedu.applicationTier.Filtro.exception.FenixFilterException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.domain.Department;
import net.sourceforge.fenixedu.domain.ExecutionSemester;
import net.sourceforge.fenixedu.domain.Teacher;
import net.sourceforge.fenixedu.domain.person.RoleType;
import net.sourceforge.fenixedu.domain.teacher.InstitutionWorkTime;
import net.sourceforge.fenixedu.presentationTier.Action.credits.ManageTeacherInstitutionWorkingTimeDispatchAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import pt.ist.fenixWebFramework.security.UserView;

/**
 * @author Ricardo Rodrigues
 * 
 */

public class DepartmentAdmOfficeManageTeacherInstitutionWorkingTimeDispatchAction extends
	ManageTeacherInstitutionWorkingTimeDispatchAction {

    public ActionForward showTeacherWorkingTimePeriods(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws NumberFormatException, FenixFilterException, FenixServiceException {

	DynaActionForm institutionWorkingTimeForm = (DynaActionForm) form;
	final ExecutionSemester executionSemester = rootDomainObject.readExecutionSemesterByOID(Integer
		.valueOf((String) institutionWorkingTimeForm.get("executionPeriodId")));

	String teacherId = institutionWorkingTimeForm.getString("teacherId");
	Teacher teacher = Teacher.readByIstId(teacherId);

	if (getTeacherOfManageableDepartments(teacherId, executionSemester, request) == null) {
	    request.setAttribute("teacherNotFound", "teacherNotFound");
	    return mapping.findForward("teacher-not-found");
	}

	getInstitutionWokTimeList(request, institutionWorkingTimeForm, executionSemester, teacher);
	return mapping.findForward("list-teacher-institution-working-time");
    }

    public ActionForward prepareEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws NumberFormatException, FenixFilterException, FenixServiceException {

	DynaActionForm institutionWorkingTimeForm = (DynaActionForm) form;
	Integer institutionWorkingTimeID = (Integer) institutionWorkingTimeForm.get("institutionWorkTimeID");
	Integer teacherID = Integer.valueOf(institutionWorkingTimeForm.getString("teacherId"));
	Integer executionPeriodID = Integer.valueOf(institutionWorkingTimeForm.getString("executionPeriodId"));

	Teacher teacher = rootDomainObject.readTeacherByOID(teacherID);
	ExecutionSemester executionSemester = rootDomainObject.readExecutionSemesterByOID(executionPeriodID);

	if (teacher == null || getTeacherOfManageableDepartments(teacher.getPerson().getIstUsername(), executionSemester, request) == null) {
	    return mapping.findForward("teacher-not-found");
	}

	InstitutionWorkTime institutionWorkTime = null;
	if (institutionWorkingTimeID != null && institutionWorkingTimeID != 0) {
	    institutionWorkTime = (InstitutionWorkTime) rootDomainObject.readTeacherServiceItemByOID(institutionWorkingTimeID);
	    if (!teacher.getTeacherServiceByExecutionPeriod(executionSemester).getInstitutionWorkTimes().contains(
		    institutionWorkTime)) {
		return mapping.findForward("teacher-not-found");
	    }
	}

	prepareToEdit(institutionWorkTime, teacher, executionSemester, request, institutionWorkingTimeForm);
	return mapping.findForward("edit-institution-work-time");
    }

    private Teacher getTeacherOfManageableDepartments(String teacherId, ExecutionSemester executionSemester,
	    HttpServletRequest request) {

	IUserView userView = UserView.getUser();
	List<Department> manageableDepartments = userView.getPerson().getManageableDepartmentCredits();
	Teacher teacher = null;
	for (Department department : manageableDepartments) {
	    teacher = department.getTeacherByPeriod(teacherId, executionSemester.getBeginDateYearMonthDay(),
		    executionSemester.getEndDateYearMonthDay());
	    if (teacher != null) {
		break;
	    }
	}
	return teacher;
    }

    public ActionForward editInstitutionWorkingTime(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws NumberFormatException, FenixFilterException, FenixServiceException,
	    InvalidPeriodException {

	editInstitutionWorkingTime(form, request, RoleType.DEPARTMENT_ADMINISTRATIVE_OFFICE);
	return mapping.findForward("successfull-edit");

    }

    public ActionForward deleteInstitutionWorkingTime(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	    HttpServletResponse response) throws NumberFormatException, FenixFilterException, FenixServiceException {

	deleteInstitutionWorkingTime(form, request, RoleType.DEPARTMENT_ADMINISTRATIVE_OFFICE);
	return mapping.findForward("successfull-delete");
    }
}