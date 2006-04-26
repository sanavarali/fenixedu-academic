<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ page import="net.sourceforge.fenixedu.presentationTier.Action.sop.utils.SessionConstants" %>
<%@ page import="net.sourceforge.fenixedu.domain.degree.DegreeType" %>

<bean:define id="institutionUrl" type="java.lang.String">
	<bean:message key="institution.url" bundle="GLOBAL_RESOURCES"/>
</bean:define>
<div class="breadcumbs">
	<a href="<%= institutionUrl %>">
		<bean:message key="institution.name.abbreviation" bundle="GLOBAL_RESOURCES"/>
	</a>
	<bean:define id="institutionUrlTeaching" type="java.lang.String">
		<bean:message key="institution.url" bundle="GLOBAL_RESOURCES"/>
		<bean:message key="link.institution" bundle="GLOBAL_RESOURCES"/>
	</bean:define>
	&nbsp;&gt;&nbsp;
	<a href="<%=institutionUrlTeaching%>">
		<bean:message  bundle="PUBLIC_DEGREE_INFORMATION" key="public.degree.information.label.education"/>
	</a>
	<logic:present name="degree">
		&nbsp;&gt;&nbsp;
		<html:link page="<%= "/showDegreeSite.do?method=showDescription&amp;degreeID=" + request.getAttribute("degreeID").toString() %>">
			<logic:equal name="degree" property="bolonhaDegree" value="false">
				<bean:write name="degree" property="sigla"/>
			</logic:equal>
			<logic:equal name="degree" property="bolonhaDegree" value="true">
				<bean:write name="degree" property="acronym"/>
			</logic:equal>
		</html:link>
	</logic:present>
	&nbsp;&gt;&nbsp;
	<bean:message key="public.degree.information.label.professionalStatus"  bundle="PUBLIC_DEGREE_INFORMATION" />
</div>

<!-- COURSE NAME -->
<h1>
	<logic:equal name="degree" property="bolonhaDegree" value="true">
		<bean:message bundle="ENUMERATION_RESOURCES" name="degree" property="bolonhaDegreeType.name"/>
	</logic:equal>
	<logic:equal name="degree" property="bolonhaDegree" value="false">
		<bean:message bundle="ENUMERATION_RESOURCES" name="degree" property="tipoCurso.name"/>
	</logic:equal>
	<bean:message bundle="PUBLIC_DEGREE_INFORMATION" key="public.degree.information.label.in"/>
	<logic:present name="inEnglish">
		<logic:equal name="inEnglish" value="false">
			<bean:write name="degree" property="nome"/>
		</logic:equal>
		<logic:equal name="inEnglish" value="true">
			<bean:write name="degree" property="nameEn"/>
		</logic:equal>
	</logic:present>
</h1>

<!-- PROFESSIONAL STATUS SITE -->
<h2 class="greytxt">
	<bean:message bundle="PUBLIC_DEGREE_INFORMATION" key="public.degree.information.label.professionalStatus" />
</h2>

<em><span class="error0"><html:errors/></span></em>

<logic:present name="infoDegreeInfo">	
	<!-- QUALIFICATION LEVEL -->
	<logic:notEmpty name="infoDegreeInfo" property="qualificationLevel">
		<h2 class="arrow_bullet"><bean:message bundle="PUBLIC_DEGREE_INFORMATION" key="public.degree.information.label.qualificationLevel" /></h2>  
		<p><bean:write name="infoDegreeInfo" property="qualificationLevel" filter="false" /></p>
	</logic:notEmpty>
	
	<!-- RECOGNITIONS -->
	<logic:notEmpty name="infoDegreeInfo" property="recognitions">
		<h2 class="arrow_bullet"><bean:message bundle="PUBLIC_DEGREE_INFORMATION" key="public.degree.information.label.recognitions" /></h2>  
		<p><bean:write name="infoDegreeInfo" property="recognitions" filter="false" /></p>
	</logic:notEmpty>
  
	<div class="clear"></div>

	<logic:empty name="infoDegreeInfo" property="qualificationLevel">
	<logic:empty name="infoDegreeInfo" property="recognitions">
		<p><i><bean:message bundle="PUBLIC_DEGREE_INFORMATION" key="not.available" /></i></p>
	</logic:empty>
	</logic:empty>

</logic:present>

<p style="margin-top: 2em;">
	<span class="px10">
		<i>
			<bean:message bundle="PUBLIC_DEGREE_INFORMATION" key="public.degree.information.label.responsability.information.degree" />
		</i>
	</span>
</p>
