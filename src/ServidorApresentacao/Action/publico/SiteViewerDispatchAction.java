package ServidorApresentacao.Action.publico;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import DataBeans.ExecutionCourseSiteView;
import DataBeans.ISiteComponent;
import DataBeans.InfoSiteAnnouncement;
import DataBeans.InfoSiteAssociatedCurricularCourses;
import DataBeans.InfoSiteBibliography;
import DataBeans.InfoSiteCommon;
import DataBeans.InfoSiteCurricularCourse;
import DataBeans.InfoSiteEvaluation;
import DataBeans.InfoSiteEvaluationMethods;
import DataBeans.InfoSiteFirstPage;
import DataBeans.InfoSiteGroupsByShift;
import DataBeans.InfoSiteObjectives;
import DataBeans.InfoSiteProgram;
import DataBeans.InfoSiteProjectShifts;
import DataBeans.InfoSiteProjects;
import DataBeans.InfoSiteRoomTimeTable;
import DataBeans.InfoSiteSection;
import DataBeans.InfoSiteShifts;
import DataBeans.InfoSiteStudentGroup;
import DataBeans.InfoSiteSummaries;
import DataBeans.InfoSiteTimetable;
import DataBeans.RoomKey;
import DataBeans.SiteView;
import ServidorAplicacao.Servico.exceptions.FenixServiceException;
import ServidorAplicacao.Servico.exceptions.NonExistingServiceException;
import ServidorApresentacao.Action.base.FenixContextDispatchAction;
import ServidorApresentacao.Action.exceptions.FenixActionException;
import ServidorApresentacao.Action.exceptions.NonExistingActionException;
import ServidorApresentacao.Action.sop.utils.ServiceUtils;

public class SiteViewerDispatchAction extends FenixContextDispatchAction {

	public ActionForward firstPage(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent firstPageComponent = new InfoSiteFirstPage();
		String objectCodeString = request.getParameter("objectCode");
		if (objectCodeString == null) {
			objectCodeString = (String) request.getAttribute("objectCode");
		}
		Integer infoExecutionCourseCode = new Integer(objectCodeString);

		readSiteView(
			request,
			firstPageComponent,
			infoExecutionCourseCode,
			null,
			null);
		return mapping.findForward("sucess");
	}

	public ActionForward announcements(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent announcementsComponent = new InfoSiteAnnouncement();
		readSiteView(request, announcementsComponent, null, null, null);

		return mapping.findForward("sucess");
	}

	public ActionForward objectives(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent objectivesComponent = new InfoSiteObjectives();
		readSiteView(request, objectivesComponent, null, null, null);

		return mapping.findForward("sucess");
	}

	public ActionForward program(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent programComponent = new InfoSiteProgram();
		readSiteView(request, programComponent, null, null, null);

		return mapping.findForward("sucess");
	}

	public ActionForward evaluationMethod(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent evaluationComponent = new InfoSiteEvaluationMethods();
		readSiteView(request, evaluationComponent, null, null, null);

		return mapping.findForward("sucess");
	}

	public ActionForward bibliography(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent bibliographyComponent = new InfoSiteBibliography();
		readSiteView(request, bibliographyComponent, null, null, null);

		return mapping.findForward("sucess");
	}

	public ActionForward curricularCourses(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent curricularCoursesComponent =
			new InfoSiteAssociatedCurricularCourses();
		readSiteView(request, curricularCoursesComponent, null, null, null);

		return mapping.findForward("sucess");
	}

	public ActionForward timeTable(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent timeTableComponent = new InfoSiteTimetable();
		readSiteView(request, timeTableComponent, null, null, null);

		return mapping.findForward("sucess");
	}

	public ActionForward shifts(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent shiftsComponent = new InfoSiteShifts();
		readSiteView(request, shiftsComponent, null, null, null);

		return mapping.findForward("sucess");
	}

	public ActionForward evaluation(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent evaluationComponent = new InfoSiteEvaluation();
		readSiteView(request, evaluationComponent, null, null, null);

		return mapping.findForward("sucess");
	}

	public ActionForward section(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		String indexString = (String) request.getParameter("index");
		Integer sectionIndex = new Integer(indexString);

		ISiteComponent sectionComponent = new InfoSiteSection();
		readSiteView(request, sectionComponent, null, sectionIndex, null);

		return mapping.findForward("sucess");
	}

	public ActionForward summaries(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		ISiteComponent summariesComponent = new InfoSiteSummaries();
		readSiteView(request, summariesComponent, null, null, null);

		return mapping.findForward("sucess");

	}
	
