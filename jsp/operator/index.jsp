<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<tiles:insert page="/fenixLayout_2col.jsp" flush="true">
  <tiles:put name="title" value=".IST - Operador" />
  <tiles:put name="serviceName" value="Operador" />
  <tiles:put name="navLocal" value="/operator/operatorMainMenu.jsp" />
  <tiles:put name="navGeral" value="/operator/commonNavGeralOperator.jsp" />
  <tiles:put name="body-context" value=""/>  
  <tiles:put name="body" value="/operator/welcomeScreen.jsp" />
  <tiles:put name="footer" value="/operator/copyrightDefault.jsp" />
</tiles:insert>

