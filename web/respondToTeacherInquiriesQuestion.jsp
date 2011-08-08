<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>
<html:html xhtml="true">
	<head>
		<title>
			<bean:message key="dot.title" bundle="GLOBAL_RESOURCES" /> - <bean:message key="message.inquiries.title" bundle="INQUIRIES_RESOURCES"/>
		</title>

		<link href="<%= request.getContextPath() %>/CSS/logdotist.css" rel="stylesheet" type="text/css" />

		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
	</head>
	<body>
		<div id="container">
			<div id="dotist_id">
				<img alt="<bean:message key="institution.logo" bundle="IMAGE_RESOURCES" />"
						src="<bean:message key="dot.logo" bundle="GLOBAL_RESOURCES" arg0="<%= request.getContextPath() %>"/>" />
			</div>
			<div id="txt">
				<h1><bean:message key="message.inquiries.title" bundle="INQUIRIES_RESOURCES"/></h1>
				<div class="mtop1">
					<!--<bean:message key="" bundle="INQUIRIES_RESOURCES"/>-->
					<p>
						Caro(a) aluno(a)
						<br/><br/>
						Est� a decorrer a fase de recolha dos Inqu�ritos aos Alunos relativa aos QUC  do 2� semestre de 2010/2011. Muitos alunos j� procederam ao seu preenchimento. No entanto, devido a um erro t�cnico, verificou-se uma falha na informa��o recolhida. Torna-se pois necess�rio proceder � repeti��o da recolha de alguns dos inqu�ritos.
						<br/><br/>
						Ciente do inc�modo causado, venho por este meio apelar para o preenchimento dos inqu�ritos, mesmo naqueles casos em que j� ter�o sido preenchidos anteriormente. Dado que as respostas s�o an�nimas, como � garantido no regulamento dos QUC, n�o � poss�vel a correc��o individual do inqu�rito a preencher.
						<br/><br/>
						Com as melhores sauda��es acad�micas,
						<br/><br/>
						Conselho Pedag�gico	
					</p>
				</div>
			</div>
			<br />

			<div align="center">
				<table>
					<tr>
						<td>
							<form method="post" action="<%= request.getContextPath() %>/respondToInquiriesQuestion.do">
								<html:hidden property="method" value="respondNow"/>
								<html:hidden property="contentContextPath_PATH" value="/estudante/estudante"/>
								<html:submit bundle="HTMLALT_RESOURCES" altKey="inquiries.respond.now" property="ok">
									<bean:message key="button.inquiries.respond.now" />
								</html:submit>
							</form>
						</td>
						<td>
							<form method="post" action="<%= request.getContextPath() %>/respondToInquiriesQuestion.do">
								<html:hidden property="method" value="registerStudentResponseRespondLater"/>
								<html:hidden property="contentContextPath_PATH" value="/comunicacao/comunicacao"/>
								<html:submit bundle="HTMLALT_RESOURCES" altKey="inquiries.respond.later" property="ok">
									<bean:message key="button.inquiries.respond.later" />
								</html:submit>
							</form>
						</td>
					</tr>
				</table>
			</div>

		</div>
	</body>
</html:html>