	public ActionForward curricularCourse(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws FenixActionException {

		String curricularCourseIdString =
			(String) request.getParameter("ccCode");
		if (curricularCourseIdString == null) {
			curricularCourseIdString = (String) request.getAttribute("ccCode");
		}
		Integer curricularCourseId = new Integer(curricularCourseIdString);
		ISiteComponent curricularCourseComponent =
			new InfoSiteCurricularCourse();
		readSiteView(
			request,
			curricularCourseComponent,
			null,
			null,
			curricularCourseId);

		return mapping.findForward("sucess");

	}

	
	public ActionForward viewExecutionCourseProjects(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws FenixActionException {
			
			
			ISiteComponent viewProjectsComponent = new InfoSiteProjects();
			readGroupView(request, viewProjectsComponent,null, null, null, null,null);		
			return mapping.findForward("sucess");
		
				
			
	}


	public ActionForward viewProjectShiftsAction(
				ActionMapping mapping,
				ActionForm form,
				HttpServletRequest request,
				HttpServletResponse response)
				throws FenixActionException {

				String objectCodeString =null;
				Integer groupPropertiesCode =null;
				objectCodeString =(String)request.getParameter("groupProperties");
				if(objectCodeString==null)
					groupPropertiesCode = (Integer)request.getAttribute("groupProperties");
				
				else
					groupPropertiesCode = new Integer(objectCodeString);
			
				ISiteComponent viewProjectShifts = new InfoSiteProjectShifts();
				ExecutionCourseSiteView siteView = (ExecutionCourseSiteView)readGroupView(request, viewProjectShifts,null,null, null,groupPropertiesCode,null);
				request.setAttribute("groupProperties",groupPropertiesCode);
					
				return mapping.findForward("sucess");	
			}
	

	public ActionForward viewStudentGroupsAction(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws FenixActionException {
				
				
			
			String objectCodeString =null;
			Integer groupPropertiesCode =null;
			objectCodeString =(String)request.getParameter("groupProperties");
			
			if(objectCodeString==null)
				groupPropertiesCode = (Integer)request.getAttribute("groupProperties");
				
			else
				groupPropertiesCode = new Integer(objectCodeString);
			
			String shiftCodeString =(String)request.getParameter("shiftCode");
			
			Integer shiftCode = new Integer(shiftCodeString);
			
			ISiteComponent viewGroupsByShift = new InfoSiteGroupsByShift();
			ExecutionCourseSiteView siteView = (ExecutionCourseSiteView)readGroupView(request, viewGroupsByShift,null,null, null,groupPropertiesCode,shiftCode);
			
			request.setAttribute("groupProperties",groupPropertiesCode);
			request.setAttribute("shiftCode",shiftCode);
					
			return mapping.findForward("sucess");	
		}
	
	public ActionForward viewStudentGroupInformationAction(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response)
			throws FenixActionException {

			String studentGroupCodeString =(String)request.getParameter("studentGroupCode");
			
			Integer studentGroupCode = new Integer(studentGroupCodeString);
			
			ISiteComponent viewStudentGroup = new InfoSiteStudentGroup();
			
			ExecutionCourseSiteView siteView = (ExecutionCourseSiteView)readGroupView(request, viewStudentGroup,null,null, null, null,studentGroupCode);
				
			return mapping.findForward("sucess");	
	
	
		}

	private void readSiteView(
		HttpServletRequest request,
		ISiteComponent firstPageComponent,
		Integer infoExecutionCourseCode,
		Integer sectionIndex,
		Integer curricularCourseId)
		throws FenixActionException {
		
		Integer objectCode = null;
		if (infoExecutionCourseCode == null) {
			String objectCodeString = request.getParameter("objectCode");
			if (objectCodeString == null) {
				objectCodeString = (String) request.getAttribute("objectCode");

			}
			objectCode = new Integer(objectCodeString);

		}

		ISiteComponent commonComponent = new InfoSiteCommon();

		Object[] args =
			{
				commonComponent,
				firstPageComponent,
				objectCode,
				infoExecutionCourseCode,
				sectionIndex,
				curricularCourseId };

		try {
			ExecutionCourseSiteView siteView =
				(ExecutionCourseSiteView) ServiceUtils.executeService(
					null,
					"ExecutionCourseSiteComponentService",
					args);
			if (infoExecutionCourseCode != null) {
				request.setAttribute(
					"objectCode",
					((InfoSiteFirstPage) siteView.getComponent())
						.getSiteIdInternal());
			} else {
				request.setAttribute("objectCode", objectCode);
			}
			request.setAttribute("siteView", siteView);
			request.setAttribute(
				"executionCourseCode",
				((InfoSiteCommon) siteView.getCommonComponent())
					.getExecutionCourse()
					.getIdInternal());
			request.setAttribute(
				"executionPeriodCode",
				((InfoSiteCommon) siteView.getCommonComponent())
					.getExecutionCourse()
					.getInfoExecutionPeriod()
					.getIdInternal());
			if (siteView.getComponent() instanceof InfoSiteSection) {
				request.setAttribute(
					"infoSection",
					((InfoSiteSection) siteView.getComponent()).getSection());
			}

		} catch (NonExistingServiceException e) {
			throw new NonExistingActionException("A disciplina", e);
		} catch (FenixServiceException e) {
			throw new FenixActionException(e);
		}
	}

	public ActionForward roomViewer(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response)
		throws Exception {

		String roomName = (String) request.getParameter("roomName");
		if (roomName == null) {
			roomName = (String) request.getAttribute("roomName");
		}

		RoomKey roomKey = null;

		if (roomName != null) {

			roomKey = new RoomKey(roomName);

			Integer objectCode = null;
			String objectCodeString = request.getParameter("objectCode");
			if (objectCodeString == null) {
					objectCodeString = (String) request.getAttribute("objectCode");
			}
			objectCode = new Integer(objectCodeString);
			ISiteComponent bodyComponent = new InfoSiteRoomTimeTable();

			Object[] args = { bodyComponent, roomKey, objectCode };

			try {
				SiteView siteView =
					(SiteView) ServiceUtils.executeService(
						null,
						"RoomSiteComponentService",
						args);

				request.setAttribute("siteView", siteView);
				request.setAttribute("objectCode", objectCode);				

		} catch (NonExistingServiceException e) {
				throw new NonExistingActionException(e);
			} catch (FenixServiceException e) {
				throw new FenixActionException(e);
			}
			return mapping.findForward("roomViewer");
		} else {
			throw new FenixActionException();
		}
	}
	
	private ExecutionCourseSiteView readGroupView(
		HttpServletRequest request,
		ISiteComponent firstPageComponent,
		Integer infoExecutionCourseCode,
		Integer sectionIndex,
		Integer curricularCourseId,
		Integer groupPropertiesCode,
		Integer studentGroupCode)
		throws FenixActionException {

		Integer objectCode = null;
		if (infoExecutionCourseCode == null) {
			String objectCodeString = request.getParameter("objectCode");
			if (objectCodeString == null) {
				objectCodeString = (String) request.getAttribute("objectCode");
			}
			objectCode = new Integer(objectCodeString);
		}

		ISiteComponent commonComponent = new InfoSiteCommon();

		Object[] args =
			{	commonComponent,
				firstPageComponent,
				objectCode,
				infoExecutionCourseCode,
				sectionIndex,
				curricularCourseId,
				groupPropertiesCode,
				studentGroupCode
				};
		ExecutionCourseSiteView siteView = null;
		try 
		{
			siteView =
				(ExecutionCourseSiteView) ServiceUtils.executeService(
					null,
					"GroupSiteComponentService",
					args);

			if (infoExecutionCourseCode != null) {
				request.setAttribute(
					"objectCode",
					((InfoSiteFirstPage) siteView.getComponent())
						.getSiteIdInternal());
			} else {
				request.setAttribute("objectCode", objectCode);
			}
			request.setAttribute("siteView", siteView);
			request.setAttribute(
				"executionCourseCode",
				((InfoSiteCommon) siteView.getCommonComponent())
					.getExecutionCourse()
					.getIdInternal());
			Integer executionCode = (Integer)request.getAttribute("executionCourseCode");
			request.setAttribute(
				"executionPeriodCode",
				((InfoSiteCommon) siteView.getCommonComponent())
					.getExecutionCourse()
					.getInfoExecutionPeriod()
					.getIdInternal());
			if (siteView.getComponent() instanceof InfoSiteSection) {
				request.setAttribute(
					"infoSection",
					((InfoSiteSection) siteView.getComponent()).getSection());
			}
		
		} catch (NonExistingServiceException e) {
			throw new NonExistingActionException("A disciplina", e);
		} catch (FenixServiceException e) {
			throw new FenixActionException(e);
		}
		return siteView;
	}	

}