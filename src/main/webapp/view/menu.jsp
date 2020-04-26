<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <c:set var="ctx" value="${pageContext.request.contextPath}" />
    <head>
        <title>mysql | menu</title>
        <script type="text/javascript" src="${ctx}/resources/js/jquery-2.1.4.js" ></script>
        <script type="text/javascript" src="${ctx}/resources/js/jquery.tmpl.js" ></script>
        <script type="text/javascript" src="${ctx}/resources/js/menu.js" ></script>
    </head>
    <body>
        <div id="menu">
            <div id="loading">Loading...</div>
                <dl class="container">
                    <script template="row" type="text/x-jquery-tmpl">
                        <a href="{{= $data}}">{{= $data}}</a><br>
                    </script>
                </dl>
            </div>
        </div>
    </body>
</html>