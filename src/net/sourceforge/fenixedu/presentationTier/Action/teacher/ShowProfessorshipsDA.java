package net.sourceforge.fenixedu.presentationTier.Action.teacher;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.fenixedu.applicationTier.IUserView;
import net.sourceforge.fenixedu.domain.ExecutionCourse;
import net.sourceforge.fenixedu.domain.ExecutionPeriod;
import net.sourceforge.fenixedu.domain.Person;
import net.sourceforge.fenixedu.domain.Professorship;
import net.sourceforge.fenixedu.domain.Teacher;
import net.sourceforge.fenixedu.presentationTier.Action.base.FenixDispatchAction;
import net.sourceforge.fenixedu.presentationTier.util.LabelValueBeanUtils;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

public class ShowProfessorshipsDA extends FenixDispatchAction {

    public ActionForward list(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
    		throws Exception {

    	final IUserView userView = getUserView(request);
    	final DynaActionForm dynaActionForm = (DynaActionForm) form;
    	final String executionPeriodIDString = request.getParameter("executionPeriodID");

    	final SortedSet<ExecutionPeriod> executionPeriods = new TreeSet<ExecutionPeriod>();

    	final ExecutionPeriod selectedExecutionPeriod;
    	if (executionPeriodIDString == null) {
    		selectedExecutionPeriod = ExecutionPeriod.readActualExecutionPeriod();
    		executionPeriods.add(selectedExecutionPeriod);
    		dynaActionForm.set("executionPeriodID", selectedExecutionPeriod.getIdInternal().toString());
    	} else if (executionPeriodIDString.length() > 0) {
    		selectedExecutionPeriod = rootDomainObject.readExecutionPeriodByOID(Integer.valueOf(executionPeriodIDString));
    		dynaActionForm.set("executionPeriodID", selectedExecutionPeriod.getIdInternal().toString());
    	} else {
    		selectedExecutionPeriod = null;
    		dynaActionForm.set("executionPeriodID", "");
    	}

    	final SortedSet<ExecutionCourse> executionCourses = new TreeSet<ExecutionCourse>(ExecutionCourse.EXECUTION_COURSE_COMPARATOR_BY_EXECUTION_PERIOD_AND_NAME);
    	request.setAttribute("executionCourses", executionCourses);

    	if (userView != null) {
    		final Person person = userView.getPerson();
    		if (person != null) {
    			final Teacher teacher = person.getTeacher();
    			if (teacher != null) {
    				for (final Professorship professorship : teacher.getProfessorshipsSet()) {
    					final ExecutionCourse executionCourse = professorship.getExecutionCourse();
    					final ExecutionPeriod executionPeriod = executionCourse.getExecutionPeriod();

    					executionPeriods.add(executionPeriod);
    					if (selectedExecutionPeriod == null || selectedExecutionPeriod == executionPeriod) {
    						executionCourses.add(executionCourse);
    					}
    				}
    			}
    		}
    	}

    	request.setAttribute("executionPeriodLabelValueBeans", LabelValueBeanUtils.executionPeriodLabelValueBeans(executionPeriods, true));

        return mapping.findForward("list");
    }

}