<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<body>
    <h1>Список валют</h1>

    <table border="1">
        <tr><th>ID</th><th>Код</th><th>Название</th><th>Символ</th></tr>
        <c:forEach var="currency" items="${currencies}">
            <tr>
                <td>${currency.id}</td>
                <td>${currency.code}</td>
                <td>${currency.fullName}</td>
                <td>${currency.sign}</td>
            </tr>
        </c:forEach>
    </table>

    <table border="2">
            <tr><th>ID</th><th>Базовая валюта</th><th>Целевая валюта</th><th>Курс</th></tr>
            <c:forEach var="rate" items="${rates}">
                <tr>
                    <td>${rate.id}</td>
                    <td>${rate.baseCurrencyId}</td>
                    <td>${rate.targetCurrencyId}</td>
                    <td>${rate.rate}</td>
                </tr>
            </c:forEach>
        </table>


    <h2>Добавить валюту</h2>
    <form method="POST" action="/currencies">
        Код:            <input type="text" name="code"/>  <br/>
        Наименование:   <input type="text" name="fullName"/> <br/>
        Символ:         <input type="text" name="sign"/>  <br/>

        <button type="submit">Добавить</button>
    </form>


    <h2>Посмотреть валюту</h2>

    <input type="text" id="code"/>
    <button onclick="go()">Посмотреть</button>

    <script>
    function go() {
        const code = document.getElementById("code").value;
        window.location.href = "${pageContext.request.contextPath}/currencies/" + code;
    }
    </script>

    <c:if test="${currency != null}">
    <table border="3">
            <tr><th>ID</th><th>Код</th><th>Название</th><th>Символ</th></tr>
                <tr>
                    <td>${currency.id}</td>
                    <td>${currency.code}</td>
                    <td>${currency.fullName}</td>
                    <td>${currency.sign}</td>
                </tr>
        </table>
    <c:if>

    <input type="text" id="code2"/>
    <input type="text" id="code3"/>
    <button onclick="go2()">Посмотреть курс</button>


    <script>
    function go2() {
        const code2 = document.getElementById("code2").value;
        const code3 = document.getElementById("code3").value;

        window.location.href = "${pageContext.request.contextPath}/rates/" + code2 + code3;
    }
    </script>

</body>
</html>