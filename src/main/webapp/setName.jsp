<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <title>mysql | ${command} | tableName</title>
    </head>
    <body>
        <c:set var="command" scope="session" value="${command}"/>
        <c:choose>
            <c:when test="${command == 'newDatabase' || command == 'dropDatabase'}">
                <form action="${command}" method="post">
                    Database Name:<br>
                    <input type="text" name="${command}"><br>
                    <br>
                    <input type="submit" value="${command}">
                    </form>
            </c:when>
            <c:when test="${command == 'newTable'}">
                <form action="${command}" method="post">
                    Table Name:<br>
                    <input type="text" name="${command}"><br><br>

                    Columns Names separated by '|'<br>
                    column1|column2|...|columnN :<br>
                    <input type="text" name="columns"><br><br>

                    <input type="submit" value="${command}">
                </form>
            </c:when>
            <c:otherwise>
                <form action="${command}" method="post">
                    Table Name:<br>
                    <input type="text" name="${command}"><br>
                    <br>
                    <input type="submit" value="${command}">
                </form>
            </c:otherwise>
        </c:choose>

        <a href="menu">menu</a> <a href="help">help</a>
    </body>
</html>