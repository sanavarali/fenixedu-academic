<%@ page language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<bean:define id="unitID" name="unit" property="externalId"/>

<h2><bean:message key="label.publicationCollaborators" bundle="RESEARCHER_RESOURCES"/></h2>

<logic:equal name="unit" property="currentUserAbleToDefineGroups" value="true">
<fr:edit name="unit" schema="edit-publication-collaborators">
	<fr:layout>
		<fr:property name="classes" value="tstyle5 thlight thmiddle"/>
		<fr:property name="columnClasses" value=",,tderror1 tdclear"/>
	</fr:layout>

	<fr:destination name="success" path="<%= "/researchUnitFunctionalities.do?method=configureGroups&unitId=" + unitID %>"/>
	<fr:destination name="cancel" path="<%= "/researchUnitFunctionalities.do?method=configureGroups&unitId=" + unitID %>"/>
</fr:edit>
</logic:equal>

