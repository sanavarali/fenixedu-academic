/*
 * Created on 14/Mar/2003
 *
 */
package ServidorApresentacao.Action.masterDegree.administrativeOffice.candidate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.util.LabelValueBean;

import DataBeans.InfoCandidateSituation;
import DataBeans.InfoCountry;
import DataBeans.InfoDegree;
import DataBeans.InfoExecutionDegree;
import DataBeans.InfoMasterDegreeCandidate;
import DataBeans.InfoPerson;
import DataBeans.comparators.ComparatorByNameForInfoExecutionDegree;
import ServidorAplicacao.IUserView;
import ServidorAplicacao.Servico.exceptions.ExistingServiceException;
import ServidorAplicacao.Servico.exceptions.FenixServiceException;
import ServidorApresentacao.Action.exceptions.ExistingActionException;
import ServidorApresentacao.Action.exceptions.FenixActionException;
import ServidorApresentacao.Action.sop.utils.SessionConstants;
import Util.Data;
import Util.EstadoCivil;
import Util.RandomStringGenerator;
import Util.Sexo;
import Util.SituationName;
import Util.Specialization;
import Util.TipoDocumentoIdentificacao;
import framework.factory.ServiceManagerServiceFactory;

/**
 * 
 * @author Nuno Nunes (nmsn@rnl.ist.utl.pt) Joana Mota (jccm@rnl.ist.utl.pt)
 * 
 *  
 */
public class ListCandidatesDispatchAction extends DispatchAction {

    public ActionForward prepareChoose(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession(false);

        if (session != null) {

            DynaActionForm listCandidatesForm = (DynaActionForm) form;

            String action = request.getParameter("action");

            if (action.equals("visualize")) {
                session.removeAttribute(SessionConstants.MASTER_DEGREE_CANDIDATE_ACTION);
                session.setAttribute(SessionConstants.MASTER_DEGREE_CANDIDATE_ACTION,
                        "label.action.visualize");
            } else if (action.equals("edit")) {
                session.removeAttribute(SessionConstants.MASTER_DEGREE_CANDIDATE_ACTION);
                session.setAttribute(SessionConstants.MASTER_DEGREE_CANDIDATE_ACTION,
                        "label.action.edit");

            }

            // Get the chosen exectionYear
            String executionYear = (String) session.getAttribute(SessionConstants.EXECUTION_YEAR);
            listCandidatesForm.set("executionYear", executionYear);

            IUserView userView = (IUserView) session.getAttribute(SessionConstants.U_VIEW);

            // Create the Degree Type List

            session.setAttribute(SessionConstants.SPECIALIZATIONS, Specialization.toArrayList());

            // Get the Degree List

            List degreeList = null;

            Object args[] = { executionYear };

            try {
                degreeList = (ArrayList) ServiceManagerServiceFactory.executeService(userView,
                        "ReadMasterDegrees", args);
            } catch (Exception e) {
                throw new Exception(e);
            }

            //			BeanComparator nameComparator = new
            // BeanComparator("infoDegreeCurricularPlan.infoDegree.nome");
            //			Collections.sort(degreeList, nameComparator);
            Collections.sort(degreeList, new ComparatorByNameForInfoExecutionDegree());
            List newDegreeList = degreeList;
            List executionDegreeLabels = buildExecutionDegreeLabelValueBean(newDegreeList);

            session.setAttribute(SessionConstants.DEGREE_LIST, executionDegreeLabels);

            // Create the Candidate Situation List

            session.setAttribute(SessionConstants.CANDIDATE_SITUATION_LIST, SituationName.toArrayList());

            return mapping.findForward("PrepareReady");
        }
        throw new Exception();

    }

