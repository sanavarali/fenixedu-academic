/*
 * Created on 21/Mar/2003
 */
package net.sourceforge.fenixedu.applicationTier.Servico.masterDegree.administrativeOffice.guide;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.FenixServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.InvalidChangeServiceException;
import net.sourceforge.fenixedu.applicationTier.Servico.exceptions.NoChangeMadeServiceException;
import net.sourceforge.fenixedu.dataTransferObject.InfoContributor;
import net.sourceforge.fenixedu.dataTransferObject.InfoGuide;
import net.sourceforge.fenixedu.dataTransferObject.InfoGuideEntry;
import net.sourceforge.fenixedu.dataTransferObject.InfoGuideWithPersonAndExecutionDegreeAndContributor;
import net.sourceforge.fenixedu.domain.DocumentType;
import net.sourceforge.fenixedu.domain.DomainFactory;
import net.sourceforge.fenixedu.domain.GraduationType;
import net.sourceforge.fenixedu.domain.Guide;
import net.sourceforge.fenixedu.domain.GuideEntry;
import net.sourceforge.fenixedu.domain.GuideState;
import net.sourceforge.fenixedu.domain.IContributor;
import net.sourceforge.fenixedu.domain.IExecutionDegree;
import net.sourceforge.fenixedu.domain.IExecutionYear;
import net.sourceforge.fenixedu.domain.IGratuitySituation;
import net.sourceforge.fenixedu.domain.IGuide;
import net.sourceforge.fenixedu.domain.IGuideEntry;
import net.sourceforge.fenixedu.domain.IGuideSituation;
import net.sourceforge.fenixedu.domain.IPerson;
import net.sourceforge.fenixedu.domain.IPersonAccount;
import net.sourceforge.fenixedu.domain.IStudent;
import net.sourceforge.fenixedu.domain.PersonAccount;
import net.sourceforge.fenixedu.domain.reimbursementGuide.IReimbursementGuideEntry;
import net.sourceforge.fenixedu.domain.reimbursementGuide.ReimbursementGuideEntry;
import net.sourceforge.fenixedu.domain.transactions.GratuityTransaction;
import net.sourceforge.fenixedu.domain.transactions.IPaymentTransaction;
import net.sourceforge.fenixedu.domain.transactions.InsuranceTransaction;
import net.sourceforge.fenixedu.domain.transactions.PaymentTransaction;
import net.sourceforge.fenixedu.domain.transactions.TransactionType;
import net.sourceforge.fenixedu.persistenceTier.ExcepcaoPersistencia;
import net.sourceforge.fenixedu.persistenceTier.IPersistentGratuitySituation;
import net.sourceforge.fenixedu.persistenceTier.IPersistentPersonAccount;
import net.sourceforge.fenixedu.persistenceTier.ISuportePersistente;
import net.sourceforge.fenixedu.persistenceTier.PersistenceSupportFactory;
import net.sourceforge.fenixedu.persistenceTier.transactions.IPersistentTransaction;
import net.sourceforge.fenixedu.util.CalculateGuideTotal;
import net.sourceforge.fenixedu.util.State;
import pt.utl.ist.berserk.logic.serviceManager.IService;

/**
 * @author Nuno Nunes (nmsn@rnl.ist.utl.pt) Joana Mota (jccm@rnl.ist.utl.pt)
 */
public class EditGuideInformation implements IService {

