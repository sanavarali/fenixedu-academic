<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<html>
    <head>
    	<title><bean:message key="label.masterDegree.administrativeOffice.payments.receipts.printReceipt.title"/></title>
    </head>

    <body>
     
    <table width="100%" height="100%" border="0">
    <tr height="30"><td>
     <table width="100%" border="0" valign="top">
      <tr> 
        <td height="100" colspan="2">
          <table border="0" width="100%" height="104" align="center" cellpadding="0" cellspacing="0">
            <tr> 
              <td width="50" height="100">
               <img src="<%= request.getContextPath() %>/images/LogoIST.gif" width="50" height="104" border="0"/> 
              </td>
              <td>
                &nbsp;
              </td>
              <td>
                <table border="0" width="100%" height="100%">
                  <tr align="left"> 
                    <td>&nbsp;<b>INSTITUTO SUPERIOR T�CNICO</b><br/>
                      &nbsp;<b>Secretaria da P�s-Gradua��o</b><br/>
                      &nbsp;<b>Centro de Custo 0212</b>
                      <hr size="1">
                    </td>
                  </tr>
                  <tr>
	                <td align="right" valign="top"> <b>Recibo N�: </b> 
                     <bean:write name="receipt" property="number"/>/<bean:write name="receipt" property="year"/><br/>
                     <logic:equal name="receipt" property="receiptsVersionsCount" value="2">
                     	<em>2� Via</em>
                     </logic:equal>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </td>
      </tr>
	</table>

	</td>
	</tr>
    <tr valign="top" >
    <td>

	<table width="100%" border="0">
	 <tr>
	 <td>
      <table width="100%" border="0">
          <tr>
            <td width="20%"><strong>Processo de:</strong></td>
            <td width="80%">&nbsp;</td>
          </tr>
          <tr>
            <td> <bean:message key="label.masterDegree.administrativeOffice.payments.name"/> </td>
            <td> <bean:write name="receipt" property="party.name"/> </td>
          </tr>
<%--           <tr>
            <td> <bean:message key="label.masterDegree.administrativeOffice.payments.address"/> </td>
            <td> <bean:write name="receipt" property="party.address"/> </td>
          </tr>
          <tr>
            <td> <bean:message key="label.masterDegree.administrativeOffice.payments.fiscalCode"/> </td>
	        <td> <bean:write name="receipt" property="party.fiscalCode"/> </td>
          </tr>
--%>         
          <tr>
          </logic:present>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
          </tr>

          <tr> 
            <td width="30%"><strong>Entidade Pagadora:</strong> </td>
            <td width="70%" >&nbsp;</td>
          </tr>
          <tr> 
            <td><bean:message key="label.masterDegree.administrativeOffice.payments.contributorName"/></td>
            <td><bean:write name="receipt" property="contributor.contributorName"/></td>
          </tr>
          <tr> 
			<td><bean:message key="label.masterDegree.administrativeOffice.payments.contributorAddress"/></td>
            <td><bean:write name="receipt" property="contributor.contributorAddress"/></td>
          </tr>
          <tr>
			<td><bean:message key="label.masterDegree.administrativeOffice.payments.contributorNumber"/></td>
            <td><bean:write name="receipt" property="contributor.contributorNumber"/></td>
          </tr>

	  </table>
	 </td>
	 </tr>
	 </table>
	 </td>
	 </tr>
	 
	 <tr>
	 <td> 
	   <table align="right">
	   
	   	<logic:iterate id="entry" name="receipt" property="entries">
		   	<tr>
		   		<td>
        			<bean:message name="entry" property="entryType.name" bundle="ENUMERATION_RESOURCES" />
        			&nbsp;<bean:write name="entry" property="description"/>
        		</td>
        		<td>.........................................</td> &nbsp;
        		<td> <bean:define id="amount" name="entry" property="amount" type="java.math.BigDecimal" /> <%= amount.toPlainString() %> &nbsp;<bean:message key="label.currencySymbol"/></td>
	 		</tr>
	   	</logic:iterate>
        <tr>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
            <td>&nbsp;</td>
        </tr>
    	<tr>
    	  	<td><strong>A liquidar a import�ncia de </strong></td>
   			<td>_____________________</td>&nbsp;
			<td><strong><bean:define id="totalAmount" name="receipt" property="totalAmount" type="java.math.BigDecimal"/><%= totalAmount.toPlainString() %>&nbsp;<bean:message key="label.currencySymbol"/></strong></td>
    	</tr>
	   </table>
	 </td>
	 </tr>	
	 <tr>
	 <td>&nbsp;
	 </td>
	 </tr>
	 <tr>
	 <td>&nbsp;
	 </td>
	 </tr>
	 <tr valign="bottom">
	 <td>
     <table valign="bottom" width="100%" border="0">
       <tr>
         <td>
         	<%= "Lisboa, " + new java.text.SimpleDateFormat("dd MMMM yyyy", request.getLocale()).format(new java.util.Date()) %>
         </td>
       </tr>
       
       <tr>
        <td>&nbsp;</td>
         <td colspan="2" valign="bottom">
           &nbsp;<div align="center">&nbsp;</div>
           <div align="center">&nbsp;</div>
           <div align="center"><b>O Funcion�rio</b> <br>
            <br>
            <br>
           </div>
          <hr align="center" width="300" size="1">
         </td>
       </tr>

	 </table>
	 </td>
	 </tr>
	 
     <tr>	 
	 <td>
		<jsp:include page="./commons/footer.jsp" flush="true" />
    </td>
    </tr>
    </table>
	
    </body>
</html>