    public ActionForward getCandidates(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession(false);

        if (session != null) {

            DynaActionForm listCandidatesForm = (DynaActionForm) form;

            IUserView userView = (IUserView) session.getAttribute(SessionConstants.U_VIEW);

            // Get the Information
            String degreeTypeTemp = (String) listCandidatesForm.get("specialization");
            String degreeName = (String) listCandidatesForm.get("degree");
            String executionDegree = request.getParameter("executionDegreeOID");

            String candidateSituationTemp = (String) listCandidatesForm.get("candidateSituation");
            String candidateNumberTemp = (String) listCandidatesForm.get("candidateNumber");
            String executionYear = (String) listCandidatesForm.get("executionYear");

            Integer candidateNumber = null;
            Specialization specialization = null;
            SituationName situationName = null;

            if (degreeName.length() == 0)
                degreeName = null;
            if (candidateNumberTemp.length() != 0)
                candidateNumber = Integer.valueOf(candidateNumberTemp);
            if (degreeTypeTemp != null && degreeTypeTemp.length() != 0)
                specialization = new Specialization(degreeTypeTemp);
            if (candidateSituationTemp != null && candidateSituationTemp.length() != 0)
                situationName = new SituationName(candidateSituationTemp);

            Object args[] = { executionDegree, specialization, situationName, candidateNumber,
                    executionYear };
            List result = null;

            try {
                result = (List) ServiceManagerServiceFactory.executeService(userView,
                        "ReadCandidateList", args);
            } catch (Exception e) {
                throw new Exception(e);
            }
            if (result.size() != 0) {
                InfoMasterDegreeCandidate infoMasterDegreeCandidate = (InfoMasterDegreeCandidate) result
                        .get(0);
                degreeName = infoMasterDegreeCandidate.getInfoExecutionDegree()
                        .getInfoDegreeCurricularPlan().getInfoDegree().getNome()
                        + "-"
                        + infoMasterDegreeCandidate.getInfoExecutionDegree()
                                .getInfoDegreeCurricularPlan().getName();
            }
            if (result.size() == 1) {
                InfoMasterDegreeCandidate infoMasterDegreeCandidate = (InfoMasterDegreeCandidate) result
                        .get(0);
                request.setAttribute("candidateID", infoMasterDegreeCandidate.getIdInternal());
                request.setAttribute(SessionConstants.MASTER_DEGREE_CANDIDATE_LIST, result);
                return mapping.findForward("ActionReady");
            }
            // Create find query String
            String query = new String();
            query = "  - Ano Lectivo : " + executionYear + "<br />";
            // query = " - Degree : " + degreeName + "<br />";
            if (specialization == null && situationName == null && candidateNumber == null)
                query += "  - Todos os criterios";
            else {
                if (degreeName != null)
                    query += "  - Degree: " + degreeName + "<br />";
                if (specialization != null)
                    query += "  - Tipo de Especializa��o: " + specialization.toString() + "<br />";
                if (situationName != null)
                    query += "  - Situa��o do Candidato: " + situationName.toString() + "<br />";
                if (candidateNumber != null)
                    query += "  - N�mero de Candidato: " + candidateNumber + "<br />";
            }

            session.removeAttribute(SessionConstants.MASTER_DEGREE_CANDIDATE_LIST);
            session.removeAttribute(SessionConstants.MASTER_DEGREE_CANDIDATE_QUERY);
            session.setAttribute(SessionConstants.MASTER_DEGREE_CANDIDATE_LIST, result);
            session.setAttribute(SessionConstants.MASTER_DEGREE_CANDIDATE_QUERY, query);

            return mapping.findForward("ChooseCandidate");
        }
        throw new Exception();
    }

    public ActionForward chooseCandidate(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession(false);

        if (session != null) {

            IUserView userView = (IUserView) session.getAttribute(SessionConstants.U_VIEW);
            Integer personID = Integer.valueOf(request.getParameter("personID"));
            request.setAttribute("candidateID", new Integer(request.getParameter("candidateID")));

            // Read the Candidates for This Person

            List result = null;

            Object args[] = { personID };
            try {
                result = (List) ServiceManagerServiceFactory.executeService(userView,
                        "GetCandidatesByPerson", args);
            } catch (Exception e) {
                throw new Exception(e);
            }

            request.setAttribute(SessionConstants.MASTER_DEGREE_CANDIDATE_LIST, result);

            return mapping.findForward("ActionReady");

        }
        throw new Exception();
    }

