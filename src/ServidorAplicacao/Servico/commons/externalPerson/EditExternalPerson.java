package ServidorAplicacao.Servico.commons.externalPerson;

import pt.utl.ist.berserk.logic.serviceManager.IService;
import Dominio.ExternalPerson;
import Dominio.IExternalPerson;
import Dominio.IPerson;
import Dominio.IWorkLocation;
import Dominio.WorkLocation;
import ServidorAplicacao.Servico.exceptions.ExistingServiceException;
import ServidorAplicacao.Servico.exceptions.FenixServiceException;
import ServidorAplicacao.Servico.exceptions.NonExistingServiceException;
import ServidorPersistente.ExcepcaoPersistencia;
import ServidorPersistente.ISuportePersistente;
import ServidorPersistente.OJB.SuportePersistenteOJB;

/**
 * 
 * @author - Shezad Anavarali (sana@mega.ist.utl.pt) - Nadir Tarmahomed
 *         (naat@mega.ist.utl.pt)
 *  
 */
public class EditExternalPerson implements IService {

    /**
     * The actor of this class.
     */
    public EditExternalPerson() {
    }

    public void run(Integer externalPersonID, String name, String address, Integer workLocationID,
            String phone, String mobile, String homepage, String email) throws FenixServiceException {
        IExternalPerson storedExternalPerson = null;
        IExternalPerson existingExternalPerson = null;

        IWorkLocation storedWorkLocation = null;

        try {
            ISuportePersistente sp = SuportePersistenteOJB.getInstance();

            storedExternalPerson = (IExternalPerson) sp.getIPersistentExternalPerson().readByOID(
                    ExternalPerson.class, externalPersonID);

            if (storedExternalPerson == null)
                throw new NonExistingServiceException(
                        "error.exception.externalPerson.nonExistingExternalPsrson");

            existingExternalPerson = sp.getIPersistentExternalPerson()
                    .readByNameAndAddressAndWorkLocationID(name, address, workLocationID);

            // checks if existes another exernal person with the same name,
            // address and name location
            if (existingExternalPerson != null) {
                if (!storedExternalPerson.getIdInternal().equals(existingExternalPerson.getIdInternal()))
                    throw new ExistingServiceException(
                            "error.exception.externalPerson.existingExternalPerson");
            }

            sp.getIPersistentExternalPerson().simpleLockWrite(storedExternalPerson);

            storedWorkLocation = (IWorkLocation) sp.getIPersistentWorkLocation().readByOID(
                    WorkLocation.class, workLocationID);
            storedExternalPerson.setWorkLocation(storedWorkLocation);

            IPerson person = storedExternalPerson.getPerson();
            sp.getIPessoaPersistente().lockWrite(person);

            person.setNome(name);
            person.setMorada(address);
            person.setTelefone(phone);
            person.setTelemovel(mobile);
            person.setEnderecoWeb(homepage);
            person.setEmail(email);

        } catch (ExcepcaoPersistencia ex) {
            FenixServiceException newEx = new FenixServiceException("Persistence layer error");
            newEx.fillInStackTrace();
            throw newEx;
        }

    }
}