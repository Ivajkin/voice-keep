<!DOCTYPE html>
<html>
<head>
  <#include "header.ftl">
</head>

<body>

  <#include "nav.ftl">

<div class="container">

  <h1>Формируем склад</h1>
    <ul>
    <#list results as x>
      <li> ${x} </li>
    </#list>
    </ul>

    <form method="post" action="/depot">
        <label>Добавить элемент склада</label>
        <input type="text">
        <input type="submit" value="[+]">
    </form>
</div>

</body>
</html>
