/*
 * Created on 17/Ago/2003
 */
package ServidorAplicacao.Servico.teacher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pt.utl.ist.berserk.logic.serviceManager.IService;
import DataBeans.InfoGroupProperties;
import Dominio.GroupProperties;
import Dominio.IExecutionCourse;
import Dominio.IGroupProperties;
import Dominio.IStudentGroup;
import Dominio.IShift;
import ServidorAplicacao.Servico.exceptions.ExistingServiceException;
import ServidorAplicacao.Servico.exceptions.FenixServiceException;
import ServidorAplicacao.Servico.exceptions.InvalidSituationServiceException;
import ServidorPersistente.ExcepcaoPersistencia;
import ServidorPersistente.IPersistentExecutionCourse;
import ServidorPersistente.IPersistentGroupProperties;
import ServidorPersistente.IPersistentStudentGroup;
import ServidorPersistente.IPersistentStudentGroupAttend;
import ServidorPersistente.ISuportePersistente;
import ServidorPersistente.OJB.SuportePersistenteOJB;

/**
 * @author asnr and scpo
 */
public class EditGroupProperties implements IService {

    /**
     * The constructor of this class.
     */
    public EditGroupProperties() {
    }

    private boolean checkIfAlreadyExists(
            InfoGroupProperties infoGroupProperties,
            IGroupProperties groupProperties) throws FenixServiceException {

        IGroupProperties existingGroupProperties = null;
        try {
            ISuportePersistente ps = SuportePersistenteOJB.getInstance();
            IPersistentGroupProperties persistentGroupProperties = ps
                    .getIPersistentGroupProperties();
            
            if (!infoGroupProperties.getName()
                    .equals(groupProperties.getName())) {
            	
            	Iterator iterExecutionCourses = groupProperties
							.getExecutionCourses().iterator();
            	while (iterExecutionCourses.hasNext()){
            	
                persistentGroupProperties = ps.getIPersistentGroupProperties();
                existingGroupProperties = ((IExecutionCourse)iterExecutionCourses.next())
										.getGroupPropertiesByName(infoGroupProperties.getName());
                if (existingGroupProperties != null)
                    throw new ExistingServiceException();
            	}
            }

        } catch (ExcepcaoPersistencia excepcaoPersistencia) {
            throw new FenixServiceException(excepcaoPersistencia);
        }
        return true;
    }

    private List checkIfIsPossibleToEdit(
            InfoGroupProperties infoGroupProperties,
            IGroupProperties groupProperties) throws FenixServiceException {

    	IPersistentStudentGroup persistentStudentGroup = null;
        IPersistentStudentGroupAttend persistentStudentGroupAttend = null;
        List errors = new ArrayList();
        try {
            ISuportePersistente ps = SuportePersistenteOJB.getInstance();

            persistentStudentGroup = ps.getIPersistentStudentGroup();
            persistentStudentGroupAttend = ps
                    .getIPersistentStudentGroupAttend();
            List allStudentsGroup = groupProperties.getAttendsSet().getStudentGroups();

            Integer groupMaximumNumber = infoGroupProperties
                    .getGroupMaximumNumber();
            Integer maximumCapacity = infoGroupProperties.getMaximumCapacity();
            Integer minimumCapacity = infoGroupProperties.getMinimumCapacity();

            if (groupMaximumNumber != null) {
                IShift shift = null;
                List shiftsInternalList = new ArrayList();
                Iterator iterator = allStudentsGroup.iterator();

                while (iterator.hasNext()) {
                    shift = ((IStudentGroup) iterator.next()).getShift();
                    if (!shiftsInternalList.contains(shift))
                        shiftsInternalList.add(shift);
                }

                Iterator iterator2 = shiftsInternalList.iterator();
                List studentGroupsList = null;
                shift = null;

                while (iterator2.hasNext()) {
                    shift = (IShift) iterator2.next();
                    if(shift!=null){
                    studentGroupsList = persistentStudentGroup
                            .readAllStudentGroupByAttendsSetAndShift(
                                    groupProperties.getAttendsSet(), shift);
                    }else{
                    	studentGroupsList = groupProperties.getAttendsSet().getStudentGroups();
                    }
                    
                    if (studentGroupsList.size() > groupMaximumNumber
                            .intValue()) {
                        if (!errors.contains(new Integer(-1)))
                            errors.add(new Integer(-1));
                    }

                }

            }

            if (maximumCapacity == null && minimumCapacity == null) {
                return errors;
            }

            Iterator iterGroups = allStudentsGroup.iterator();
            List allStudents = null;
            Integer size;
            while (iterGroups.hasNext()) {
                allStudents = new ArrayList();

                IStudentGroup studentGroup = (IStudentGroup) iterGroups.next();
                allStudents = persistentStudentGroupAttend
                        .readAllByStudentGroup(studentGroup);
                size = new Integer(allStudents.size());
                if (maximumCapacity != null) {

                    if (size.compareTo(maximumCapacity) > 0) {
                        if (!errors.contains(new Integer(-2)))
                            errors.add(new Integer(-2));
                    }
                }

                if (minimumCapacity != null) {
                    if (size.compareTo(minimumCapacity) < 0) {
                        if (!errors.contains(new Integer(-3)))
                            errors.add(new Integer(-3));
                    }
                }
            }

        } catch (ExcepcaoPersistencia excepcaoPersistencia) {
            throw new FenixServiceException(excepcaoPersistencia);
        }
        return errors;
    }