    public InfoGuide run(InfoGuide infoGuide, String[] quantityList, Integer contributorNumber,
            String othersRemarks, Integer othersQuantity, Double othersPrice)
            throws ExcepcaoPersistencia, FenixServiceException {

        // This will be the flag that indicates if a change has been made to the
        // Guide
        // No need to anything if there's no change ...
        boolean change = false;

        
        // Safety check to see if the Guide can be changed
        this.chekIfChangeable(infoGuide);

        // Read The Guide

        ISuportePersistente sp = PersistenceSupportFactory.getDefaultPersistenceSupport();
        IGuide guide = sp.getIPersistentGuide().readByNumberAndYearAndVersion(infoGuide.getNumber(),
                infoGuide.getYear(), infoGuide.getVersion());

        // check if it's needed to change the Contributor
        if ((contributorNumber != null)
                && (!infoGuide.getInfoContributor().getContributorNumber().equals(contributorNumber))) {
            change = true;
        } else {
            contributorNumber = infoGuide.getInfoContributor().getContributorNumber();
        }

        // Read the Contributor

        IContributor contributor = sp.getIPersistentContributor().readByContributorNumber(contributorNumber);

        infoGuide.setInfoContributor(InfoContributor.newInfoFromDomain(contributor));

        // Check the quantities of the Guide Entries
        // The items without a quantity or with a 0 quantity will be deleted if
        // the guide is NON PAYED or
        // they won't appear in the new guide version if the guide has been
        // payed

        Iterator iterator = infoGuide.getInfoGuideEntries().iterator();
        List newInfoGuideEntries = new ArrayList();
        List guideEntriesToRemove = new ArrayList();

        int quantityListIndex = 0;
        while (iterator.hasNext()) {
            InfoGuideEntry infoGuideEntry = (InfoGuideEntry) iterator.next();
            if ((quantityList[quantityListIndex] == null)
                    || (quantityList[quantityListIndex].length() == 0)
                    || (quantityList[quantityListIndex].equals("0"))) {
                // Add to items to remove
                guideEntriesToRemove.add(infoGuideEntry);
                change = true;
            } else {
                if (!infoGuideEntry.getQuantity().equals(new Integer(quantityList[quantityListIndex])))
                    change = true;
                infoGuideEntry.setQuantity(new Integer(quantityList[quantityListIndex]));
                newInfoGuideEntries.add(infoGuideEntry);
            }
            quantityListIndex++;
        }

        // Check if a Others Guide Entry will be Added
        IGuideEntry othersGuideEntry = null;
        if ((othersPrice != null) && (othersQuantity != null) && (!othersPrice.equals(new Double(0)))
                && (!othersQuantity.equals(new Integer(0)))) {
            change = true;
            othersGuideEntry = DomainFactory.makeGuideEntry();
            othersGuideEntry.setDescription(othersRemarks);
            othersGuideEntry.setDocumentType(DocumentType.OTHERS);
            // TODO : In the future it's possible to be a Major Degree
            othersGuideEntry.setGraduationType(GraduationType.MASTER_DEGREE);
            othersGuideEntry.setPrice(othersPrice);
            othersGuideEntry.setQuantity(othersQuantity);
        }

        if (infoGuide.getInfoGuideSituation().getSituation().equals(GuideState.NON_PAYED)) {
            // If there's a change ...
            if (change) {

                // fill in the last field in the Others Guide Entry if necessary
                if (othersGuideEntry != null) {
                    othersGuideEntry.setGuide(guide);
                }
                // Remove the Guide entries wich have been deleted
                Iterator entryIterator = guideEntriesToRemove.iterator();
                while (entryIterator.hasNext()) {
                    InfoGuideEntry infoGuideEntry = (InfoGuideEntry) entryIterator.next();

                    IGuideEntry guideEntry = (IGuideEntry) sp.getIPersistentGuideEntry().readByOID(
                            GuideEntry.class, infoGuideEntry.getIdInternal());

                    IPaymentTransaction paymentTransaction = sp.getIPersistentPaymentTransaction()
                            .readByGuideEntryID(guideEntry.getIdInternal());

                    if (paymentTransaction != null) {
                        paymentTransaction.getPersonAccount().getPaymentTransactions().remove(
                                paymentTransaction);
                        sp.getIPersistentPaymentTransaction().deleteByOID(PaymentTransaction.class,
                                paymentTransaction.getIdInternal());
                    }

                    if (guideEntry.getReimbursementGuideEntries() != null) {
                        for (IReimbursementGuideEntry reimbursementGuideEntry : guideEntry
                                .getReimbursementGuideEntries()) {
                            reimbursementGuideEntry.getReimbursementGuide()
                                    .getReimbursementGuideEntries().remove(reimbursementGuideEntry);
                            sp.getIPersistentGuideEntry().deleteByOID(ReimbursementGuideEntry.class,
                                    reimbursementGuideEntry.getIdInternal());
                        }
                    }
                    guideEntry.getGuide().getGuideEntries().remove(guideEntry);

                    sp.getIPersistentGuideEntry().deleteByOID(GuideEntry.class,
                            infoGuideEntry.getIdInternal());
                }

                // Update the remaing guide entries
                entryIterator = newInfoGuideEntries.iterator();
                while (entryIterator.hasNext()) {
                    InfoGuideEntry infoGuideEntry = (InfoGuideEntry) entryIterator.next();

                    IGuideEntry guideEntry = sp.getIPersistentGuideEntry()
                            .readByGuideAndGraduationTypeAndDocumentTypeAndDescription(guide,
                                    infoGuideEntry.getGraduationType(),
                                    infoGuideEntry.getDocumentType(), infoGuideEntry.getDescription());
                    guideEntry.setQuantity(infoGuideEntry.getQuantity());

                }
                guide.setContributor(contributor);

            }

        } else if (infoGuide.getInfoGuideSituation().getSituation().equals(GuideState.PAYED)) {
            // If there's a change ...
            if (change) {

                // Create a new Guide Version
                IGuide newGuideVersion = this.createNewGuideVersion(infoGuide);

                // fill in the last field in the Others Guide Entry if
                // necessary
                if (othersGuideEntry != null) {
                    othersGuideEntry.setGuide(newGuideVersion);
                }

                // Create The new Situation
                IGuideSituation guideSituation = DomainFactory.makeGuideSituation();
                guideSituation.setDate(infoGuide.getInfoGuideSituation().getDate());
                guideSituation.setGuide(newGuideVersion);
                guideSituation.setRemarks(infoGuide.getRemarks());
                guideSituation.setSituation(infoGuide.getInfoGuideSituation().getSituation());
                guideSituation.setState(new State(State.ACTIVE));

                // Write the new Guide Version

                // // Make sure that everything is written before reading
                // ...
                // sp.confirmarTransaccao();
                // sp.iniciarTransaccao();

                // For Transactions Creation
                IPersistentTransaction persistentTransaction = sp.getIPersistentTransaction();
                IPaymentTransaction paymentTransaction = null;
                IGratuitySituation gratuitySituation = null;
                IPersistentPersonAccount persistentPersonAccount = sp.getIPersistentPersonAccount();
                IPersonAccount personAccount = persistentPersonAccount.readByPerson(guide.getPerson()
                        .getIdInternal());

                if (personAccount == null) {
                    personAccount = DomainFactory.makePersonAccount(guide.getPerson());
                }

                IPersistentGratuitySituation persistentGratuitySituation = sp
                        .getIPersistentGratuitySituation();

                // Write the Guide Entries
                for (InfoGuideEntry infoGuideEntry : (List<InfoGuideEntry>) newInfoGuideEntries) {
                    IGuideEntry guideEntry = DomainFactory.makeGuideEntry();
                    infoGuideEntry.copyToDomain(infoGuideEntry, guideEntry);

                    // Reset id internal to allow persistence to write a new
                    // version
                    guideEntry.setGuide(newGuideVersion);

                    IPerson studentPerson = guide.getPerson();
                    IStudent student = sp.getIPersistentStudent().readByUsername(
                            studentPerson.getUsername());
                    IExecutionDegree executionDegree = guide.getExecutionDegree();

                    // Write Gratuity Transaction
                    if (guideEntry.getDocumentType().equals(DocumentType.GRATUITY)) {

                        executionDegree = guide.getExecutionDegree();
                        gratuitySituation = persistentGratuitySituation
                                .readGratuitySituationByExecutionDegreeAndStudent(executionDegree
                                        .getIdInternal(), student.getIdInternal());

                        paymentTransaction = DomainFactory.makeGratuityTransaction(guideEntry.getPrice(),
                                new Timestamp(Calendar.getInstance().getTimeInMillis()), guideEntry
                                        .getDescription(), infoGuide.getPaymentType(),
                                TransactionType.GRATUITY_ADHOC_PAYMENT, Boolean.FALSE,
                                guide.getPerson(), personAccount, guideEntry, gratuitySituation);


                        // Update GratuitySituation
                        Double remainingValue = gratuitySituation.getRemainingValue();

                        gratuitySituation.setRemainingValue(new Double(remainingValue.doubleValue()
                                + paymentTransaction.getValue().doubleValue()));

                    }

                    // Write Insurance Transaction
                    if (guideEntry.getDocumentType().equals(DocumentType.INSURANCE)) {
                        paymentTransaction = DomainFactory.makeInsuranceTransaction(guideEntry.getPrice(),
                                new Timestamp(Calendar.getInstance().getTimeInMillis()), guideEntry
                                        .getDescription(), infoGuide.getPaymentType(),
                                TransactionType.INSURANCE_PAYMENT, Boolean.FALSE, guide.getPerson(),
                                personAccount, guideEntry, executionDegree.getExecutionYear(), student);
                    }

                }

                // Update the version number for the next Database Access
                infoGuide.setVersion(newGuideVersion.getVersion());

                // CREATE TRANSACTIONS!!!!!!!!!!!!!!!!!!!!!(Gratuity or
                // Insurance)

                // UPDATE remainigValue ou GRATUITY_SITUATION!!!!!

            }

        }

        // If there's no change
        if (!change) {
            throw new NoChangeMadeServiceException();
        }
        IGuide newGuide = null;
        InfoGuide result = null;

        // Make sure that everything is written before reading ...
        sp.confirmarTransaccao();
        sp.iniciarTransaccao();

        newGuide = sp.getIPersistentGuide().readByNumberAndYearAndVersion(infoGuide.getNumber(),
                infoGuide.getYear(), infoGuide.getVersion());
        // Update the Guide Total
        InfoGuide infoGuideTemp = new InfoGuide();
        infoGuideTemp.setInfoGuideEntries(newInfoGuideEntries);

        result = InfoGuideWithPersonAndExecutionDegreeAndContributor.newInfoFromDomain(newGuide);

        result.setTotal(CalculateGuideTotal.calculate(result));

        newGuide.setTotal(result.getTotal());

        return result;

    }

