<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<body>
    <h1>Список пользователей</h1>

    <table border="1">
        <tr><th>ID</th><th>Имя</th><th>Email</th></tr>
        <c:forEach var="user" items="${users}">
            <tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.email}</td>
            </tr>
        </c:forEach>
    </table>

    <h2>Добавить пользователя</h2>
    <form method="POST" action="/users">
        Имя:   <input type="text" name="name"/>  <br/>
        Email: <input type="text" name="email"/> <br/>
        <button type="submit">Добавить</button>
    </form>
</body>
</html>