    private void unEnrollStudentGroups(List studentGroupList) throws FenixServiceException {
    	try{
    		ISuportePersistente ps = SuportePersistenteOJB.getInstance();
    		IPersistentStudentGroup persistentStudentGroup = ps.getIPersistentStudentGroup();

    		Iterator iterStudentGroupList = studentGroupList.iterator();
    		while(iterStudentGroupList.hasNext()){
    			IStudentGroup studentGroup = (IStudentGroup)iterStudentGroupList.next();
    			IShift shift = studentGroup.getShift();
    			if(shift!=null){
    				persistentStudentGroup.simpleLockWrite(studentGroup);
    				studentGroup.setShift(null);
    			}
    		}
    	} catch (ExcepcaoPersistencia excepcaoPersistencia) {
    		throw new FenixServiceException(excepcaoPersistencia);
    	}
    }
    
    
    /**
     * Executes the service.
     */

    public List run(Integer objectCode, InfoGroupProperties infoGroupProperties)
            throws FenixServiceException {

        List result = new ArrayList();
        try {
            ISuportePersistente ps = SuportePersistenteOJB.getInstance();

            IPersistentGroupProperties persistentGroupProperties = ps
                    .getIPersistentGroupProperties();
            IPersistentExecutionCourse persistentExecutionCourse = ps
                    .getIPersistentExecutionCourse();

            IGroupProperties groupProperties = (IGroupProperties) persistentGroupProperties
                    .readByOID(GroupProperties.class, infoGroupProperties
                            .getIdInternal());
            if(groupProperties == null){
            	throw new InvalidSituationServiceException();
            }
            
            if (checkIfAlreadyExists(infoGroupProperties, groupProperties)) {
                result = checkIfIsPossibleToEdit(infoGroupProperties,
                        groupProperties);
                
                persistentGroupProperties.simpleLockWrite(groupProperties);
                groupProperties.setEnrolmentBeginDay(infoGroupProperties
                        .getEnrolmentBeginDay());
                groupProperties.setEnrolmentEndDay(infoGroupProperties
                        .getEnrolmentEndDay());
                groupProperties.setEnrolmentPolicy(infoGroupProperties
                        .getEnrolmentPolicy());
                groupProperties.setGroupMaximumNumber(infoGroupProperties
                        .getGroupMaximumNumber());
                groupProperties.setIdealCapacity(infoGroupProperties
                        .getIdealCapacity());
                groupProperties.setIdInternal(infoGroupProperties
                        .getIdInternal());
                groupProperties.setMaximumCapacity(infoGroupProperties
                        .getMaximumCapacity());
                groupProperties.setMinimumCapacity(infoGroupProperties
                        .getMinimumCapacity());

                groupProperties.setName(infoGroupProperties.getName());
                groupProperties.setProjectDescription(infoGroupProperties
                        .getProjectDescription());
                groupProperties
                        .setShiftType(infoGroupProperties.getShiftType());
                if(infoGroupProperties.getShiftType()==null){
                	unEnrollStudentGroups(groupProperties.getAttendsSet().getStudentGroups());
                }

            }
        } catch (ExcepcaoPersistencia e) {
            throw new FenixServiceException(e);
        }
        return result;

    }
}