    private void chekIfChangeable(InfoGuide infoGuide) throws FenixServiceException,
            ExcepcaoPersistencia {
        ISuportePersistente sp = null;
        List guides = null;

        // Annuled Guides cannot be changed
        if (infoGuide.getInfoGuideSituation().getSituation().equals(GuideState.ANNULLED))
            throw new InvalidChangeServiceException("Situation of Guide Is Annulled");

        guides = sp.getIPersistentGuide()
                .readByNumberAndYear(infoGuide.getNumber(), infoGuide.getYear());

        // If it's not the latest version ...
        if (guides.size() != infoGuide.getVersion().intValue())
            throw new InvalidChangeServiceException("Not the Latest Version");
    }

    private IGuide createNewGuideVersion(InfoGuide infoGuide) throws FenixServiceException,
            ExcepcaoPersistencia {
        IGuide guide = DomainFactory.makeGuide();
        ISuportePersistente sp = null;
        IContributor contributor = null;
        IPerson person = null;
        IExecutionDegree executionDegree = null;

        // Read the needed information from the DataBase
        person = sp.getIPessoaPersistente()
                .lerPessoaPorUsername(infoGuide.getInfoPerson().getUsername());
        contributor = sp.getIPersistentContributor().readByContributorNumber(
                infoGuide.getInfoContributor().getContributorNumber());
        IExecutionYear executionYear = sp.getIPersistentExecutionYear().readExecutionYearByName(
                infoGuide.getInfoExecutionDegree().getInfoExecutionYear().getYear());

        executionDegree = sp.getIPersistentExecutionDegree().readByDegreeCurricularPlanAndExecutionYear(
                infoGuide.getInfoExecutionDegree().getInfoDegreeCurricularPlan().getName(),
                infoGuide.getInfoExecutionDegree().getInfoDegreeCurricularPlan().getInfoDegree()
                        .getSigla(), executionYear.getYear());

        // Set the fields
        guide.setContributor(contributor);
        guide.setPerson(person);
        guide.setExecutionDegree(executionDegree);

        guide.setCreationDate(Calendar.getInstance().getTime());
        guide.setGuideRequester(infoGuide.getGuideRequester());
        guide.setNumber(infoGuide.getNumber());
        guide.setYear(infoGuide.getYear());
        guide.setVersion(new Integer(infoGuide.getVersion().intValue() + 1));
        guide.setPaymentDate(infoGuide.getPaymentDate());
        guide.setPaymentType(infoGuide.getPaymentType());
        guide.setRemarks(null);

        // The total will be calculated afterwards
        guide.setTotal(new Double(0));

        return guide;
    }

}
