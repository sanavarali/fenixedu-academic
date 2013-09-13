<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<tiles:insert definition="df.layout.two-column.teacher" beanName="" flush="true">
	<tiles:put name="title" value="private.teacher.management.groups"/>
  <tiles:put name="executionCourseName" value="/teacher/executionCourseName.jsp" />
  <tiles:put name="navGeral" value="/teacher/commonNavGeralTeacher.jsp" />
  <tiles:put name="body" value="/teacher/viewAttendsSet_bd.jsp" />
  <tiles:put name="navLocal" value="/teacher/commons/executionCourseAdministrationNavbar.jsp" type="page"/>
  <tiles:put name="footer" value="/copyright.jsp" />
</tiles:insert>