    public ActionForward visualize(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession(false);

        if (session != null) {

            IUserView userView = (IUserView) session.getAttribute(SessionConstants.U_VIEW);
            Integer candidateID = (Integer) request.getAttribute("candidateID");

            if (candidateID == null) {
                candidateID = Integer.valueOf(request.getParameter("candidateID"));
            }

            // Read the Candidates for This Person

            InfoMasterDegreeCandidate result = null;

            Object args[] = { candidateID };
            try {
                result = (InfoMasterDegreeCandidate) ServiceManagerServiceFactory.executeService(
                        userView, "GetCandidatesByID", args);
            } catch (Exception e) {
                throw new Exception(e);
            }

            request.setAttribute(SessionConstants.MASTER_DEGREE_CANDIDATE, result);
            return mapping.findForward("VisualizeCandidate");

        }
        throw new Exception();
    }

    public ActionForward prepareEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession(false);

        if (session != null) {

            DynaActionForm editCandidateForm = (DynaActionForm) form;

            IUserView userView = (IUserView) session.getAttribute(SessionConstants.U_VIEW);

            Integer candidateID = (Integer) request.getAttribute("candidateID");
            if (candidateID == null) {
                candidateID = Integer.valueOf(request.getParameter("candidateID"));
            }

            // Read the Candidates for This Person

            InfoMasterDegreeCandidate infoMasterDegreeCandidate = null;

            Object args[] = { candidateID };
            try {
                infoMasterDegreeCandidate = (InfoMasterDegreeCandidate) ServiceManagerServiceFactory
                        .executeService(userView, "GetCandidatesByID", args);
            } catch (Exception e) {
                throw new Exception(e);
            }

            boolean validationError = request.getParameter("error") != null;
            if (!validationError) {
                populateForm(editCandidateForm, infoMasterDegreeCandidate);
            }

            // Get List of available Countries
            Object result = null;
            result = ServiceManagerServiceFactory.executeService(userView, "ReadAllCountries", null);
            List country = (ArrayList) result;

            // Build List of Countries for the Form
            Iterator iterador = country.iterator();

            List nationalityList = new ArrayList();
            while (iterador.hasNext()) {
                InfoCountry countryTemp = (InfoCountry) iterador.next();
                nationalityList.add(new LabelValueBean(countryTemp.getNationality(), countryTemp
                        .getNationality()));
            }

            request.setAttribute(SessionConstants.NATIONALITY_LIST_KEY, nationalityList);
            request.setAttribute(SessionConstants.MASTER_DEGREE_CANDIDATE, infoMasterDegreeCandidate);
            request.setAttribute(SessionConstants.MARITAL_STATUS_LIST_KEY, new EstadoCivil()
                    .toArrayList());
            request.setAttribute(SessionConstants.IDENTIFICATION_DOCUMENT_TYPE_LIST_KEY,
                    TipoDocumentoIdentificacao.toArrayList());
            request.setAttribute(SessionConstants.SEX_LIST_KEY, new Sexo().toArrayList());
            request.setAttribute(SessionConstants.MONTH_DAYS_KEY, Data.getMonthDays());
            request.setAttribute(SessionConstants.MONTH_LIST_KEY, Data.getMonths());
            request.setAttribute(SessionConstants.YEARS_KEY, Data.getYears());

            request.setAttribute(SessionConstants.EXPIRATION_YEARS_KEY, Data.getExpirationYears());

            request.setAttribute(SessionConstants.CANDIDATE_SITUATION_LIST, SituationName.toArrayList());

            return mapping.findForward("PrepareReady");

        }
        throw new Exception();
    }

    public ActionForward change(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession(false);

        if (session != null) {

            DynaActionForm editCandidateForm = (DynaActionForm) form;

            IUserView userView = (IUserView) session.getAttribute(SessionConstants.U_VIEW);

            Integer candidateID = (Integer) editCandidateForm.get("candidateID");

            // FIXME: Check All if fields are empty

            Calendar birthDate = Calendar.getInstance();
            Calendar idDocumentIssueDate = Calendar.getInstance();
            Calendar idDocumentExpirationDate = Calendar.getInstance();

            InfoPerson infoPerson = new InfoPerson();

            String day = (String) editCandidateForm.get("birthDay");
            String month = (String) editCandidateForm.get("birthMonth");
            String year = (String) editCandidateForm.get("birthYear");

            if ((day == null) || (month == null) || (year == null) || (day.length() == 0)
                    || (month.length() == 0) || (year.length() == 0)) {
                infoPerson.setNascimento(null);
            } else {
                birthDate.set(new Integer(((String) editCandidateForm.get("birthYear"))).intValue(),
                        new Integer(((String) editCandidateForm.get("birthMonth"))).intValue(),
                        new Integer(((String) editCandidateForm.get("birthDay"))).intValue());
                infoPerson.setNascimento(birthDate.getTime());
            }

            day = (String) editCandidateForm.get("idIssueDateDay");
            month = (String) editCandidateForm.get("idIssueDateMonth");
            year = (String) editCandidateForm.get("idIssueDateYear");

            if ((day == null) || (month == null) || (year == null) || (day.length() == 0)
                    || (month.length() == 0) || (year.length() == 0)) {
                infoPerson.setDataEmissaoDocumentoIdentificacao(null);
            } else {
                idDocumentIssueDate.set(new Integer(((String) editCandidateForm.get("idIssueDateYear")))
                        .intValue(), new Integer(((String) editCandidateForm.get("idIssueDateMonth")))
                        .intValue(), new Integer(((String) editCandidateForm.get("idIssueDateDay")))
                        .intValue());
                infoPerson.setDataEmissaoDocumentoIdentificacao(idDocumentIssueDate.getTime());
            }

            day = (String) editCandidateForm.get("idExpirationDateDay");
            month = (String) editCandidateForm.get("idExpirationDateMonth");
            year = (String) editCandidateForm.get("idExpirationDateYear");

            if ((day == null) || (month == null) || (year == null) || (day.length() == 0)
                    || (month.length() == 0) || (year.length() == 0)) {
                infoPerson.setDataValidadeDocumentoIdentificacao(null);
            } else {
                idDocumentExpirationDate.set(new Integer(((String) editCandidateForm
                        .get("idExpirationDateYear"))).intValue(), new Integer(
                        ((String) editCandidateForm.get("idExpirationDateMonth"))).intValue(),
                        new Integer(((String) editCandidateForm.get("idExpirationDateDay"))).intValue());
                infoPerson.setDataValidadeDocumentoIdentificacao(idDocumentExpirationDate.getTime());
            }
            InfoCountry nationality = new InfoCountry();
            nationality.setNationality((String) editCandidateForm.get("nationality"));

            infoPerson.setTipoDocumentoIdentificacao(new TipoDocumentoIdentificacao(
                    (String) editCandidateForm.get("identificationDocumentType")));
            infoPerson.setNumeroDocumentoIdentificacao((String) editCandidateForm
                    .get("identificationDocumentNumber"));
            infoPerson.setLocalEmissaoDocumentoIdentificacao((String) editCandidateForm
                    .get("identificationDocumentIssuePlace"));
            infoPerson.setNome((String) editCandidateForm.get("name"));

            String sex = (String) editCandidateForm.get("sex");
            if ((sex == null) || (sex.length() == 0))
                infoPerson.setSexo(null);
            else
                infoPerson.setSexo(new Sexo(sex));

            String maritalStatus = (String) editCandidateForm.get("maritalStatus");
            if ((maritalStatus == null) || (maritalStatus.length() == 0))
                infoPerson.setEstadoCivil(null);
            else
                infoPerson.setEstadoCivil(new EstadoCivil(maritalStatus));

            infoPerson.setInfoPais(nationality);
            infoPerson.setNomePai((String) editCandidateForm.get("fatherName"));
            infoPerson.setNomeMae((String) editCandidateForm.get("motherName"));
            infoPerson.setFreguesiaNaturalidade((String) editCandidateForm.get("birthPlaceParish"));
            infoPerson.setConcelhoNaturalidade((String) editCandidateForm.get("birthPlaceMunicipality"));
            infoPerson.setDistritoNaturalidade((String) editCandidateForm.get("birthPlaceDistrict"));
            infoPerson.setMorada((String) editCandidateForm.get("address"));
            infoPerson.setLocalidade((String) editCandidateForm.get("place"));
            infoPerson.setCodigoPostal((String) editCandidateForm.get("postCode"));
            infoPerson.setFreguesiaMorada((String) editCandidateForm.get("addressParish"));
            infoPerson.setConcelhoMorada((String) editCandidateForm.get("addressMunicipality"));
            infoPerson.setDistritoMorada((String) editCandidateForm.get("addressDistrict"));
            infoPerson.setTelefone((String) editCandidateForm.get("telephone"));
            infoPerson.setTelemovel((String) editCandidateForm.get("mobilePhone"));
            infoPerson.setEmail((String) editCandidateForm.get("email"));
            infoPerson.setEnderecoWeb((String) editCandidateForm.get("webSite"));
            infoPerson.setNumContribuinte((String) editCandidateForm.get("contributorNumber"));
            infoPerson.setProfissao((String) editCandidateForm.get("occupation"));
            infoPerson.setUsername((String) editCandidateForm.get("username"));
            infoPerson.setLocalidadeCodigoPostal((String) editCandidateForm.get("areaOfAreaCode"));

            InfoMasterDegreeCandidate newCandidate = new InfoMasterDegreeCandidate();
            newCandidate.setInfoPerson(infoPerson);

            newCandidate.setMajorDegree((String) editCandidateForm.get("majorDegree"));
            newCandidate.setMajorDegreeSchool((String) editCandidateForm.get("majorDegreeSchool"));
            newCandidate.setSpecializationArea((String) editCandidateForm.get("specializationArea"));

            String majorDegreeYearString = (String) editCandidateForm.get("majorDegreeYear");

            if ((majorDegreeYearString == null) || (majorDegreeYearString.length() == 0))
                newCandidate.setMajorDegreeYear(null);
            else
                newCandidate.setMajorDegreeYear(new Integer(majorDegreeYearString));

            String averageString = (String) editCandidateForm.get("average");
            if ((averageString != null) && (averageString.length() != 0))
                newCandidate.setAverage(new Double(averageString));
            else
                newCandidate.setAverage(null);

            String situation = (String) editCandidateForm.get("situation");
            String situationRemarks = (String) editCandidateForm.get("situationRemarks");
            InfoCandidateSituation infoCandidateSituation = new InfoCandidateSituation();
            infoCandidateSituation.setRemarks(situationRemarks);

            infoCandidateSituation.setSituation(new SituationName(situation));
            newCandidate.setInfoCandidateSituation(infoCandidateSituation);

            Object args[] = { candidateID, newCandidate };
            InfoMasterDegreeCandidate infoMasterDegreeCandidateChanged = null;

            try {
                infoMasterDegreeCandidateChanged = (InfoMasterDegreeCandidate) ServiceManagerServiceFactory
                        .executeService(userView, "ChangeCandidate", args);
            } catch (ExistingServiceException e) {
                throw new ExistingActionException("Esta Person", e);
            } catch (FenixServiceException e) {
                throw new FenixActionException(e);
            }

            request.setAttribute(SessionConstants.MASTER_DEGREE_CANDIDATE,
                    infoMasterDegreeCandidateChanged);
            return mapping.findForward("ChangeSuccess");

        }
        throw new Exception();
    }

    public ActionForward changePassword(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession(false);

        if (session != null) {

            IUserView userView = (IUserView) session.getAttribute(SessionConstants.U_VIEW);

            // Read the Candidate

            Integer candidateID = Integer.valueOf(request.getParameter("candidateID"));

            InfoMasterDegreeCandidate infoMasterDegreeCandidate = null;

            try {
                Object args[] = { candidateID };
                infoMasterDegreeCandidate = (InfoMasterDegreeCandidate) ServiceManagerServiceFactory
                        .executeService(userView, "GetCandidatesByID", args);
            } catch (FenixServiceException e) {
                throw new FenixActionException();
            }

            infoMasterDegreeCandidate.getInfoPerson().setPassword(
                    RandomStringGenerator.getRandomStringGenerator(8));

            // Write the Person
            try {
                Object args[] = { infoMasterDegreeCandidate.getInfoPerson().getIdInternal(),
                        infoMasterDegreeCandidate.getInfoPerson().getPassword() };
                ServiceManagerServiceFactory.executeService(userView, "ChangePersonPassword", args);
            } catch (FenixServiceException e) {
                throw new FenixActionException();
            }

            session.setAttribute(SessionConstants.MASTER_DEGREE_CANDIDATE, infoMasterDegreeCandidate);
            session.setAttribute(SessionConstants.PRINT_PASSWORD, Boolean.TRUE);
            return mapping.findForward("ChangePasswordSuccess");

        }
        throw new Exception();
    }

    private void populateForm(DynaActionForm editCandidateForm,
            InfoMasterDegreeCandidate infoMasterDegreeCandidate) {
        // Fill in The Form

        InfoPerson infoPerson = infoMasterDegreeCandidate.getInfoPerson();

        editCandidateForm.set("identificationDocumentNumber", infoPerson
                .getNumeroDocumentoIdentificacao());
        editCandidateForm.set("identificationDocumentType", infoPerson.getTipoDocumentoIdentificacao()
                .toString());
        editCandidateForm.set("identificationDocumentIssuePlace", infoPerson
                .getLocalEmissaoDocumentoIdentificacao());
        editCandidateForm.set("name", infoPerson.getNome());

        Calendar birthDate = Calendar.getInstance();
        if (infoPerson.getNascimento() == null) {
            editCandidateForm.set("birthDay", Data.OPTION_DEFAULT);
            editCandidateForm.set("birthMonth", Data.OPTION_DEFAULT);
            editCandidateForm.set("birthYear", Data.OPTION_DEFAULT);
        } else {
            birthDate.setTime(infoPerson.getNascimento());
            editCandidateForm.set("birthDay", new Integer(birthDate.get(Calendar.DAY_OF_MONTH))
                    .toString());
            editCandidateForm.set("birthMonth", new Integer(birthDate.get(Calendar.MONTH)).toString());
            editCandidateForm.set("birthYear", new Integer(birthDate.get(Calendar.YEAR)).toString());
        }

        Calendar identificationDocumentIssueDate = Calendar.getInstance();
        if (infoPerson.getDataEmissaoDocumentoIdentificacao() == null) {
            editCandidateForm.set("idIssueDateDay", Data.OPTION_DEFAULT);
            editCandidateForm.set("idIssueDateMonth", Data.OPTION_DEFAULT);
            editCandidateForm.set("idIssueDateYear", Data.OPTION_DEFAULT);
        } else {
            identificationDocumentIssueDate.setTime(infoPerson.getDataEmissaoDocumentoIdentificacao());
            editCandidateForm.set("idIssueDateDay", new Integer(identificationDocumentIssueDate
                    .get(Calendar.DAY_OF_MONTH)).toString());
            editCandidateForm.set("idIssueDateMonth", new Integer(identificationDocumentIssueDate
                    .get(Calendar.MONTH)).toString());
            editCandidateForm.set("idIssueDateYear", new Integer(identificationDocumentIssueDate
                    .get(Calendar.YEAR)).toString());
        }

        Calendar identificationDocumentExpirationDate = Calendar.getInstance();
        if (infoPerson.getDataValidadeDocumentoIdentificacao() == null) {
            editCandidateForm.set("idExpirationDateDay", Data.OPTION_DEFAULT);
            editCandidateForm.set("idExpirationDateMonth", Data.OPTION_DEFAULT);
            editCandidateForm.set("idExpirationDateYear", Data.OPTION_DEFAULT);
        } else {
            identificationDocumentExpirationDate.setTime(infoPerson
                    .getDataValidadeDocumentoIdentificacao());
            editCandidateForm.set("idExpirationDateDay", new Integer(
                    identificationDocumentExpirationDate.get(Calendar.DAY_OF_MONTH)).toString());
            editCandidateForm.set("idExpirationDateMonth", new Integer(
                    identificationDocumentExpirationDate.get(Calendar.MONTH)).toString());
            editCandidateForm.set("idExpirationDateYear", new Integer(
                    identificationDocumentExpirationDate.get(Calendar.YEAR)).toString());
        }

        editCandidateForm.set("fatherName", infoPerson.getNomePai());
        editCandidateForm.set("motherName", infoPerson.getNomeMae());

        if (infoPerson.getInfoPais() == null) {
            editCandidateForm.set("nationality", null);
        } else {
            editCandidateForm.set("nationality", infoPerson.getInfoPais().getNationality());
        }

        editCandidateForm.set("birthPlaceParish", infoPerson.getFreguesiaNaturalidade());
        editCandidateForm.set("birthPlaceMunicipality", infoPerson.getConcelhoNaturalidade());
        editCandidateForm.set("birthPlaceDistrict", infoPerson.getDistritoNaturalidade());
        editCandidateForm.set("address", infoPerson.getMorada());
        editCandidateForm.set("place", infoPerson.getLocalidade());
        editCandidateForm.set("postCode", infoPerson.getCodigoPostal());
        editCandidateForm.set("addressParish", infoPerson.getFreguesiaMorada());
        editCandidateForm.set("addressMunicipality", infoPerson.getConcelhoMorada());
        editCandidateForm.set("addressDistrict", infoPerson.getDistritoMorada());
        editCandidateForm.set("telephone", infoPerson.getTelefone());
        editCandidateForm.set("mobilePhone", infoPerson.getTelemovel());
        editCandidateForm.set("email", infoPerson.getEmail());
        editCandidateForm.set("webSite", infoPerson.getEnderecoWeb());
        editCandidateForm.set("contributorNumber", infoPerson.getNumContribuinte());
        editCandidateForm.set("occupation", infoPerson.getProfissao());
        editCandidateForm.set("username", infoPerson.getUsername());
        editCandidateForm.set("areaOfAreaCode", infoPerson.getLocalidadeCodigoPostal());
        editCandidateForm.set("situation", infoMasterDegreeCandidate.getInfoCandidateSituation()
                .getSituation().toString());
        editCandidateForm.set("specializationArea", infoMasterDegreeCandidate.getSpecializationArea());
        editCandidateForm.set("average", infoMasterDegreeCandidate.getAverage().toString());
        editCandidateForm.set("majorDegree", infoMasterDegreeCandidate.getMajorDegree());
        editCandidateForm.set("majorDegreeSchool", infoMasterDegreeCandidate.getMajorDegreeSchool());

        editCandidateForm.set("candidateID", infoMasterDegreeCandidate.getIdInternal());

        if ((infoPerson.getSexo() != null))
            editCandidateForm.set("sex", infoPerson.getSexo().toString());
        if (infoPerson.getEstadoCivil() != null)
            editCandidateForm.set("maritalStatus", infoPerson.getEstadoCivil().toString());

        if ((infoMasterDegreeCandidate.getMajorDegreeYear().intValue() == 0))
            editCandidateForm.set("majorDegreeYear", null);
        else
            editCandidateForm.set("majorDegreeYear", String.valueOf(infoMasterDegreeCandidate
                    .getMajorDegreeYear()));

    }

    private List buildExecutionDegreeLabelValueBean(List executionDegreeList) {
        List executionDegreeLabels = new ArrayList();
        Iterator iterator = executionDegreeList.iterator();
        while (iterator.hasNext()) {
            InfoExecutionDegree infoExecutionDegree = (InfoExecutionDegree) iterator.next();
            String name = infoExecutionDegree.getInfoDegreeCurricularPlan().getInfoDegree().getNome();

            name = infoExecutionDegree.getInfoDegreeCurricularPlan().getInfoDegree().getTipoCurso()
                    .toString()
                    + " em " + name;

            name += duplicateInfoDegree(executionDegreeList, infoExecutionDegree) ? "-"
                    + infoExecutionDegree.getInfoDegreeCurricularPlan().getName() : "";

            executionDegreeLabels.add(new LabelValueBean(name, infoExecutionDegree
                    .getInfoDegreeCurricularPlan().getInfoDegree().getNome()));
            //
        }
        return executionDegreeLabels;
    }

    private boolean duplicateInfoDegree(List executionDegreeList, InfoExecutionDegree infoExecutionDegree) {
        InfoDegree infoDegree = infoExecutionDegree.getInfoDegreeCurricularPlan().getInfoDegree();
        Iterator iterator = executionDegreeList.iterator();

        while (iterator.hasNext()) {
            InfoExecutionDegree infoExecutionDegree2 = (InfoExecutionDegree) iterator.next();
            if (infoDegree.equals(infoExecutionDegree2.getInfoDegreeCurricularPlan().getInfoDegree())
                    && !(infoExecutionDegree.equals(infoExecutionDegree2)))
                return true;

        }
        return false;
    }

}