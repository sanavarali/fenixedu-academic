<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<span class="error"><html:errors /></span>

<logic:present name="siteView">
<bean:define id="infoSiteSection" name="siteView" property="component"/>
<bean:define id="section" name="infoSiteSection" property="section"/>
<bean:define id="currentSectionCode" name="section" property="idInternal"/>
	
<h2><bean:write name="section" property="name"/></h2>
<table	 cellpadding="0" border="0">
	<tr> 
		<td>
			<div class="gen-button">
				<html:link page="<%= "/editSection.do?method=prepareEditSection&amp;objectCode=" + pageContext.findAttribute("objectCode") + "&amp;currentSectionCode=" + currentSectionCode %>">		
					<bean:message key="button.editSection"/>
				</html:link>
			</div>
		</td>
		<td>
			<div class="gen-button">
				<logic:notEmpty name="section" property="superiorInfoSection">
					<bean:define id="superiorSection" name="section" property="superiorInfoSection"/>
					<bean:define id="superiorSectionCode" name="superiorSection" property="idInternal"/>
					<html:link page="<%= "/deleteSection.do?method=deleteSection&amp;objectCode=" + pageContext.findAttribute("objectCode") + "&amp;currentSectionCode=" + currentSectionCode + "&amp;superiorSectionCode=" + superiorSectionCode %>" onclick="return confirm('Tem a certeza que deseja apagar esta sec��o?')">		
						<bean:message key="button.deleteSection"/>
					</html:link>
				</logic:notEmpty>
				<logic:empty name="section" property="superiorInfoSection">
					<html:link page="<%= "/deleteSection.do?method=deleteSection&amp;objectCode=" + pageContext.findAttribute("objectCode") + "&amp;currentSectionCode=" + currentSectionCode %>" onclick="return confirm('Tem a certeza que deseja apagar esta sec��o?')">		
						<bean:message key="button.deleteSection"/>
					</html:link>
				</logic:empty>
			</div>
		</td>
											
		<td>
			<div class="gen-button">
				<img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="" /> 
				<%-- <bean:define id="sectionCode" name="section" property="idInternal"/> --%>
				<html:link page="<%= "/sectionManagement.do?method=prepareCreateRegularSection&amp;objectCode=" + pageContext.findAttribute("objectCode") + "&amp;currentSectionCode=" + currentSectionCode %>">
					<bean:message key="button.insertSubSection"/>
				</html:link>
			</div>
		</td>
		<td>
			<div class="gen-button">
				<img src="<%= request.getContextPath() %>/images/dotist_post.gif" alt="" /> 
				<html:link page="<%= "/insertItem.do?method=prepareInsertItem&amp;objectCode=" + pageContext.findAttribute("objectCode") + "&amp;currentSectionCode=" + currentSectionCode %>">
					<bean:message key="button.insertItem"/>
				</html:link>
			</div>
		</td>
</tr>
</table>
<br />   
<bean:define id="itemsList" name="infoSiteSection" property="items"/>

<logic:iterate id="item" name="itemsList">
<bean:define id="itemCode" name="item" property="idInternal"/>
<h4><bean:write name="item" property="name"/></h4>
    <logic:equal name="item" property="urgent" value="true"><font color="red"></logic:equal> 		
  	<bean:write name="item" property="information" filter="false" />
  	<logic:equal name="item" property="urgent" value="true"></font></logic:equal>
  	<logic:present name="item" property="links">
  	<br/>
  	<br/>
  		<table>
  			<tr><td class="listClasses-header">
  			Ficheiros:
  			</td><td class="listClasses-header">&nbsp;</td></tr>
		<logic:iterate id="infoLink" name="item" property="links">
		<tr><td class="listClasses">
			<html:link page="<%= "/fileDownload.do?itemCode=" + itemCode %>" paramId="fileName" paramName="infoLink" paramProperty="link" ><bean:write name="infoLink" property="linkName"/></html:link>
		</td>
		<td class="listClasses"><html:link page="<%= "/fileDelete.do?method=deleteFile&objectCode=" + pageContext.findAttribute("objectCode") + "&amp;itemCode=" + itemCode + "&amp;currentSectionCode=" + currentSectionCode%>" paramId="fileName" paramName="infoLink" paramProperty="link" >Apagar Ficheiro</html:link>
		
		</td>
		</tr>
		</logic:iterate>
		</table>
  	</logic:present>
  	
<br />
<br />
<table>
	<tr>			
		<td>
			<div class="gen-button">
				<html:link page="<%= "/editItem.do?method=prepareEditItem&amp;objectCode=" + pageContext.findAttribute("objectCode") + "&amp;itemCode=" + itemCode %>">
					<bean:message key="button.editItem"/>
				</html:link>
			</div>
		</td>
		<td>
			<div class="gen-button">
				<html:link page="<%= "/deleteItem.do?method=deleteItem&amp;objectCode=" + pageContext.findAttribute("objectCode") + "&amp;itemCode=" + itemCode + "&amp;currentSectionCode=" + currentSectionCode%>" onclick="return confirm('Tem a certeza que deseja apagar este item?')">
					<bean:message key="button.deleteItem"/>
				</html:link>
			</div>
		</td>
		<td>
			<div class="gen-button">
				<html:link page="<%="/prepareFileUpload.do?method=prepareFileUpload&objectCode=" + pageContext.findAttribute("objectCode") + "&amp;itemCode=" + itemCode + "&amp;currentSectionCode=" + currentSectionCode%>">
					Inserir Ficheiro
				</html:link>
			</div>
		</td>
	</tr>
</table>  
<br> 
</logic:iterate>
</logic:present>